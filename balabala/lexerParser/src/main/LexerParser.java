package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.vo.VariableInfo;

/**
 * Java版词法分析器 只为了获取类定义 只为了获取变量
 * 
 */
public class LexerParser {

    /**
     * 指定了文件 初始化了基本类型、用户自己声明的类型
     * 
     */
    public LexerParser(String path) {
        // 除了基本类型意以外，把用户类型也加入到type中
        initBaseType();

        filepath = path;

        while (path.indexOf("\\") > 0) {
            path = path.substring(path.indexOf("\\") + 1);
        }

        classname = path.substring(0, path.indexOf(".") - 1);
        initUserType();
    }

    /** 指定的一个文件 */
    public String filepath;

    /** 类名 */
    public String classname;

    /** java中一些基础的类型 */
    public List<String> baseType = new ArrayList<String>();

    /** 用户自己声明的类型 */
    public List<String> userType = new ArrayList<String>();

    /** 所有变量清单 */
    public List<String> variable = new ArrayList<String>();

    /** 变量的信息 */
    public List<VariableInfo> variableInfo = new ArrayList<VariableInfo>();

    /**
     * 先找到文件中所有变量 然后遍历行，确定这些变量的位置、出现的次数
     * 
     */
    public void getVariableInfo() {
        // 先找到文件中所有变量
        this.parserVariable();

        for (String var : variable) {
            int linenum = 0;
            int time = 1;

            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.filepath));
                String line = reader.readLine();
                while (line != null) {
                    linenum++;

                    // 跳过所有空行、注释行、打印log
                    if (this.isEmpty(line) || line.contains("//") || line.contains("System")) {
                        line = reader.readLine();
                        continue;
                    }

                    if (line.contains(var)) {

                        VariableInfo varInfo = new VariableInfo();
                        varInfo.setName(var);
                        varInfo.setLinenum(linenum);
                        varInfo.setTime(time++);
                        variableInfo.add(varInfo);
                    }

                    line = reader.readLine();
                }

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 找到java文件中所有的变量
     * 
     */
    public void parserVariable() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.filepath));
            String line = reader.readLine();

            int linenum = 0;
            boolean isfirstPublic = true;

            while (line != null) {
                linenum++;

                // 跳过所有空行、注释行、打印log
                if (this.isEmpty(line) || line.contains("//") || line.contains("System")) {
                    line = reader.readLine();
                    continue;
                }

                // 直至第一个public，才是类体,之前的全部跳过
                if (isfirstPublic) {
                    isfirstPublic = !line.contains("public");
                    line = reader.readLine();
                    continue;
                }

                // 跳过方法声明那一行
                // 特征：这行最后一个字符是{
                if (line.length() == line.indexOf("{") + 1) {
                    line = reader.readLine();
                    continue;
                }

                // 判断这行是否有基本类型、用户类型的变量定义
                int isVarDefined = -1;
                String vartype = "";
                for (String type : this.baseType) {
                    if (line.contains(type)) {
                        isVarDefined = line.indexOf(type);
                        vartype = type;
                        break;
                    }
                }
                if (vartype == "") {
                    for (String type : this.userType) {
                        if (line.contains(type)) {

                            isVarDefined = line.indexOf(type);
                            vartype = type;
                            break;
                        }
                    }
                }

                // 有类型定义
                if (vartype != "") {

                    // 有效的类型定义
                    if (isdefinition(line, vartype)) {

                        // 有效、准确的定义,把变量名挖出来
                        if (vartype != "") {
                            // 普通定义的变量
                            String vars = "";// line.substring(isVarDefined + vartype.length());
                            // 使用泛型的
                            if (line.indexOf(">") > 0) {
                                vars = line.substring(line.indexOf(">") + 1, line.indexOf("="));
                            }
                            // 使用数组
                            else if (line.indexOf("]") > 0) {
                                vars = line.substring(line.indexOf("]") + 1, line.indexOf("="));
                            }
                            // 带初始化的
                            else if (line.indexOf("=") > 0) {
                                vars = line.substring(isVarDefined, line.indexOf("=")).trim();
                            }
                            // 初始化都不带的
                            else {
                                vars = line.substring(isVarDefined, line.indexOf(";"));
                            }

                            vars = vars.trim();
                            if (vars.indexOf(" ") > 0) {
                                vars = vars.substring(vars.indexOf(" ") + 1);
                            }

                            // 不添加重复变量
                            if (!variable.contains(vars)) {
                                variable.add(vars);
                            }

                        }

                    }
                }

                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断是否是有效定义 类型的前一个字符 类型的后一个字符
     * 
     */
    private boolean isdefinition(String line, String vartype) {
        int isVarDefined = line.indexOf(vartype);
        String bef = line.substring(isVarDefined - 1, isVarDefined);
        String aft = line.substring(isVarDefined - 1, isVarDefined);

        // 排除空的
        if (bef == null || aft == null) {
            return false;
        }
        if (bef.length() + aft.length() == 0) {
            return false;
        }

        // 类型前面不能有new
        if (line.indexOf("new " + vartype) > 0 && line.indexOf("new " + vartype) < isVarDefined) {
            return false;
        }

        // 泛型 List<String>
        if (bef.equals("<") && aft.equals(">")) {
            return true;
        }
        // 数组 String[]
        if (bef.equals(" ") && aft.equals("[")) {
            return true;
        }
        // 定义时赋值的 String filepath = "D:\\wor
        if (bef.equals(" ") && (aft.equals(" ") || aft.equals("="))) {
            return true;
        }
        // 定义时不赋值的 int testint;
        if (bef.equals(" ") && (aft.equals(" ") || aft.equals(";"))) {
            return true;
        }

        return false;
    }

    /**
     * 初始化用户类型 使用初始化时配置的路径 读取那些import的类型
     * 
     */
    public void initUserType() {
        StringBuffer context = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.filepath));
            String line = reader.readLine();
            int linenum = 1;
            while (line != null) {

                // 跳过空白行
                if (this.isEmpty(line)) {
                    // 下一行
                    line = reader.readLine();
                    continue;
                }

                // 用户指定类需要import，必定在类声明之前
                if (line.contains("public")) {
                    break;
                }

                // 只有import的,才需要判断。
                if (line.contains("import")) {
                    // 删除最后一个[.]以前的内容，删除最后一个;
                    line = line.replaceAll(".+[.]", "").replace(";", "");
                    // 添加到用户类型里头
                    this.userType.add(line);
                }

                // 下一行
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断一个字符串是否是空白行 只有空格、tab 空白行：true
     */
    private boolean isEmpty(String line) {

        // String转字符数组
        char[] charArray = line.toCharArray();

        for (char ch : charArray) {
            int ascii = (int) ch;
            // 空格 32，制表符，9
            if (!(ascii == 32 || ascii == 9)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 把一个文本文件，按行读取成一个Sting字符串 使用初始化时配置的路径
     */
    public String readFileToString() {
        return readFileToString(this.filepath);
    }

    /**
     * 把一个文本文件，按行读取成一个Sting字符串 需要输入完整的文件路径
     */
    private String readFileToString(String path) {
        StringBuffer context = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while (line != null) {
                context.append(line);
                // System.out.println(line);
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return context.toString();
    }

    /**
     * 按字符读取文本文件 需要输入完整的文件路径
     * 
     */
    public void readFileByChar(String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            int ch;
            while ((ch = reader.read()) != -1) {
                // ch is each character in your file.
                System.out.println(Character.toString(ch));
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化的时候，把一些类型先加入到type中，包括： java的8个基本类型 java.lang中的所有类型 暂时排除了System
     */
    private void initBaseType() {
        // Java的基本数据类型: byte、short、int、long、float、double、char和boolean
        baseType.add("byte");
        baseType.add("short");
        baseType.add("int");
        baseType.add("long");
        baseType.add("float");
        baseType.add("double");
        baseType.add("char");
        baseType.add("boolean");
        // java.lang.*
        baseType.add("AbstractMethodError");
        baseType.add("AbstractStringBuilder");
        baseType.add("Appendable");
        baseType.add("ApplicationShutdownHooks");
        baseType.add("ArithmeticException");
        baseType.add("ArrayIndexOutOfBoundsException");
        baseType.add("ArrayStoreException");
        baseType.add("AssertionError");
        baseType.add("AssertionStatusDirectives");
        baseType.add("AutoCloseable");
        baseType.add("Boolean");
        baseType.add("BootstrapMethodError");
        baseType.add("Byte");
        baseType.add("Character");
        baseType.add("CharacterData");
        baseType.add("CharacterData00");
        baseType.add("CharacterData01");
        baseType.add("CharacterData02");
        baseType.add("CharacterData03");
        baseType.add("CharacterData0E");
        baseType.add("CharacterDataLatin1");
        baseType.add("CharacterDataPrivateUse");
        baseType.add("CharacterDataUndefined");
        baseType.add("CharacterName");
        baseType.add("CharSequence");
        baseType.add("Class");
        baseType.add("ClassCastException");
        baseType.add("ClassCircularityError");
        baseType.add("ClassFormatError");
        baseType.add("ClassLoader");
        baseType.add("ClassNotFoundException");
        baseType.add("ClassValue");
        baseType.add("Cloneable");
        baseType.add("CloneNotSupportedException");
        baseType.add("Comparable");
        baseType.add("Compiler");
        baseType.add("CompoundEnumeration");
        baseType.add("ConditionalSpecialCasing");
        baseType.add("Deprecated");
        baseType.add("Double");
        baseType.add("Enum");
        baseType.add("EnumConstantNotPresentException");
        baseType.add("Error");
        baseType.add("Exception");
        baseType.add("ExceptionInInitializerError");
        baseType.add("FdLibm");
        baseType.add("Float");
        baseType.add("FunctionalInterface");
        baseType.add("IllegalAccessError");
        baseType.add("IllegalAccessException");
        baseType.add("IllegalArgumentException");
        baseType.add("IllegalCallerException");
        baseType.add("IllegalMonitorStateException");
        baseType.add("IllegalStateException");
        baseType.add("IllegalThreadStateException");
        baseType.add("IncompatibleClassChangeError");
        baseType.add("IndexOutOfBoundsException");
        baseType.add("InheritableThreadLocal");
        baseType.add("InstantiationError");
        baseType.add("InstantiationException");
        baseType.add("Integer");
        baseType.add("InternalError");
        baseType.add("InterruptedException");
        baseType.add("Iterable");
        baseType.add("LayerInstantiationException");
        baseType.add("LinkageError");
        baseType.add("LiveStackFrame");
        baseType.add("LiveStackFrameInfo");
        baseType.add("Long");
        baseType.add("Math");
        baseType.add("Module");
        baseType.add("ModuleLayer");
        baseType.add("NamedPackage");
        baseType.add("NegativeArraySizeException");
        baseType.add("NoClassDefFoundError");
        baseType.add("NoSuchFieldError");
        baseType.add("NoSuchFieldException");
        baseType.add("NoSuchMethodError");
        baseType.add("NoSuchMethodException");
        baseType.add("NullPointerException");
        baseType.add("Number");
        baseType.add("NumberFormatException");
        baseType.add("Object");
        baseType.add("OutOfMemoryError");
        baseType.add("Override");
        baseType.add("Package");
        baseType.add("Process");
        baseType.add("ProcessBuilder");
        baseType.add("ProcessEnvironment");
        baseType.add("ProcessHandle");
        baseType.add("ProcessHandleImpl");
        baseType.add("ProcessImpl");
        baseType.add("PublicMethods");
        baseType.add("Readable");
        baseType.add("Record");
        baseType.add("ReflectiveOperationException");
        baseType.add("Runnable");
        baseType.add("Runtime");
        baseType.add("RuntimeException");
        baseType.add("RuntimePermission");
        baseType.add("SafeVarargs");
        baseType.add("SecurityException");
        baseType.add("SecurityManager");
        baseType.add("Short");
        baseType.add("Shutdown");
        baseType.add("StackFrameInfo");
        baseType.add("StackOverflowError");
        baseType.add("StackStreamFactory");
        baseType.add("StackTraceElement");
        baseType.add("StackWalker");
        baseType.add("StrictMath");
        baseType.add("String");
        baseType.add("StringBuffer");
        baseType.add("StringBuilder");
        baseType.add("StringCoding");
        baseType.add("StringConcatHelper");
        baseType.add("StringIndexOutOfBoundsException");
        baseType.add("StringLatin1");
        baseType.add("StringUTF16");
        baseType.add("SuppressWarnings");
        // baseType.add("System");
        baseType.add("Terminator");
        baseType.add("Thread");
        baseType.add("ThreadDeath");
        baseType.add("ThreadGroup");
        baseType.add("ThreadLocal");
        baseType.add("Throwable");
        baseType.add("TypeNotPresentException");
        baseType.add("UnknownError");
        baseType.add("UnsatisfiedLinkError");
        baseType.add("UnsupportedClassVersionError");
        baseType.add("UnsupportedOperationException");
        baseType.add("VerifyError");
        baseType.add("VersionProps");
        baseType.add("VirtualMachineError");
        baseType.add("Void");
        baseType.add("WeakPairMap");

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LexerParser [filepath=");
        builder.append(filepath);
        builder.append(",\n classname=");
        builder.append(classname);
        builder.append(",\n baseType=");
        builder.append(baseType);
        builder.append(",\n userType=");
        builder.append(userType);
        builder.append(",\n variable=");
        builder.append(variable);
        builder.append(",\n variableInfo=");
        builder.append(variableInfo);
        builder.append("]");
        return builder.toString();
    }

}
