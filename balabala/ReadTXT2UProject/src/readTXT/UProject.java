package readTXT;

import java.util.ArrayList;
import java.util.List;

//单个Project
public class UProject {

	private List<UClass> uClass = new ArrayList<UClass>();

	//输出，在什么类、什么方法、第几行、打什么样的log
	public void printLogs() {

		for (UClass singleClass : getuClass()) {

			System.out.println("在 " + singleClass.getuClassName() + " 类,");
			for (UFunc singleFunc : singleClass.getuFunc()) {

				System.out.println("   "+singleFunc.getuFuncName() + " 方法,");
				for (UFormatLog singleLog : singleFunc.getuFormatLog()) {

					System.out.print("     "+singleLog.getLinenum() + " 行,");
					System.out.print(singleLog.getLog() + ",");
					System.out.println(singleLog.getObjName() + ".");
				}
			}
		}
	}

	public List<UClass> getuClass() {
		return uClass;
	}

	public void setuClass(List<UClass> uClass) {
		this.uClass = uClass;
	}

	@Override
	public String toString() {
		return "UProject [uClass=" + uClass + "]";
	}

}
