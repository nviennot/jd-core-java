package jd.ide.intellij;

public class JavaDecompiler {
    static {
        String path = "";
        try {
            path = JavaDecompiler.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            path = java.net.URLDecoder.decode(path, "UTF-8");
            path = new java.io.File(path).getParent();
            System.load(path + "/libjd-intellij" + getLibraryExtension());
        } catch (Exception e) {
            throw new IllegalStateException("Something got wrong when loading the Java Decompiler native lib at " + path);
        }
    }

    private static String getLibraryExtension() {
        String platform = System.getProperty("os.name").toLowerCase();
        if(isWindows(platform)) {
            return ".dll";
        } else if(isMac(platform)) {
            return ".jnilib";
        } else if(isLinux(platform)) {
            return ".so";
        } else {
            throw new RuntimeException("Uknown platform.");
        }
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
