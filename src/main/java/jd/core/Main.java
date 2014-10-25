package jd.core;

import jd.ide.intellij.config.JDPluginComponent;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String HELP = "Usage: java -jar jd-core-java.jar [options] <compiled.jar> [<out_dir>]]\n" +
            "Options:\n" +
            "    -z - save sources into *.sct.zip file;\n" +
            "    -n - add line numbers into sources; (false by default)\n" +
            "    -r - not realign line numbers (true by default)\n";

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.err.println(HELP);
                return;
            }

            List<String> paths = new ArrayList<String>();
            for(String arg: args) {
                if(arg.startsWith("-")) {
                    processArgs(arg);
                } else {
                    paths.add(arg);
                }
            }

            if(paths.isEmpty()) {
                throw new IllegalArgumentException("Nothing is found to decomplile.");
            }

            String jarPath = paths.get(0);

            String outDirPath = paths.size() > 1 ? paths.get(1) : null;

            int numDecompiled = new Decompiler().decompileToDir(jarPath, outDirPath);
            System.err.println("Decompiled " + numDecompiled + " classes");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void processArgs(String arg) {
        if("-".equals(arg)) {
            throw new IllegalArgumentException("Illegal argument: '-'");
        }

        for(Character ch : arg.substring(1).toCharArray()) {
            switch(ch) {
                case 'z':
                    JDPluginComponent.CONF.setSaveToZip(true);
                    break;

                case 'n':
                    JDPluginComponent.CONF.setShowLineNumbersEnabled(true);
                    break;

                case 'r':
                    JDPluginComponent.CONF.setRealignLineNumbersEnabled(false);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown argument: '" + ch + "'");
            }
        }
    }
}
