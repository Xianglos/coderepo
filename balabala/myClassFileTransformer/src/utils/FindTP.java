package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FindTP {

	public FindTP(String fn) {
		this.funcName = fn;
		tpPath = null;
		this.run();
	}

	public FindTP() {

	}


	/** 选中的文本 */
	protected String funcName;

	/** TP文件名绝对路径 */
	public String tpPath;

	/**
	 * 安装选中的文本，遍历文件夹，查找指定的方法名 找到对应的TP文件名
	 * 
	 */
	public void run() {

		if (funcName == null || funcName == "") {
			return;
		}

		List<String> javaList = mainRecursion(System.getProperty("user.dir") + "\\src\\", ".jav");
		List<String> exceList = mainRecursion(System.getProperty("user.dir") + "\\src\\", ".xlsx");
		for (String fp : javaList) {
			findTP(fp, this.funcName, exceList);
		}
	}

	/**
	 * 递归遍历指定文件夹内所有文件
	 * 
	 */
	protected List<String> mainRecursion(String folderPath, String suffix) {
		// 开始时间
		long startTime = System.currentTimeMillis();

		// 递归遍历指定文文件夹内所有文件
		File folder = new File(folderPath);
		List<String> resultList = new ArrayList<String>();
		listFilesForFolder(folder, suffix, resultList);

		// 结束时间
		long endTime = System.currentTimeMillis();
		long timeElapsed = endTime - startTime;
		System.out.println("END with:" + timeElapsed + " ms.");

		return resultList;

	}

	/**
	 * 递归遍历指定文文件夹内所有文件 现在指定后缀名是
	 * 
	 */
	protected void listFilesForFolder(final File folder, String suffix, List<String> resultList) {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry, suffix, resultList);
			} else {
				if (fileEntry.getName().endsWith(suffix)) {

					resultList.add(fileEntry.getAbsolutePath());

				}
			}
		}

	}

	/**
	 * 打印文件里的行号和内容 参数是文件绝对路径
	 * 
	 */
	protected void printfile(String filepath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filepath));
			// 行号计数从1开始。但这里是0，循环刚开始的会+1
			int linenum = 0;
			String line = reader.readLine();
			while (line != null) {
				linenum++;
				System.out.println("Line" + linenum + " >>" + line);
				line = reader.readLine();
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 如果有选中文名的方法名，就返回对应的TP文件名 否则返回null
	 * 
	 */
	protected void findTP(String filepath, String funcName, List<String> exceList) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filepath));
			// 行号计数从1开始。但这里是0，循环刚开始的会+1
			int linenum = 0;
			String line = reader.readLine();
			while (line != null) {

				// 这个文件里有指定的方法名字
				if (line.contains(" " + funcName + "(")) {
					// 开始找对应的TP文件名
					// 一般在接下来的数行之内的字符串里
					String tpName = "";

					linenum++;
					line = reader.readLine();
					for (int i = 0; i < 5 && line != null; i++) {

						String[] params = line.split("\",\"");
						for (String p : params) {
							//System.out.println("Line" + linenum + " >>[" + p + "]");
							for (String excel : exceList) {
								// TP_XXXXXX(3).xlsx
								if (excel.contains("TP_") && excel.contains("(" + p + ").xlsx")) {
									this.tpPath = excel;
									return;
								}
							}
						}
						linenum++;
						line = reader.readLine();

					}

				}

				linenum++;
				// System.out.println("Line" + linenum + " >>" + line);
				line = reader.readLine();
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

}
