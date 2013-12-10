package jd.core;

import java.io.File;
import java.io.IOException;

public class FileSystem {

    // -1 = unset, 0 = false, 1 = true;
    private static int isCaseSensitive = -1;

    public static boolean isCaseSensitive() throws IOException {
        if (isCaseSensitive != -1) {
            return isCaseSensitive == 1;
        }
        if (testCaseSensitivity()) {
            isCaseSensitive = 1;
            return true;
        } else {
            isCaseSensitive = 0;
            return false;
        }
    }

    private static boolean testCaseSensitivity() throws IOException {
        File tempFile = File.createTempFile("jd-core-java", null);
        String upperCaseName = tempFile.getAbsolutePath().toUpperCase();
        if (new File(upperCaseName).exists()) {
            return false;
        } else {
            return true;
        }
    }
}
