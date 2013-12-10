package jd.core;

import java.util.HashSet;
import java.util.Set;

public class CaseInsensitiveFilePathSet {

    Set<String> set = new HashSet<String>();

    public void add(String path) {
        set.add(path.toLowerCase());
    }

    public boolean containsIgnoreCase(String path) {
        return set.contains(path.toLowerCase());
    }

    public String getNumberedName(String path) {
        int lastComma = path.lastIndexOf('.');
        String withoutExt = path.substring(0, lastComma);
        String ext = path.substring(lastComma + 1);
        for (int i = 2; i < 100; i++) {
            String newName = withoutExt + "." + i + "." + ext;
            if (!this.containsIgnoreCase(newName)) {
                return newName;
            }
        }
        // give up after 100 trials...
        return path;
    }
}
