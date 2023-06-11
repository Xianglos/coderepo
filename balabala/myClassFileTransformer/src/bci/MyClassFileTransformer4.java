package bci;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import utils.LexerParser;
import utils.UserLogPropertiesParser;
import utils.vo.LogInfoVO;
import utils.vo.VariableInfo;

/**
 * 字节码转换器
 * 
 * 
 */
public class MyClassFileTransformer4 implements ClassFileTransformer {

    /** 准备就绪进行转换 */
    public static boolean READY_TO_TRANSFORM = false;

    /** 用户配置的需要打印的log */
    private static UserLogPropertiesParser USERLOG = null;

    /** 指定路径下，所有java文件的变量信息 */
    private static List<LexerParser> lexerParserList = null;

    /**
     * 字节码加载到虚拟机前会进入这个方法
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (!READY_TO_TRANSFORM) {
            // 把excel中，用户配置的需要打印的log信息，转化成VO
            String filePath = "D:\\workspace\\java\\myClassFileTransformer\\src\\resource\\test.xlsx";
            convertExcel2VO(filePath);

            // 递归遍历指定文件夹内所有文件
            String folderPath = "D:\\workspace\\java\\myClassFileTransformer\\src\\bci";
            mainRecursion(folderPath);
            // log(USERLOG.toString());

            // 伞兵一号！
            READY_TO_TRANSFORM = true;
        }

        // 用来存要转换的那个类的所有变量信息
        List<VariableInfo> targetClassVarinfo = null;


        // 去掉所有包名，只剩下类名
        String classNameWithoutPkg = className;
        while (classNameWithoutPkg != null && classNameWithoutPkg.indexOf(".") > 0) {
            classNameWithoutPkg = classNameWithoutPkg.substring(classNameWithoutPkg.indexOf(".") + 1, classNameWithoutPkg.length());
        }
        // 遍历已经转换过的java文件，查找是否有对应的类
        for (LexerParser lp : lexerParserList) {
            if (lp.getClassname().equals(classNameWithoutPkg)) {
                targetClassVarinfo = lp.getVariableInfo();
                log("transform:Get it:" + classNameWithoutPkg);
            }
        }

        // 如果没有目标类
        if (targetClassVarinfo == null || targetClassVarinfo.size() == 0) {
            return null;
        }
        // 双捕完成
        // javassist的包名是用点分割的，需要转换下
        if (className != null && className.indexOf("/") != -1) {
            className = className.replaceAll("/", ".");
            log("transform:Get it:" + className);
        }

        try {

            // 获取类、插入log方法，获取这个方法
            ClassPool pool = ClassPool.getDefault();
            ClassClassPath classPath = new ClassClassPath(this.getClass());
            pool.insertClassPath(classPath);
            CtClass ctClass = pool.get(className);

            // 类里的所有方法，插入log的时候要在对应的方法里
            CtMethod[] ctClazz = ctClass.getDeclaredMethods();

            // Test Code//
            // 插入一个打log的方法
            CtMethod logFunc = CtNewMethod.make("private void log(Object obj) {System.out.println(obj);}", ctClass);
            ctClass.addMethod(logFunc);

            // 下面开始添加log
            for (LogInfoVO logInfo : USERLOG.getLogsInfoVOs()) {
                // 对于用户设置的每一个log配置
                // 1.判断是不是当前类的log
                // 如果不是，continue;
                // 2.整理要打印的log
                // 有变量、次数的：找到这个变量对应次数的行数，打印变量所在行的下一行
                // 有行数、内容的：在当前行插入纯内容
                // 有变量、次数、内容的：拼上内容，然后打在下一行
                // 3.执行插入insertAt

                // 1.判断是不是当前类的log
                if (!logInfo.getClassName().equals(classNameWithoutPkg)) {
                    continue;
                }

                // 2.整理要打印的log
                // 最终的log内容
                String logString = "";
                // 最终log要插入的行数
                int logLine = 0;

                if (!isBlank(logInfo.getVariable()) && logInfo.getTime() > 0 && isBlank(logInfo.getContext())) {
                    // 有变量、次数的(没有内容)：找到这个变量对应次数的行数，打印变量所在行的下一行
                    for (VariableInfo allLogInfo : targetClassVarinfo) {
                        // 变量名和行号一致
                        if (allLogInfo.getName().equals(logInfo.getVariable()) && allLogInfo.getTime() == logInfo.getTime()) {
                            logLine = allLogInfo.getLinenum() + 1;
                            logString = logInfo.getVariable();
                            break;
                        }
                    }

                } else if (logInfo.getLinenum() > 0 && !isBlank(logInfo.getContext())) {
                    // 有行数、内容的：在当前行插入纯内容
                    logLine = logInfo.getLinenum();
                    logString = "\"" + logInfo.getContext() + "\"";

                } else if (!isBlank(logInfo.getVariable()) && logInfo.getTime() > 0 && !isBlank(logInfo.getContext())) {
                    // 有变量、次数、内容的：拼上内容，然后打在下一行
                    for (VariableInfo allLogInfo : targetClassVarinfo) {
                        // 变量名和行号一致
                        if (allLogInfo.getName().equals(logInfo.getVariable()) && allLogInfo.getTime() == logInfo.getTime()) {
                            logLine = allLogInfo.getLinenum() + 1;
                            logString = "\"" + logInfo.getContext() + "\"+" + logInfo.getVariable() + "";
                            break;
                        }
                    }

                }

                if (logLine > 0) {
                    log("transform:Log at line:" + logLine + ">> context:" + logString);

                    // 要在对应的方法里才能插入，不是随便第一个地方都可以insert的，也不能insert到别人方法里
                    String funcname = "";
                    for (CtMethod ct : ctClazz) {
                        // 方法体首行
                        if (ct.getMethodInfo().getLineNumber(0) > logLine) {
                            // 跳出循环之前，方法名是上一次循环的
                            break;
                        }
                        // 拿到方法名
                        funcname = ct.getName();
                    }

                    CtMethod ctMethod = ctClass.getDeclaredMethod(funcname);
                    ctMethod.insertAt(logLine, "log(\">>>\"+" + logString + ");");
                    // ctMethod.insertAt(logLine, "System.out.println(\"==============This is it!\");");
                }

            }

            return ctClass.toBytecode();

        } catch (NotFoundException e) {
            log("NotFoundException\n");
            e.printStackTrace();
        } catch (CannotCompileException e) {
            log("CannotCompileException\n");
            e.printStackTrace();
            // } catch (IOException e) {
            // log("IOException\n" + e.getStackTrace());
        } catch (Exception e) {
            log("Exception\n");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 在main函数执行前，执行的函数
     * 
     * @param options
     * @param ins
     */
    public static void premain(String options, Instrumentation ins) {
        // 注册我自己的字节码转换器
        ins.addTransformer(new MyClassFileTransformer4());
    }

