package readTXT;

import java.util.ArrayList;
import java.util.List;

//����Project
public class UProject {

	private List<UClass> uClass = new ArrayList<UClass>();

	//�������ʲô�ࡢʲô�������ڼ��С���ʲô����log
	public void printLogs() {

		for (UClass singleClass : getuClass()) {

			System.out.println("�� " + singleClass.getuClassName() + " ��,");
			for (UFunc singleFunc : singleClass.getuFunc()) {

				System.out.println("   "+singleFunc.getuFuncName() + " ����,");
				for (UFormatLog singleLog : singleFunc.getuFormatLog()) {

					System.out.print("     "+singleLog.getLinenum() + " ��,");
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
