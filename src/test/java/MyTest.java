import MyUtility.Sheet.SheetUtility;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

public class MyTest {
    @Test
    public void isNullTest() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        row.createCell(0);
        Assert.assertTrue(SheetUtility.isNullCell(sheet, 0, 1));
        Assert.assertTrue(SheetUtility.isNullCell(sheet, 1, 1));
        Assert.assertFalse(SheetUtility.isNullCell(sheet, 0, 0));
        Assert.assertFalse(SheetUtility.isNullCell(sheet, 0, 0));
        Assert.assertTrue(SheetUtility.isNullCell(row, 1));
        Assert.assertFalse(SheetUtility.isNullCell(row, 0));
    }
}
