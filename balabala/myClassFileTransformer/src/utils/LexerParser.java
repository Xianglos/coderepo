package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import utils.vo.VariableInfo;

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

        // 文件的绝对路径
        filepath = path;
        // 类名
        while (path.indexOf("\\") > 0) {
            path = path.substring(path.indexOf("\\") + 1);
        }
        classname = path.substring(0, path.indexOf("."));

        // 初始化用户变量
        initUserType();
        // 先找到文件中所有变量
        this.parserVariable();
        // 然后遍历这些变量，确定这些变量的位置、出现的次数
        this.getAllVariableInfo();
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
    public void getAllVariableInfo() {

        for (String var : variable) {
            int linenum = 0;
            int time = 1;
            // 注释块
            boolean isCommnetBlock = false;

            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.filepath));
                String line = reader.readLine();
                while (line != null) {
                    linenum++;

                    // 注释块开始
                    if (line.contains("/*")) {
                        isCommnetBlock = true;
                        line = reader.readLine();
                        continue;
                    }
                    // 注释块结束
                    if (line.contains("*/")) {
                        isCommnetBlock = false;
                    }

                    // 跳过所有空行、注释行、打印log、注释块
                    if (this.isEmpty(line) || line.contains("//") || line.contains("System") || isCommnetBlock) {
                        line = reader.readLine();
                        continue;
                    }

                    // 全字匹配变量名
                    if (this.isWholeWordMached(line, var)) {
                        String regex = "\\b" + var + "\\b";

                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(line);

                        while (matcher.find()) {
                            log("Matched: " + matcher.group());

                            // 添加到变量信息里去
                            VariableInfo varInfo = new VariableInfo();
                            varInfo.setName(var);
                            varInfo.setLinenum(linenum);
                            varInfo.setTime(time++);
                            variableInfo.add(varInfo);
                        }

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
     * 全字匹配字符串(正则)
     * 
     */
    private Boolean isWholeWordMached(String origin, String word) {

        char[] chs = word.toCharArray();
        StringBuffer sb = new StringBuffer();
        sb.append("\\b");
        for (char ch : chs) {
            sb.append("[");
            sb.append(String.valueOf(ch));
            sb.append("]");
        }
        sb.append("\\b");

        String regex = sb.toString();

        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(origin);
            return matcher.find();
        } catch (PatternSyntaxException e) {
            // TODO: handle exception
            log("String:" + origin + "regex:" + regex);
            e.printStackTrace();
            throw e;
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

            // 行号计数从1开始。但这里是0，循环刚开始的会+1
            int linenum = 0;
            // 大括号计数
            int bracketCount = 0;
            // 注释块
            boolean isCommnetBlock = false;
            // 有第一个public了吗
            boolean isfirstPublic = true;

            while (line != null) {
                linenum++;
                log("\nLine:" + linenum + ">>");

                // 注释块开始
                if (line.contains("/*")) {
                    isCommnetBlock = true;
                    line = reader.readLine();
                    continue;
                }
                // 注释块结束
                if (line.contains("*/")) {
                    isCommnetBlock = false;
                }

                // 跳过所有空行、注释行、打印log、注释块、注解
                if (this.isEmpty(line) || line.contains("//") || isCommnetBlock || line.contains("System") || line.contains("@")) {
                    line = reader.readLine();
                    continue;
                }

                // 直至第一个public，才是类体,之前的全部跳过
                if (isfirstPublic) {
                    isfirstPublic = !line.contains("public");
                    line = reader.readLine();
                    continue;
                }

                // 变量只存在于左值，所以如果有等于号的话可以删掉右边的
                if (line.indexOf("=") > 0) {
                    line = line.substring(0, line.indexOf("="));
                }
                // 去掉throw和之后的内容
                if (line.indexOf("throw") > 0) {
                    line = line.substring(0, line.indexOf("throw"));
                }
                // 去掉throw和之后的内容
                if (line.indexOf("return") > 0) {
                    line = line.substring(0, line.indexOf("return"));
                }
                line = line.trim();

                String var = null;
                // 在>2层{}里，是方法体
                if (bracketCount >= 2) {
                    var = this.analyzeMethodBody(line);
                }
                // 在1层{}里，static常量或者方法入参
                else if (bracketCount == 1 || bracketCount == 2) {
                    // 用public/private/protected修饰的变量，行尾应该有封号
                    if (line.contains(";")) {
                        var = this.analyzeMethodBody(line);
                    }
                    // 方法的没入参
                    else if (line.contains("() {")) {
                        // this.analyzeFuncParam(line);
                    }
                    // 方法的入参，也是要检索的对象
                    else {
                        if (line.indexOf("(") > 0) {
                            line = line.substring(line.indexOf("("), line.length());
                        }
                        if (line.indexOf(")") > 0) {
                            line = line.substring(0, line.indexOf(")"));
                        }
                        this.analyzeFuncParam(line);
                    }

                }
                // 很罕见，因为参数列表太长，方法定义的第一行就换行了
                else {
                    if (line.indexOf("(") > 0) {
                        line = line.substring(line.indexOf("("), line.length());
                    }
                    if (line.indexOf(")") > 0) {
                        line = line.substring(0, line.indexOf(")"));
                    }
                    this.analyzeFuncParam(line);
                }

                // 不添加重复变量
                if (var != null && !variable.contains(var)) {
                    log(" " + var);
                    variable.add(var);
                }

                // 方法体应该包含在两层{}之内
                if (line.indexOf("{") > 0) {
                    bracketCount++;
                }
                if (line.indexOf("}") > 0) {
                    bracketCount--;
                }

                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断参数列表里是否有有效定义
     * 
     * 
     */
    private void analyzeFuncParam(String line) {
        // 一行里可能会有多个参数，故分割后再处理
        String[] varArrey = line.split(",");

        // 没参数，回了
        if (varArrey == null || varArrey.length == 0) {
            return;
        }

        for (String vars : varArrey) {

            int typeIndex = -1;
            String vartype = "";
            for (String type : this.userType) {
                if (vars.contains(type)) {
                    typeIndex = vars.indexOf(type);
                    vartype = type;
                    break;
                }
            }
            if (vartype == "") {
                for (String type : this.baseType) {
                    if (vars.contains(type)) {
                        typeIndex = vars.indexOf(type);
                        vartype = type;
                        break;
                    }
                }
            }

            // 有类型定义
            if (vartype != "") {
                String vartmp = "";
                vartmp = vars.substring(typeIndex);
                vartmp = vartmp.substring(vartmp.indexOf(" ") + 1);
                // 最后一个参数
                if (vars.contains(")")) {
                    vartmp = vartmp.substring(0, vartmp.indexOf(")"));
                }

                if (vartmp != null && !variable.contains(vartmp)) {
                    log(" " + vartmp);
                    variable.add(vartmp);
                }

            }
        }

    }

    /**
     * 判断方法体里是否是有效定义
     * 
     * 
     */
    private String analyzeMethodBody(String line) {
        // 判断这行是否有基本类型、用户类型的变量定义
        int typeIndex = -1;
        String vartype = "";
        for (String type : this.userType) {
            if (line.contains(type)) {
                typeIndex = line.indexOf(type);
                vartype = type;
                break;
            }
        }
        if (vartype == "") {
            for (String type : this.baseType) {
                if (line.contains(type)) {
                    typeIndex = line.indexOf(type);
                    vartype = type;
                    break;
                }
            }
        }

        // 有类型定义
        if (vartype != "") {

            // 有效的类型定义
            if (isdefinition(line, vartype)) {
                // 去掉类定义之前的字符
                line = line.substring(typeIndex + vartype.length() + 1);

                // 有效、准确的定义,把变量名挖出来
                if (vartype != "") {
                    // 普通定义的变量
                    String var = "";
                    // 使用泛型的
                    if (line.indexOf(">") > 0) {
                        var = line.substring(line.indexOf(">") + 1, line.indexOf("="));
                    }
                    // 使用数组
                    else if (line.indexOf("]") > 0) {
                        var = line.substring(line.indexOf("]") + 1, line.indexOf("="));
                    }
                    // 带初始化的
                    // else if (line.indexOf("=") > 0) {
                    // var = line.substring(0, line.indexOf("=")).trim();
                    // }
                    // 初始化都不带的
                    else {
                        // 以封号为截结尾的定义
                        if (line.indexOf(";") > 0) {
                            var = line.substring(0, line.indexOf(";"));
                        } else {
                            // 定义了，但是封号换行了
                            var = line;// .substring(0, line.indexOf(" "));
                        }

                    }

                    var = var.trim();
                    if (var.indexOf(" ") > 0) {
                        var = var.substring(var.indexOf(" ") + 1);
                    }

                    return var;

                }

            }
        }

        return null;

    }

    /**
     * 判断是否是有效定义 类型的前一个字符 类型的后一个字符
     * 
     */
    private boolean isdefinition(String line, String vartype) {
        int typeIndex = line.indexOf(vartype);
        String befCh = "";
        if (typeIndex > 0) {
            befCh = line.substring(typeIndex - 1, typeIndex);
        }
        String aftCh = line.substring(typeIndex + vartype.length(), typeIndex + vartype.length() + 1);

        // 排除空的
        if (befCh == null || aftCh == null) {
            return false;
        }
        if (befCh.length() + aftCh.length() == 0) {
            return false;
        }

        // 类型前面不能有new
        if (line.indexOf("new " + vartype) > 0 && line.indexOf("new " + vartype) < typeIndex) {
            return false;
        }
        // 不能throw类型
        if (line.indexOf("throw ") > 0) {
            return false;
        }

        // 泛型 List<String>
        if (befCh.equals("<") && aftCh.equals(">")) {
            return true;
        }
        // 数组 String[]
        if (this.isEmpty(befCh) && "[]".equals(line.substring(typeIndex + vartype.length(), typeIndex + vartype.length() + 2))) {
            return true;
        }
        // 定义时赋值的 String filepath = "D:\\wor
        // if (bef.equals(" ") && (aft.equals(" ") || aft.equals("="))) {
        // return true;
        // }
        // 定义时不赋值的 int testint;
        if (this.isEmpty(befCh) && (aftCh.equals(" ") || aftCh.equals(";"))) {
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
                if (line.contains("public ")) {
                    break;
                }

                // 只有import的,才需要判断。
                if (line.contains("import ")) {
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
                // log(line);
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
                log(Character.toString(ch));
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
        builder.append("LexerParser [");
        builder.append("filepath=");
        builder.append(filepath);
        builder.append(",\n classname=");
        builder.append(classname);
        builder.append(",\n baseType=");
        builder.append(baseType);
        builder.append(",\n userType=");
        builder.append(userType);
        builder.append(",\n variable=");
        builder.append(variable);
        builder.append(",\n variableInfo=\n");
        for (VariableInfo varInfo : variableInfo) {
            builder.append("              ");
            builder.append(varInfo.toString());
            builder.append("\n");
        }
        builder.append("\n]");
        return builder.toString();
    }

    /**
     * 打罗格
     */
    private void log(String log) {
        if (false) {
        // if (true) {
            System.out.print(log);
        }

    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public List<String> getBaseType() {
        return baseType;
    }

    public void setBaseType(List<String> baseType) {
        this.baseType = baseType;
    }

    public List<String> getUserType() {
        return userType;
    }

    public void setUserType(List<String> userType) {
        this.userType = userType;
    }

    public List<String> getVariable() {
        return variable;
    }

    public void setVariable(List<String> variable) {
        this.variable = variable;
    }

    public void setVariableInfo(List<VariableInfo> variableInfo) {
        this.variableInfo = variableInfo;
    }

    public List<VariableInfo> getVariableInfo() {
        return variableInfo;
    }
}