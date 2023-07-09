package utils.vo;

public class FuncInfo {

    /** 变量名 */
    public String name;

    /** 这次出现时在第几行 */
    public int linenum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLinenum() {
        return linenum;
    }

    public void setLinenum(int linenum) {
        this.linenum = linenum;
    }

    @Override
    public String toString() {
        return "VariableInfo [name=" + name + ", linenum=" + linenum + "]";
    }
}
