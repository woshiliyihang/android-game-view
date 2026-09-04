#pragma once

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_com_example_llama_LlamaJni_nativeCreate(
        JNIEnv * env, jclass clazz, jstring model_path, jint context_size,
        jint threads, jint gpu_layers);

JNIEXPORT jstring JNICALL Java_com_example_llama_LlamaJni_nativeGenerate(
        JNIEnv * env, jclass clazz, jlong handle, jstring prompt,
        jint max_tokens, jfloat temperature);

JNIEXPORT void JNICALL Java_com_example_llama_LlamaJni_nativeFree(
        JNIEnv * env, jclass clazz, jlong handle);

#ifdef __cplusplus
}
#endif
