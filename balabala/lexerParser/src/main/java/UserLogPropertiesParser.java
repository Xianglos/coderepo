package main.java;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import main.vo.LogInfoVO;

/**
 * 把excel中，用户配置的需要打印的log信息，转化成VO
 * 
 */
public class UserLogPropertiesParser {

    /**
     * 只接受带参构造，需要传入.xlsx的绝对路径
     * 
     */
    public UserLogPropertiesParser(String xlsxPath) {
        this.excelPath = xlsxPath;
        // 把excel里的vo，转换成VO类
        this.parser();

    }

    /** excel文件名 */
    public String excelPath;

    /** 需要打印的log信息 */
    public List<LogInfoVO> logsInfoVOs = new ArrayList<LogInfoVO>();

    /** 把excel里的vo，转换成VO类 */
    public void parser() {

        try {
            // 1.获取文件输入流
            InputStream is = new FileInputStream(this.excelPath);
            // 2.获取工作簿对象
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            // 3.获取工作表对象
            Sheet sheet = workbook.getSheetAt(0);
            // 4.获取行对象
            // Row row = sheet.getRow(0);
            // 5.获取单元格对象
            // Cell cell = row.getCell(0);
            // 6.获取单元格中的值
            // String value = cell.getStringCellValue();

            boolean endFlag = false;

            for (int findClass = 0; findClass < 1024; findClass++) {
                // 4.获取行对象
                Row row = sheet.getRow(findClass);
                // 空行
                if (row == null) {
                    continue;
                }

                // 5.获取单元格对象
                Cell propertiesClass = row.getCell(1);

                // 找到了目标类
                if ("utils.UserLogProperties".equals(propertiesClass.getStringCellValue())) {
                    // VO的有效数据从findClass+2开始

                    for (int rownum = findClass + 3; rownum < 1024; rownum++) {
                        Row paramRow = sheet.getRow(rownum);

                        // 空行
                        if (paramRow == null) {
                            endFlag = true;
                            break;
                        }

                        // 需要打印的log信息
                        DataFormatter dataFormatter = new DataFormatter();

                        LogInfoVO infoVO = new LogInfoVO();
                        infoVO.setClassName(paramRow.getCell(1).getStringCellValue());
                        infoVO.setVariable(paramRow.getCell(2).getStringCellValue());

                        infoVO.setTime(dataFormatter.formatCellValue(paramRow.getCell(3)));
                        infoVO.setLinenum(dataFormatter.formatCellValue(paramRow.getCell(4)));

                        infoVO.setContext(paramRow.getCell(5).getStringCellValue());
                        infoVO.setRemark(paramRow.getCell(6).getStringCellValue());
                        logsInfoVOs.add(infoVO);
                    }

                    // 已经结束咧
                    if (endFlag) {
                        break;
                    }

                }
            }

        } catch (FileNotFoundException e) {
            // 找不到文件
            log(this.excelPath);
            e.printStackTrace();
        } catch (IOException e) {
            // 读写错误
            log(this.excelPath);
            e.printStackTrace();
        }

    }

    public List<LogInfoVO> getLogsInfoVOs() {
        return logsInfoVOs;
    }

    public void setLogsInfoVOs(List<LogInfoVO> logsInfoVOs) {
        this.logsInfoVOs = logsInfoVOs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserLogPropertiesParser [logsInfoVOs=");
        for (LogInfoVO inf : logsInfoVOs) {
            builder.append("\n             ");
            builder.append(inf);
        }
        builder.append("\n]");
        return builder.toString();
    }

    /**
     * 打罗格
     */
    private void log(String log) {
//      if (false) {
        if (true) {
            System.out.print(log);
        }

    }
}
