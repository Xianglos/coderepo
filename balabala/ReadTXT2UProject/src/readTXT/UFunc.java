package readTXT;

import java.util.ArrayList;
import java.util.List;

//单个方法
public class UFunc {

	public UFunc(String funcName) {
		if (!(funcName == null || funcName.equals("") || funcName.equals(" "))) {
			uFuncName = funcName.substring(1, funcName.length());
		}

	}

	// 自身方法名
	private String uFuncName;

	// log的行数,文本,对象名
	private List<UFormatLog> uFormatLog = new ArrayList<UFormatLog>();

	public String getuFuncName() {
		return uFuncName;
	}

	public void setuFuncName(String uFuncName) {
		this.uFuncName = uFuncName;
	}

	public List<UFormatLog> getuFormatLog() {
		return uFormatLog;
	}

	public void setuFormatLog(List<UFormatLog> uFormatLog) {
		this.uFormatLog = uFormatLog;
	}

	@Override
	public String toString() {
		return "UFunc [uFuncName=" + uFuncName + ", uFormatLog=" + uFormatLog + "]";
	}

}
