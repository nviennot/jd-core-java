package jd.core;

import java.io.*;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
        Map<String, ByteArrayOutputStream> pathToSrc = decompile(jarPath);

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

    public int decompileToDir(String outDir, Map<String, ByteArrayOutputStream> pathToSrc) throws IOException {
        for (Map.Entry<String, ByteArrayOutputStream> entry : pathToSrc.entrySet()) {
            String fileName = entry.getKey();
            File file = new File(outDir, fileName);
            file.getParentFile().mkdirs();
            
            ByteArrayOutputStream os = entry.getValue();

            FileOutputStream out = new FileOutputStream(file);
			os.writeTo(out);
			out.close();

        }
        return pathToSrc.size();
    }

    public int decompileToZip(String zipName, Map<String, ByteArrayOutputStream> entries) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipName));

        for(Map.Entry<String, ByteArrayOutputStream> entry : entries.entrySet()) {
            zipOut.putNextEntry(new ZipEntry(entry.getKey()));

            ByteArrayOutputStream os = entry.getValue();
            
            os.writeTo(zipOut);

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

    public Map<String, ByteArrayOutputStream> decompile(String jarPath) throws DecompilerException, IOException {
    	ZipFile zFile = new ZipFile(jarPath);
        ZipInputStream zip = new ZipInputStream(new FileInputStream(jarPath));
        ZipEntry ze;
        Map<String, ByteArrayOutputStream> pathToSrc = new HashMap<String, ByteArrayOutputStream>();
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
                    
                    String sourceCode = decompiler.decompile(jarPath, classPath);
                    
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    os.write(sourceCode.getBytes());
                    
                    pathToSrc.put(javaPath, os);
                }
            } else if( !ze.isDirectory() ) {
                
                InputStream is = zFile.getInputStream(ze);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                
                int i;                
                while ((i = is.read()) != -1)
                {
                	os.write(i);
                }
                
                pathToSrc.put(ze.getName(), os);
            }
        }

        return pathToSrc;
    }

    private boolean validContent(String decompiled) {
        return decompiled != null && !decompiled.matches("(?sm)class\\s*\\{\\s*\\}.*");
    }
}
