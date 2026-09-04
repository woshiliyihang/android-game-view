#include "LlamaJni.h"

#include "llama.h"

#include <algorithm>
#include <mutex>
#include <string>
#include <vector>

namespace {

struct Session {
    llama_model * model = nullptr;
    llama_context * context = nullptr;
    llama_sampler * sampler = nullptr;
    const llama_vocab * vocab = nullptr;
    std::mutex mutex;
};

void throw_illegal_state(JNIEnv * env, const std::string & message) {
    jclass exception = env->FindClass("java/lang/IllegalStateException");
    if (exception != nullptr) {
        env->ThrowNew(exception, message.c_str());
    }
}

std::string to_string(JNIEnv * env, jstring value) {
    if (value == nullptr) {
        return {};
    }
    const char * chars = env->GetStringUTFChars(value, nullptr);
    if (chars == nullptr) {
        return {};
    }
    std::string result(chars);
    env->ReleaseStringUTFChars(value, chars);
    return result;
}

std::string token_to_piece(const llama_vocab * vocab, llama_token token) {
    std::string piece(128, '\0');
    int32_t length = llama_token_to_piece(vocab, token, piece.data(), piece.size(), 0, true);
    if (length < 0) {
        piece.resize(static_cast<size_t>(-length));
        length = llama_token_to_piece(vocab, token, piece.data(), piece.size(), 0, true);
    }
    if (length <= 0) {
        return {};
    }
    piece.resize(static_cast<size_t>(length));
    return piece;
}

void free_session(Session * session) {
    if (session == nullptr) {
        return;
    }
    if (session->sampler != nullptr) {
        llama_sampler_free(session->sampler);
    }
    if (session->context != nullptr) {
        llama_free(session->context);
    }
    if (session->model != nullptr) {
        llama_model_free(session->model);
    }
    delete session;
}

} // namespace

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_llama_LlamaJni_nativeCreate(JNIEnv * env, jclass, jstring model_path,
                                              jint context_size, jint threads, jint gpu_layers) {
    const std::string path = to_string(env, model_path);
    if (path.empty()) {
        throw_illegal_state(env, "model path is empty");
        return 0;
    }

    llama_backend_init();

    auto * session = new Session();
    llama_model_params model_params = llama_model_default_params();
    model_params.n_gpu_layers = std::max(0, static_cast<int>(gpu_layers));
    session->model = llama_model_load_from_file(path.c_str(), model_params);
    if (session->model == nullptr) {
        free_session(session);
        throw_illegal_state(env, "failed to load GGUF model");
        return 0;
    }

    llama_context_params context_params = llama_context_default_params();
    context_params.n_ctx = std::max<uint32_t>(256, static_cast<uint32_t>(context_size));
    context_params.n_batch = std::min<uint32_t>(context_params.n_ctx, 512);
    context_params.n_threads = std::max(1, static_cast<int>(threads));
    context_params.n_threads_batch = context_params.n_threads;
    session->context = llama_init_from_model(session->model, context_params);
    if (session->context == nullptr) {
        free_session(session);
        throw_illegal_state(env, "failed to create llama context");
        return 0;
    }

    llama_sampler_chain_params sampler_params = llama_sampler_chain_default_params();
    session->sampler = llama_sampler_chain_init(sampler_params);
    llama_sampler_chain_add(session->sampler, llama_sampler_init_top_k(40));
    llama_sampler_chain_add(session->sampler, llama_sampler_init_top_p(0.95f, 1));
    llama_sampler_chain_add(session->sampler, llama_sampler_init_temp(0.7f));
    llama_sampler_chain_add(session->sampler, llama_sampler_init_dist(LLAMA_DEFAULT_SEED));
    session->vocab = llama_model_get_vocab(session->model);
    return reinterpret_cast<jlong>(session);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_llama_LlamaJni_nativeGenerate(JNIEnv * env, jclass, jlong handle,
                                                jstring prompt, jint max_tokens, jfloat temperature) {
    auto * session = reinterpret_cast<Session *>(handle);
    if (session == nullptr || session->context == nullptr || session->vocab == nullptr) {
        throw_illegal_state(env, "invalid llama session");
        return nullptr;
    }
    const std::string prompt_text = to_string(env, prompt);
    if (prompt_text.empty()) {
        throw_illegal_state(env, "prompt is empty");
        return nullptr;
    }

    std::lock_guard<std::mutex> lock(session->mutex);
    const int32_t token_capacity = std::max<int32_t>(32, static_cast<int32_t>(prompt_text.size()) + 8);
    std::vector<llama_token> prompt_tokens(static_cast<size_t>(token_capacity));
    int32_t token_count = llama_tokenize(session->vocab, prompt_text.c_str(), prompt_text.size(),
                                         prompt_tokens.data(), token_capacity, true, false);
    if (token_count < 0) {
        prompt_tokens.resize(static_cast<size_t>(-token_count));
        token_count = llama_tokenize(session->vocab, prompt_text.c_str(), prompt_text.size(),
                                     prompt_tokens.data(), -token_count, true, false);
    }
    if (token_count <= 0) {
        throw_illegal_state(env, "failed to tokenize prompt");
        return nullptr;
    }
    prompt_tokens.resize(static_cast<size_t>(token_count));

    llama_memory_clear(llama_get_memory(session->context), true);
    llama_batch batch = llama_batch_get_one(prompt_tokens.data(), token_count);
    if (llama_decode(session->context, batch) != 0) {
        throw_illegal_state(env, "prompt decode failed");
        return nullptr;
    }

    std::string output;
    llama_token next = llama_sampler_sample(session->sampler, session->context, -1);
    const int32_t limit = std::max(1, static_cast<int>(max_tokens));
    for (int32_t i = 0; i < limit && !llama_vocab_is_eog(session->vocab, next); ++i) {
        output += token_to_piece(session->vocab, next);
        llama_sampler_accept(session->sampler, next);
        batch = llama_batch_get_one(&next, 1);
        if (llama_decode(session->context, batch) != 0) {
            throw_illegal_state(env, "token decode failed");
            return nullptr;
        }
        next = llama_sampler_sample(session->sampler, session->context, -1);
    }
    return env->NewStringUTF(output.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_llama_LlamaJni_nativeFree(JNIEnv *, jclass, jlong handle) {
    free_session(reinterpret_cast<Session *>(handle));
}
