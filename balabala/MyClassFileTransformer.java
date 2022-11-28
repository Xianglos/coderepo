package bci;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/**
 * 字节码转换器
 * 
 * 
 */
public class MyClassFileTransformer implements ClassFileTransformer {

	/**
	 * 字节码加载到虚拟机前会进入这个方法
	 */
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		// System.out.println(className);
		// 如果加载Numm类才拦截
		if (!"bci/Numm".equals(className)) {
			return null;
		}

		// javassist的包名是用点分割的，需要转换下
		if (className != null && className.indexOf("/") != -1) {
			className = className.replaceAll("/", ".");
			System.out.println("Get it:" + className);
		}
		try {
			// 通过包名获取类文件
			ClassPool pool = ClassPool.getDefault();
			ClassClassPath classPath = new ClassClassPath(this.getClass());
			pool.insertClassPath(classPath);
			CtClass ccNumm = pool.get(className);

//			// 获得指定方法名的方法
//			CtMethod m = cc.getDeclaredMethod("setNum");
//
//			// 在方法执行前插入代码
//			m.insertBefore("{ System.out.println(\"Before:\"+num); }");
//			m.insertAfter("{ System.out.println(\"After:\"+num); }");
			
			//Compile
			CtMethod m = CtNewMethod.make("public int minus(int input) {return num -= input; }",ccNumm);
			ccNumm.addMethod(m);
			
			CtMethod add = ccNumm.getDeclaredMethod("add");
			add.setBody("{return minus($1);}");

			System.out.println("Done.");

			return ccNumm.toBytecode();

//			//参数里需要加上-Xbootclasspath/a:
//			//TODO
//			ClassPool pool = ClassPool.getDefault();
//			pool.insertClassPath(new ClassClassPath(String.class));
//			CtClass stringCt = pool.get("java.lang.String");
//
//			CtClass[] param = new CtClass[1];
//			param[0] = pool.get("java.lang.Object");
//			CtMethod equals = stringCt.getDeclaredMethod("equals", param);
//
//			equals.setBody("{return true;}");
//			stringCt.toBytecode();
//			
//			return null;

		} catch (NotFoundException e) {
			System.out.println("NotFoundException\n" + e.getStackTrace());
		} catch (CannotCompileException e) {
			System.out.println("CannotCompileException\n" + e.getStackTrace());
		} catch (IOException e) {
			System.out.println("IOException\n" + e.getStackTrace());
		} catch (Exception e) {
			System.out.println("Exception\n" + e.getStackTrace());
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
		ins.addTransformer(new MyClassFileTransformer());
	}
}
