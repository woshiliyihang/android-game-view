package com.example.llama;

public final class LlamaJni implements AutoCloseable {
    static {
        System.loadLibrary("llama-jni");
    }

    private long nativeHandle;

    public LlamaJni(String modelPath, int contextSize, int threads, int gpuLayers) {
        nativeHandle = nativeCreate(modelPath, contextSize, threads, gpuLayers);
        if (nativeHandle == 0) {
            throw new IllegalStateException("native llama session was not created");
        }
    }

    public synchronized String generate(String prompt, int maxTokens, float temperature) {
        if (nativeHandle == 0) {
            throw new IllegalStateException("llama session is closed");
        }
        return nativeGenerate(nativeHandle, prompt, maxTokens, temperature);
    }

    @Override
    public synchronized void close() {
        if (nativeHandle != 0) {
            nativeFree(nativeHandle);
            nativeHandle = 0;
        }
    }

    private static native long nativeCreate(String modelPath, int contextSize, int threads, int gpuLayers);
    private static native String nativeGenerate(long handle, String prompt, int maxTokens, float temperature);
    private static native void nativeFree(long handle);
}
