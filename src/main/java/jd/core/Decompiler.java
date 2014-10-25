package jd.core;

import java.io.*;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.zip.ZipOutputStream;

import jd.ide.intellij.JavaDecompiler;
import jd.ide.intellij.config.JDPluginComponent;

public class Decompiler {
    private JavaDecompiler decompiler;

    public Decompiler() {
        decompiler = new JavaDecompiler();
    }

    public String decompile(String jarPath, String internalClassName) throws DecompilerException {
        String decompiled = decompiler.decompile(jarPath, internalClassName);
        if (!validContent(decompiled))
            throw new DecompilerException("cannot decompile " + jarPath + "!" +  internalClassName);
        return decompiled;
    }

    public int decompileToDir(String jarPath, String outDir) throws DecompilerException, IOException {
        String name = jarPath.substring(0, jarPath.lastIndexOf(".jar"));

        Map<String, String> pathToSrc = decompile(jarPath);

        if(JDPluginComponent.CONF.isSaveToZip()) {
            return decompileToZip(name, pathToSrc);
        } else {
            return decompileToDir(outDir == null ? name : outDir, pathToSrc);
        }
    }

    private int decompileToDir(String outDir, Map<String, String> pathToSrc) throws FileNotFoundException {
        for (Map.Entry<String, String> entry : pathToSrc.entrySet()) {
            String fileName = entry.getKey();
            File file = new File(outDir, fileName);
            file.getParentFile().mkdirs();
            PrintWriter out = new PrintWriter(file);
            out.print(entry.getValue());
            out.close();
        }
        return pathToSrc.size();
    }

    public int decompileToZip(String name, Map<String, String> entries) throws DecompilerException, IOException {
        String zipName = name + ".src.zip";

        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipName));
        PrintWriter printOut = new PrintWriter(zipOut);

        for(Map.Entry<String, String> entry : entries.entrySet()) {
            zipOut.putNextEntry(new ZipEntry(entry.getKey()));
            printOut.print(entry.getValue());
            printOut.flush();
            zipOut.closeEntry();
        }

        zipOut.finish();
        zipOut.close();

        return entries.size();
    }

    public Map<String, String> decompile(String jarPath) throws DecompilerException, IOException {
        ZipInputStream zip = new ZipInputStream(new FileInputStream(jarPath));
        ZipEntry ze;
        Map<String, String> pathToSrc = new HashMap<String, String>();
        CaseInsensitiveFilePathSet caseInsensitiveSet = new CaseInsensitiveFilePathSet();

        Scanner in = new Scanner(zip);
        while ((ze = zip.getNextEntry()) != null ) {
            String entryName = ze.getName();
            if (entryName.endsWith(".class")) {
                String classPath = entryName.replaceAll("\\$.*\\.class$", ".class");
                String javaPath = classPath.replaceAll("\\.class$", ".java");
                if (!pathToSrc.containsKey(javaPath)) {
                    // If file system is not case sensitive, we should case insensitive check as well.
                    // Otherwise, existing file is overwritten by another file with the same name and different case.
                    if (!FileSystem.isCaseSensitive()) {
                        if (caseInsensitiveSet.containsIgnoreCase(javaPath)) {
                            javaPath = caseInsensitiveSet.getNumberedName(javaPath);
                        }
                    }
                    caseInsensitiveSet.add(javaPath);
                    pathToSrc.put(javaPath, decompiler.decompile(jarPath, classPath));
                }
            } else if( !ze.isDirectory() ) {
                StringBuilder entry = new StringBuilder();
                while(in.hasNextLine()) {
                    entry.append(in.nextLine()).append('\n');
                }
                pathToSrc.put(ze.getName(), entry.toString());
            }
        }

        return pathToSrc;
    }

    private boolean validContent(String decompiled) {
        return decompiled != null && !decompiled.matches("(?sm)class\\s*\\{\\s*\\}.*");
    }
}
