package jd.ide.intellij;

import com.jd.util.NativeUtils;
import java.io.InputStream;

public class JavaDecompiler {
    static {
        try {
            NativeUtils.loadLibraryFromJar(getLibraryPath());
        } catch(Exception e) {
            throw new RuntimeException("Failed to load the native library", e);
        }
    }

    private static String getLibraryPath() {
        String path = "/%s/%s/%s.%s";
        String nativelibPath = "/META-INF/nativelib";
        String ideRunningNativelibPath = "./jd-intellij/src/main/native/nativelib/";

        String os, arch, ext, base;

        arch = getArch();

        String platform = System.getProperty("os.name").toLowerCase();
        if(isWindows(platform)) {
            os = "win32";
            base = "jd-intellij";
            ext = "dll";
        } else if(isMac(platform)) {
            os = "macosx";
            base = "libjd-intellij";
            ext = "jnilib";
        } else if(isLinux(platform)) {
            os = "linux";
            base = "libjd-intellij";
            ext = "so";
        } else {
            throw new RuntimeException("Uknown platform.");
        }

        path = String.format(path, os, arch, base, ext);

        nativelibPath += path;
        ideRunningNativelibPath += path;

        InputStream is = NativeUtils.class.getResourceAsStream(nativelibPath);
        if (is != null) {
            return nativelibPath;
        } else {
            return ideRunningNativelibPath;
        }
    }

    private static String getArch() {
        String arch = System.getProperty("os.arch");
        if("x86".equals(arch) || "i386".equals(arch)) {
            return "x86";
        } else if("amd64".equals(arch) || "x86_64".equals(arch)) {
            return "x86_64";
        }

        throw new RuntimeException("Unknown architecture, found " + arch);
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
