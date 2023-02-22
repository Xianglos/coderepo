package bci;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import utils.ReadTXT2UProject;
import utils.UClass;
import utils.UFormatLog;
import utils.UFunc;
import utils.UProject;

/**
 * 字节码转换器
 * 
 * 
 */
public class MyClassFileTransformer4 implements ClassFileTransformer {

	/**
	 * 字节码加载到虚拟机前会进入这个方法
	 */
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		// log("className:" + className);

		String filePath = "D:\\workspace\\java\\myClassFileTransformer\\src\\resource\\classlog.properties";

		ReadTXT2UProject util = new ReadTXT2UProject();
		UProject uproj = util.runme(filePath);
		// uproj.printLogs();

		for (UClass singleClass : uproj.getuClass()) {
			// 如果加载Numm类才拦截
			if (!singleClass.getuClassName().equals(className)) {
				continue;
			}

			// javassist的包名是用点分割的，需要转换下
			if (className != null && className.indexOf("/") != -1) {
				className = className.replaceAll("/", ".");
				log("transform:Get it:" + className);
			}

			for (UFunc singleFunc : singleClass.getuFunc()) {

				try {
					// 通过包名获取类文件
					ClassPool pool = ClassPool.getDefault();
					ClassClassPath classPath = new ClassClassPath(this.getClass());
					pool.insertClassPath(classPath);
					CtClass ccSomeCode = pool.get(className);

					//获得指定方法名的方法
					CtMethod funcName = ccSomeCode.getDeclaredMethod(singleFunc.getuFuncName());
					// setNum.insertBefore("{ log(\"Before:\"+num); }");
					//int linenum = setNum.insertAt(20, "{ System.out.println(\"Numm:trans insertAt:\"+$1); }");
					//log("transform:linenum:" + linenum);

					// 新建一个打log的方法
					CtMethod logFunc = CtNewMethod.make("private void log(Object obj) {System.out.println(obj);}",
							ccSomeCode);
					ccSomeCode.addMethod(logFunc);

					// 变量proj里的方法名，插入log
					// 在方法执行前插入代码
					funcName.insertBefore("{ log(\"" + className + ":Start\"); }");
					funcName.insertAfter("{ log(\"" + className + ":End\"); }");
					//runme.insertAfter("{ return false; }");

					// 在第8行开始的地方插入，如果这行只有一个{，那么会插入到下一行
					//runme.insertAt(8, "{ log(\"runme:num Bef\"+num.getNum()); }");
					insertLogsByLine(singleFunc.getuFormatLog(), funcName);

					log("transform:Done.");

					// Compile
					return ccSomeCode.toBytecode();

				} catch (NotFoundException e) {
					log("NotFoundException\n" + e.getStackTrace());
				} catch (CannotCompileException e) {
					log("CannotCompileException\n" + e.getStackTrace());
					//	} catch (IOException e) {
					// log("IOException\n" + e.getStackTrace());
				} catch (Exception e) {
					log("Exception\n" + e.getStackTrace());
				}

			}
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
	 * print log to console
	 * 
	 * @param options
	 * @param ins
	 */
	private void log(String str) {
		System.out.println(str);
	}

	/**
	 * 追加一些log 针对方法，在指定行插入log，行数是相对于整个文件的
	 */
	private boolean insertLogsByLine(List<UFormatLog> formatLogList, CtMethod ctMethod) throws Exception {

		try {
			for (UFormatLog formatLog : formatLogList) {
				// .insertAt(8, "{ log(\"runme:num Bef\"+num.getNum()); }");
				StringBuffer logCode = new StringBuffer();
				// 有log、有ObjName
				if (formatLog.fullSize) {
					logCode.append("{ System.out.println(");
					logCode.append("\"" + formatLog.getLog() + ":\"");
					logCode.append("+" + formatLog.getObjName());
					logCode.append("); }");
				} else {
					// 只有log
					if (formatLog.getLog() != null && "" != formatLog.getLog() && " " != formatLog.getLog()) {
						logCode.append("{ System.out.println(\"" + formatLog.getLog() + "\"); }");
					} else if (formatLog.getObjName() != null && "" != formatLog.getObjName()
							&& " " != formatLog.getObjName()) {
						// 只有objName
						logCode.append("{ System.out.println(" + formatLog.getObjName() + "); }");
					} else {
						// 只有行号
						continue;
					}

				}

				ctMethod.insertAt(formatLog.getLinenum(), logCode.toString());
			}
		} catch (CannotCompileException e) {
			throw e;
		}

		return true;

	}
}
