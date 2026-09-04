# Android JNI SDK for llama.cpp

This directory is an Android ARM64 JNI integration kit for the llama.cpp build in this repository. It is intended as a starting point for an Android production application, not as an APK or a complete application module.

## Download

The prebuilt SDK archive is published in the GitHub release:

- https://github.com/woshiliyihang/android-game-view/releases/tag/v0.2-android-jni-sdk

The archive contains:

- ARM64 (`arm64-v8a`) llama.cpp, ggml CPU and OpenCL static libraries
- `libmtmd.a` and the public multimodal headers
- llama.cpp, ggml and generated Adreno OpenCL kernel headers
- `libomp.so`, `libOpenCL.so` and `libc++_shared.so`
- A Java JNI wrapper example
- A CMake template for building `libllama-jni.so`

## Architecture

The sample exposes a small text-generation API:

```text
Kotlin/Java -> LlamaJni.java -> libllama-jni.so -> libllama.a -> ggml OpenCL
```

The wrapper uses `n_gpu_layers` to request GPU offload. On the Redmi K40 Pro, the runtime OpenCL implementation is expected from `/vendor/lib64/libOpenCL.so`; the SDK also includes the OpenCL loader used during the build.

The sample is deliberately file-path based. It does not copy a Java `Bitmap` into native memory. For production VLM support, decode the Android image into tightly packed RGB bytes and call the `mtmd_bitmap_init()` / `mtmd_tokenize()` APIs from `mtmd.h`, or build a dedicated JNI method around those APIs. Keep the language model GGUF and matching `mmproj` GGUF together.

## Integrate into an existing Android app

1. Download and unpack the SDK archive.
2. Copy `prebuilt/arm64-v8a` and `include` into a stable location in the app repository.
3. Copy `java/com/example/llama/LlamaJni.java` into the app's source tree, or rename the package and update the JNI symbol names in `jni/LlamaJni.cpp`.
4. Copy `jni/LlamaJni.cpp`, `include/LlamaJni.h`, and `CMakeLists.txt` into the app's native integration module.
5. In the app module's `build.gradle`, enable CMake and ARM64 only:

```groovy
android {
    defaultConfig {
        ndk {
            abiFilters 'arm64-v8a'
        }
        externalNativeBuild {
            cmake {
                cppFlags '-std=c++17 -fexceptions -frtti'
            }
        }
    }

    externalNativeBuild {
        cmake {
            path file('src/main/cpp/CMakeLists.txt')
            version '3.22.1'
        }
    }
}
```

6. Ensure `libomp.so`, `libOpenCL.so`, and `libc++_shared.so` are placed in `app/src/main/jniLibs/arm64-v8a/` if they are not already supplied by the app.
7. Build the app with `arm64-v8a` and test on the target Qualcomm device.

The supplied CMake file expects this layout relative to itself:

```text
src/main/cpp/
  CMakeLists.txt
  jni/LlamaJni.cpp
  include/LlamaJni.h
  prebuilt/arm64-v8a/include/...
  prebuilt/arm64-v8a/lib/libllama.a
  prebuilt/arm64-v8a/lib/libmtmd.a
  prebuilt/arm64-v8a/lib/libggml*.a
  prebuilt/arm64-v8a/lib/libomp.so
  prebuilt/arm64-v8a/lib/libOpenCL.so
  prebuilt/arm64-v8a/lib/libc++_shared.so
```

## Java usage

Copy the model to app-private storage. Do not hard-code `/sdcard` paths in production; use `Context.getFilesDir()` or a content resolver that copies the model into app-private storage.

```java
try (LlamaJni llama = new LlamaJni(
        new File(getFilesDir(), "models/model.gguf").getAbsolutePath(),
        4096,
        4,
        999)) {
    String answer = llama.generate("Describe the purpose of this app.", 256, 0.7f);
}
```

Recommended initial values for the Redmi K40 Pro:

- `gpuLayers = 999`: request maximum available OpenCL offload
- `contextSize = 2048` or `4096`: lower it if memory pressure occurs
- `threads = 4`: benchmark 4, 6 and 8 on the target firmware
- Start with a Q4_K_M or smaller GGUF model

The actual offload and memory usage must be confirmed from logcat and benchmark results. `999` is a request, not a guarantee.

## VLM integration

The archive includes `libmtmd.a` and headers for native VLM integration. A production VLM JNI method should generally:

1. Load the language model with `llama_model_load_from_file()`.
2. Create `mtmd_context` with `mtmd_init_from_file()` and the matching `mmproj` GGUF.
3. Decode or resize the Android image to RGB bytes.
4. Create an `mtmd_bitmap` with `mtmd_bitmap_init(width, height, rgbBytes)`.
5. Build text and image parts with `mtmd_tokenize()`.
6. Feed the resulting chunks into the llama decode loop and return tokens incrementally to Kotlin/Java.

The current JNI sample implements text generation only, but the packaged `libmtmd.a` and public headers are included to keep the ABI and CMake layout ready for that extension. The `llama-mtmd-cli` in the separate VLM runtime release remains useful for validating a model and `mmproj` pair before integrating it into the app.

## Runtime libraries and OpenCL

Do not package Android system libraries such as `libc.so`, `libm.so` or `libdl.so`. Android supplies those. Package the following application dependencies when needed:

- `libomp.so`
- `libc++_shared.so`
- `libOpenCL.so` loader from this SDK, or the device/vendor-compatible loader strategy approved for the target firmware

On the Redmi K40 Pro, confirm the device-side implementation before release:

```bash
adb shell getprop ro.product.cpu.abi
adb shell ls -l /vendor/lib64/libOpenCL.so
adb logcat | grep -i -E 'opencl|ggml|llama'
```

Do not assume that an OpenCL loader from one Android firmware is interchangeable with another vendor implementation. Test every supported device and firmware family.

## Production checklist

- Use a worker thread or coroutine; never load or decode a model on the main thread.
- Keep one native session per concurrent generation and serialize access to each session.
- Add cancellation through `llama_set_abort_callback()`.
- Stream tokens through a callback or queue instead of returning a large final string.
- Release the native handle in all lifecycle paths, including Activity recreation.
- Copy models into app-private storage and verify free space before loading.
- Record model name, quantization, context size, GPU layers, backend and peak memory in benchmarks.
- Validate `mmproj` compatibility for every VLM model.
- Treat the included JNI wrapper as an example and review error handling, logging, licensing and ABI stability before shipping.
