package readTXT;

import java.util.ArrayList;
import java.util.List;

//单个Class
public class UClass {

	public UClass(String className) {
		uClassName = className;
	}

	// 自身类名
	private String uClassName;

	// 方法名
	private List<UFunc> uFunc = new ArrayList<UFunc>();

	public String getuClassName() {
		return uClassName;
	}

	public void setuClassName(String uClassName) {
		this.uClassName = uClassName;
	}

	public List<UFunc> getuFunc() {
		return uFunc;
	}

	public void setuFunc(List<UFunc> uFunc) {
		this.uFunc = uFunc;
	}

	@Override
	public String toString() {
		return "UClass [uClassName=" + uClassName + ", uFunc=" + uFunc + "]";
	}

}
