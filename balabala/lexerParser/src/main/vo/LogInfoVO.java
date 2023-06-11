package main.vo;

/** 需要打印的log信息 */
public class LogInfoVO {

    /** 类名 */
    public String className;

    /** 变量名 */
    public String variable;

    /** 出现次数 */
    public int time;

    /** 行号 */
    public int linenum;

    /** 日志内容 */
    public String context;

    /** 备注 */
    public String remark;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setTime(String time) {
        if (time != null && !"".equals(time) && !" ".equals(time)) {
            this.time = Integer.valueOf(time);
        } else if (time.contains(".")) {
            this.time = (int) Float.parseFloat(time);
        } else {
            this.time = 0;
        }
    }

    public int getLinenum() {
        return linenum;
    }

    public void setLinenum(int linenum) {
        this.linenum = linenum;
    }

    public void setLinenum(String linenum) {
        if (linenum != null && !"".equals(linenum) && !" ".equals(linenum)) {
            this.linenum = Integer.valueOf(linenum);
        } else if (linenum.contains(".")) {
            this.linenum = (int) Float.parseFloat(linenum);
        } else {
            this.linenum = 0;
        }
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LogInfoVO [className=");
        builder.append(className);
        builder.append(", variable=");
        builder.append(variable);
        builder.append(", time=");
        builder.append(time);
        builder.append(", linenum=");
        builder.append(linenum);
        builder.append(", context=");
        builder.append(context);
        builder.append(", remark=");
        builder.append(remark);
        builder.append("]");
        return builder.toString();
    }

}