    /**
     * 把excel中，用户配置的需要打印的log信息，转化成VO
     * 
     */
    private static void convertExcel2VO(String xlsxPath) {
        // 开始时间
        long startTime = System.currentTimeMillis();

        USERLOG = new UserLogPropertiesParser(xlsxPath);

        // 结束时间
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        // System.out.println("END with:" + timeElapsed + " ms.");

    }

    /**
     * 递归遍历指定文件夹内所有文件
     * 
     */
    private static void mainRecursion(String folderPath) {
        // 开始时间
        long startTime = System.currentTimeMillis();

        lexerParserList = new ArrayList<LexerParser>();

        // 递归遍历指定文文件夹内所有文件
        File folder = new File(folderPath);
        listFilesForFolder(folder);

        // 结束时间
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        // System.out.println("END with:" + timeElapsed + " ms.");

    }

    /**
     * 递归遍历指定文文件夹内所有文件 现在指定后缀名是.java
     * 
     */
    private static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                if (fileEntry.getName().endsWith(".java")) {

                    // System.out.println("\n" + fileEntry.getAbsolutePath());
                    LexerParser parser = new LexerParser(fileEntry.getAbsolutePath());
                    lexerParserList.add(parser);

                }
            }
        }
    }

    /**
     * 判断字符串是否为空 null、""、" "
     * 
     * @param options
     * @param ins
     */
    private static boolean isBlank(String str) {
        if (str == null || "".equals(str) || " ".equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * print log to console
     * 
     * @param options
     * @param ins
     */
    private static void log(String str) {
        System.out.println(str);
    }

}
