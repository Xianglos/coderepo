package utils.vo;

public class VariableInfo {

    /** 变量名 */
    public String name;

    /** 第几次出现 */
    public int time;

    /** 这次出现时在第几行 */
    public int linenum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getLinenum() {
        return linenum;
    }

    public void setLinenum(int linenum) {
        this.linenum = linenum;
    }

    @Override
    public String toString() {
        return "VariableInfo [name=" + name + ", time=" + time + ", linenum=" + linenum + "]";
    }

}