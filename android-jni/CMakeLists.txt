cmake_minimum_required(VERSION 3.22.1)
project(llama-jni LANGUAGES C CXX)

set(LLAMA_SDK_ROOT "${CMAKE_CURRENT_LIST_DIR}/prebuilt/arm64-v8a")
set(LLAMA_INCLUDE_DIR "${LLAMA_SDK_ROOT}/include")
set(LLAMA_LIB_DIR "${LLAMA_SDK_ROOT}/lib")

add_library(llama STATIC IMPORTED)
set_target_properties(llama PROPERTIES IMPORTED_LOCATION "${LLAMA_LIB_DIR}/libllama.a")

add_library(mtmd STATIC IMPORTED)
set_target_properties(mtmd PROPERTIES IMPORTED_LOCATION "${LLAMA_LIB_DIR}/libmtmd.a")

add_library(ggml STATIC IMPORTED)
set_target_properties(ggml PROPERTIES IMPORTED_LOCATION "${LLAMA_LIB_DIR}/libggml.a")

add_library(ggml-base STATIC IMPORTED)
set_target_properties(ggml-base PROPERTIES IMPORTED_LOCATION "${LLAMA_LIB_DIR}/libggml-base.a")

add_library(ggml-cpu STATIC IMPORTED)
set_target_properties(ggml-cpu PROPERTIES IMPORTED_LOCATION "${LLAMA_LIB_DIR}/libggml-cpu.a")

add_library(ggml-opencl STATIC IMPORTED)
set_target_properties(ggml-opencl PROPERTIES IMPORTED_LOCATION "${LLAMA_LIB_DIR}/libggml-opencl.a")

add_library(llama_jni SHARED
    jni/LlamaJni.cpp
)

target_include_directories(llama_jni PRIVATE
    include
    "${LLAMA_INCLUDE_DIR}"
    "${LLAMA_INCLUDE_DIR}/ggml"
    "${LLAMA_INCLUDE_DIR}/mtmd"
    "${LLAMA_INCLUDE_DIR}/opencl-generated"
)

target_compile_features(llama_jni PRIVATE cxx_std_17)
target_compile_options(llama_jni PRIVATE -fvisibility=hidden)
target_compile_definitions(llama_jni PRIVATE GGML_OPENCL_USE_ADRENO_KERNELS=1)

find_library(log-lib log)
find_library(android-lib android)
find_library(dl-lib dl)
find_library(m-lib m)
find_library(gles-lib GLESv3)

# Keep static libraries in dependency order. mtmd is linked so this target can
# be extended with image input without changing the SDK layout.
target_link_libraries(llama_jni PRIVATE
    mtmd
    llama
    ggml-opencl
    ggml-cpu
    ggml
    ggml-base
    "${LLAMA_LIB_DIR}/libllama-common.a"
    "${LLAMA_LIB_DIR}/libllama-common-base.a"
    "${LLAMA_LIB_DIR}/libomp.so"
    "${LLAMA_LIB_DIR}/libOpenCL.so"
    "${LLAMA_LIB_DIR}/libc++_shared.so"
    ${log-lib}
    ${android-lib}
    ${dl-lib}
    ${m-lib}
    ${gles-lib}
)
