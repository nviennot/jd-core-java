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

    public int decompile(String jarPath, String outPath) throws DecompilerException, IOException {
        Map<String, String> pathToSrc = decompile(jarPath);

        if (outPath == null) {
          outPath = jarPath.replaceAll("\\.jar$", "") + ".src";
            if(JDPluginComponent.CONF.isSaveToZip()) {
              outPath = outPath + ".zip";
            }
        }

        if(JDPluginComponent.CONF.isSaveToZip()) {
            return decompileToZip(outPath, pathToSrc);
        } else {
            return decompileToDir(outPath, pathToSrc);
        }
    }

    public int decompileToDir(String outDir, Map<String, String> pathToSrc) throws FileNotFoundException {
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

    public int decompileToZip(String zipName, Map<String, String> entries) throws IOException {
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

    public String decompileClass(String jarPath, String internalClassName) throws DecompilerException {
        String decompiled = decompiler.decompile(jarPath, internalClassName);
        if (!validContent(decompiled))
            throw new DecompilerException("cannot decompile " + jarPath + "!" +  internalClassName);
        return decompiled;
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
