package main.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Runme {

    public static List<LexerParser> lexerParserList = null;

    public static void main(String[] args) {

        lexerParserList = new ArrayList<LexerParser>();

        // 递归遍历指定文件夹内所有文件
         String folderpath = "D:\\workspace\\java\\myClassFileTransformer\\src";
         //mainRecursion(folderpath);

        // 把excel里的vo，转换成VO类
         String filepath = "D:\\workspace\\java\\myClassFileTransformer\\src\\resource\\test.xlsx";
         convertExcel2VO(filepath);

    }

    public static void convertExcel2VO(String xlsxPath) {
        // 开始时间
        long startTime = System.currentTimeMillis();

        UserLogPropertiesParser parser = new UserLogPropertiesParser(xlsxPath);
        System.out.println(parser);

        // 结束时间
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("END with:" + timeElapsed + " ms.");
    }

    /**
     * 递归遍历指定文件夹内所有文件
     * 
     */
    public static void mainRecursion(String folderPath) {
        // 开始时间
        long startTime = System.currentTimeMillis();

        lexerParserList = new ArrayList<LexerParser>();

        // 递归遍历指定文文件夹内所有文件
        File folder = new File(folderPath);
        listFilesForFolder(folder);

        for (LexerParser lx : lexerParserList) {
            System.out.println(lx.toString());
        }

        // 结束时间
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
                if (fileEntry.getName().endsWith(".java")) {

                    System.out.println("\n" + fileEntry.getAbsolutePath());

                    // TODO Auto-generated method stub

                    LexerParser parser = new LexerParser(fileEntry.getAbsolutePath());
                    lexerParserList.add(parser);

                }
            }
        }
    }

}
