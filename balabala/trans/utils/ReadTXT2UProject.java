package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ReadTXT2UProject {

	public UProject runme(String filePath) {
		return this.readFile(filePath);
	}

	// 定义一个方法，接收文件路径作为参数
	private UProject readFile(String filePath) {
		// 定义一个BufferedReader对象，用于存储类名、方法名、需要标记的log的信息
		UProject proj = new UProject();
		// 定义一个BufferedReader对象，用于读取文件
		BufferedReader br = null;
		try {
			// 创建一个FileReader对象，用于打开文件
			FileReader fr = new FileReader(filePath);
			// 创建一个BufferedReader对象，用于缓存数据并按行读取
			br = new BufferedReader(fr);
			// 定义一个字符串变量，用于存储每一行的内容
			String line = null;
			// 使用while循环，当读到文件末尾时返回null
			while ((line = br.readLine()) != null) {
				// 跳过空行
				if (line == null || line.equals("") || line.equals(" ")) {
					continue;
				}
				
				// 打印每一行的内容
				//System.out.println(line);

				//把properties文件识别成类名、方法名、log
				classification(line, proj);

			}
		} catch (IOException e) {
			// 处理异常情况
			e.printStackTrace();
		} finally {
			// 关闭资源
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return proj;
	}

	// 区分这一行是类名、方法名、log行数
	// 类名，以【英文字母】开头
	// 方法名，以【.】开头
	// log行数，以【数字】开头
	private void classification(String context, UProject proj) {
		// 截取第一位字符
		char firstCh = context.substring(0, 1).toCharArray()[0];

		// 是【英文字母】，就是读到了类名
		if (Character.isLetter(firstCh)) {
			//新建一个UClass对象，添加到proj
			UClass uClass=new UClass(context);
			proj.getuClass().add(uClass);
		}
		// 是【.】，读到了方法名
		else if ('.'==firstCh) {
			//新建一个UFunc对象，添加到proj
			//暂时不支持在文件里重复写类名，所以当前读到的方法就应该是这个project里最后一个类
			//(更后面的类还没有被读取到
			UClass lastClass=proj.getuClass().get(proj.getuClass().size()-1);
			lastClass.getuFunc().add(new UFunc(context));
		}
		// 是【数字】，读到了log
		else if (Character.isDigit(firstCh)) {
			//新建一个UFormatLog对象，添加到proj
			UFormatLog formatLog=new UFormatLog(context);
			//proj里最后一个类的最后一个方法
			//(更后面的方法还没有被读取到
			UClass lastClass=proj.getuClass().get(proj.getuClass().size()-1);
			UFunc lastFunc=lastClass.getuFunc().get(lastClass.getuFunc().size()-1);
			lastFunc.getuFormatLog().add(formatLog);
		}else {
			//暂时没考虑valdatation
			System.out.println("!!!!!!!!!!!!!!!!Wrong Fomat!!!!!!!!!!!!!!!!!!!");
		}

	}

}
