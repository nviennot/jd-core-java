package jd.ide.intellij;

import com.jd.util.NativeUtils;

public class JavaDecompiler {
    static {
        try {
            NativeUtils.loadLibraryFromJar(getLibraryPath());
        } catch(Exception e) {
            throw new RuntimeException("Failed to load the native library", e);
        }
    }

    private static String getLibraryPath() {
        // TODO Detect 32/64 bit and load the appropriate library
        String os, arch, ext;

        arch = "x86_64"; // FIXME

        String platform = System.getProperty("os.name").toLowerCase();
        if(isWindows(platform)) {
            os = "win32";
            ext = "dll";
        } else if(isMac(platform)) {
            os = "macosx";
            ext = "jnilib";
        } else if(isLinux(platform)) {
            os = "linux";
            ext = "so";
        } else {
            throw new RuntimeException("Uknown platform.");
        }

        return String.format("/META-INF/nativelib/%s/%s/libjd-intellij.%s", os, arch, ext);
    }

    private static boolean isWindows(String os) {
        return os.indexOf("win") > -1;
    }

    private static boolean isMac(String os) {
        return os.indexOf("mac") > -1;
    }

    private static boolean isLinux(String os) {
        return os.indexOf("linux") > -1;
    }

    public native String decompile(String basePath, String internalClassName);
}
