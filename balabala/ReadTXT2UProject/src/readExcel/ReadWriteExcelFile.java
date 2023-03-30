import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadWriteExcelFile {

    public static void main(String[] args) throws IOException {
        // Create an object of File class to open xlsx file
        File file = new File("C:\\Users\\Admin\\Desktop\\test.xlsx");

        // Create an object of FileInputStream class to read excel file
        FileInputStream inputStream = new FileInputStream(file);

        // Creating workbook instance that refers to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        // Creating a Sheet object using the sheet name
        XSSFSheet sheet = workbook.getSheet("Sheet1");

        // Get current cell value value and overwrite the value
        Row row = sheet.getRow(0);
        Cell cell = row.getCell(0);
        cell.setCellValue("Overwritten value");

        // Close input stream
        inputStream.close();

        // Create an object of FileOutputStream class to create write data in excel file
        FileOutputStream outputStream = new FileOutputStream(file);

        // write data in the excel file
        workbook.write(outputStream);

        // close output stream
        outputStream.close();
    }
}
