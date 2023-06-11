package main.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Runme {

    public static List<LexerParser> lexerParserList = null;

    public static void main(String[] args) {

        lexerParserList = new ArrayList<LexerParser>();

        // 递归遍历指定文件夹内所有文件
        String filePath = "D:\\workspace\\java\\lexerParser\\src\\test\\resources";
        mainRecursion(filePath);

    }

    /**
     * 递归遍历指定文件夹内所有文件
     * 
     */
    public static void mainRecursion(String folderPath) {
        long startTime = System.currentTimeMillis();

        lexerParserList = new ArrayList<LexerParser>();

        // 递归遍历指定文文件夹内所有文件
        File folder = new File(folderPath);
        listFilesForFolder(folder);

        for (LexerParser lx : lexerParserList) {
            System.out.println(lx.toString());
        }

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("END with:" + timeElapsed + " ms.");

    }

    /**
     * 递归遍历指定文文件夹内所有文件 现在指定后缀名是.java
     * 
     */
    public static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                if (fileEntry.getName().endsWith(".jav")) {

                    System.out.println("\n" + fileEntry.getAbsolutePath());

                    // TODO Auto-generated method stub

                    LexerParser parser = new LexerParser(fileEntry.getAbsolutePath());
                    lexerParserList.add(parser);

                }
            }
        }
    }

}
