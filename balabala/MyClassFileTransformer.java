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
import javassist.NotFoundException;

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
			CtClass cc = pool.get(className);

			// 获得指定方法名的方法
			CtMethod m = cc.getDeclaredMethod("setNum");

			// 在方法执行前插入代码
			m.insertBefore("{ System.out.println(\"Before:\"+num); }");
			m.insertAfter("{ System.out.println(\"After:\"+num); }");

			System.out.println("Done.");

			return cc.toBytecode();

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
	 * 需要在MANIFEST.MF中指定Premain-Class
	 * @param options
	 * @param ins
	 */
	public static void premain(String options, Instrumentation ins) {
		// 注册我自己的字节码转换器
		ins.addTransformer(new MyClassFileTransformer());
	}
}
