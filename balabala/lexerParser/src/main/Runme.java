package main.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Runme {

    public static List<LexerParser> lexerParserList = null;

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        lexerParserList = new ArrayList<LexerParser>();

        // 递归遍历指定文文件夹内所有文件
        File folder = new File("D:\\workspace\\java\\lexerParser\\src");
        listFilesForFolder(folder);
        
        
        for(LexerParser lx:lexerParserList) {
            System.out.println(lx.toString());
        }

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("END with:" + timeElapsed + " ms.");

    }

    public static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                if (fileEntry.getName().endsWith(".java")) {

                    // TODO Auto-generated method stub

                    LexerParser parser = new LexerParser(fileEntry.getAbsolutePath());
                    parser.getVariableInfo();

                    lexerParserList.add(parser);

                }
            }
        }
    }

}
