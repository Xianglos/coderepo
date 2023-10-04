package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuperLexerParser extends LexerParser {

	public SuperLexerParser(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 找到java文件中所有的变量
	 * 
	 */
	@Override
	public void parserVariable() {
		// 去掉import及以上的内容，去掉所有注释、注解、空行、制表符
		String allcontext = this.readFileToString();
		// 按照{};分割
		String[] singleCodeArrey = allcontext.split(";|\\{|\\}");

		for (int inx = 0; inx < singleCodeArrey.length; inx++) {
			// 删除等号之后的内容
			// 如果是全局变量的话还要删除
			singleCodeArrey[inx] = singleCodeArrey[inx]
					.replaceAll("[=].*", "")
					.replaceAll(".*[s][t][a][t][i][c]", "")
					.replaceAll(".*[p][u][b][l][i][c]", "")
					.replaceAll(".*[p][r][i][v][a][t][e]", "")
					.replaceAll(".*[p][r][o][t][e][c][t][e][d]", "")
					.trim();
		}

		// declaration
		for (String dec : singleCodeArrey) {
			String[] array = dec.split(" ");
			if (array != null && array.length == 2) {

				// 判断是否是一个类
				boolean isClass = false;
				// import的类型
				for (String type : userType) {
					// 直接声明、声明List、声明数组
					if ((type + " ").equals(array[0] + " ") 
							|| array[0].contains("<" + type + ">")
							|| array[0].contains(type + "[]")) {
						isClass = true;
						break;
					}
				}
				if (!isClass) {
					// 基本类型
					for (String type : baseType) {
						// 直接声明、声明List、声明数组
						if ((type + " ").equals(array[0] + " ") 
								|| array[0].contains("<" + type + ">")
								|| array[0].contains(type + "[]")) {
							isClass = true;
							break;
						}
					}
				}

				// 是一个类，数组第二个元素就是变量名字
				if (isClass && this.isJavaVariable(array[1].trim())) {
					super.variable.add(array[1].trim());
				}

			}
		}

		// 测试检索结果
//		for (String var : super.variable) {
//			System.out.println(">>" + var);
//		}

	}

	/**
	 * 把一个文本文件，按行读取成一个Sting字符串 需要输入完整的文件路径
	 */
	@Override
	public String readFileToString() {
		StringBuilder context = new StringBuilder();
		// 注释块
		boolean isCommnetBlock = false;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(super.filepath));
			String line = reader.readLine();
			while (line != null) {

				// 注释块开始
				if (line.contains("/*")) {
					isCommnetBlock = true;
					line = reader.readLine();
					continue;
				}
				// 注释块结束
				if (line.contains("*/")) {
					isCommnetBlock = false;
					line = reader.readLine();
					continue;
				}

				// 跳过所有空行、注释行、注释块、注解
				if (this.isEmpty(line) 
						|| line.contains("//") 
						|| line.contains("	//") 
						|| isCommnetBlock
						|| line.contains("@") 
						|| line.toString().contains("package")
						|| line.toString().contains("import")) {

					line = reader.readLine();
					continue;
				}

				context.append(line.trim());
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
	 * 判断是否满足java命名规范 大小写字母和数字 0~9:48~57 A~Z:65~90 a~z:97~122 _:95 $:36
	 */
	protected boolean isJavaVariable(String str) {
		String regex = "[a-z]+[a-z0-9A-Z_$]*";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);

		return matcher.find();
	}

}
