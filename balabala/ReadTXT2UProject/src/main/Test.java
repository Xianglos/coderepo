package main;

import readTXT.ReadTXT2UProject;
import readTXT.UProject;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		log("Main Start");

		String filePath = "D:\\workspace\\java\\readTXT\\src\\resource\\classlog.properties";

		ReadTXT2UProject util = new ReadTXT2UProject();
		UProject uproj=util.runme(filePath);
		uproj.printLogs();

		log("Main End");
	}

	public static void log(String log) {
		System.out.println(log);
	}

}
