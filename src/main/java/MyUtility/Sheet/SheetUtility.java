package MyUtility.Sheet;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class SheetUtility{
    private SheetUtility(){
        //not instantiable
    }
    private static class SheetUtilityException extends RuntimeException{
        final String message;
        public SheetUtilityException(String message){
            this.message = message;
        }
        public String getMessage(){
            return this.message;
        }
    }

    public static Cell locateCell (Sheet sheet, int rowIndex, int columnIndex){
        if(sheet instanceof SXSSFSheet){
            throw new SheetUtilityException("SXSSFSheet is not supported by this method");
        }
        Row row;
        Cell cell;
        row = sheet.getRow(rowIndex);
        if(row == null){
            row = sheet.createRow(rowIndex);
        }
        cell = row.getCell(columnIndex);
        if(cell == null){
            cell = row.createCell(columnIndex);
        }
        return cell;
    }


    public static Cell locateCell(Row row, int columnIndex){
        Cell cell;
        cell = row.getCell(columnIndex);
        if(cell == null){
            cell = row.createCell(columnIndex);
        }
        return cell;
    }

    public static void setCellStyle(Row row, int columnIndex, CellStyle cellStyle){
        locateCell(row, columnIndex).setCellStyle(cellStyle);
    }
    public static void setCellStyle(Sheet sheet, int rowIndex, int columnIndex, CellStyle cellStyle){
        locateCell(sheet, rowIndex, columnIndex).setCellStyle(cellStyle);
    }

    public static void setStringValue(Row row, int columnIndex, String value){
        locateCell(row, columnIndex).setCellValue(value);
    }
    public static void setStringValue(Sheet sheet, int rowIndex, int columnIndex, String value){
        locateCell(sheet, rowIndex, columnIndex).setCellValue(value);
    }

    public static void setIntValue(Row row, int columnIndex, int value){
        locateCell(row, columnIndex).setCellValue(value);
    }
    public static void setIntValue(Sheet sheet, int rowIndex, int columnIndex, int value){
        locateCell(sheet, rowIndex, columnIndex).setCellValue(value);
    }

    public static void setDateValue(Row row, int columnIndex, Date value){
        locateCell(row, columnIndex).setCellValue(value);
    }
    public static void setDateValue(Sheet sheet, int rowIndex, int columnIndex, Date value){
        locateCell(sheet, rowIndex, columnIndex).setCellValue(value);
    }

    public static void setDoubleValue(Row row, int columnIndex, Double value){
        locateCell(row, columnIndex).setCellValue(value);
    }
    public static void setDoubleValue(Sheet sheet, int rowIndex, int columnIndex, Double value){
        locateCell(sheet, rowIndex, columnIndex).setCellValue(value);
    }

    public static void setCalendarValue(Row row, int columnIndex, Calendar value){
        locateCell(row, columnIndex).setCellValue(value);
    }
    public static void setCalendarValue(Sheet sheet, int rowIndex, int columnIndex, Calendar value){
        locateCell(sheet, rowIndex, columnIndex).setCellValue(value);
    }

    public static String getStringValue(Row row, int columnIndex) {
        Cell cell = locateCell(row, columnIndex);
        return generalGetString(cell);
    }
    public static String getStringValue(Sheet sheet,int rowIndex, int columnIndex){
        Cell cell = locateCell(sheet, rowIndex, columnIndex);
        return generalGetString(cell);
    }
    private static String generalGetString(Cell cell) {
        CellType cellType;
        try{
            cellType = cell.getCellType();
        }catch (NoSuchMethodError ex){
            cellType = cell.getCellTypeEnum();
        }

        if (cellType == CellType.FORMULA) {
            return getStringByCellType(cell.getCachedFormulaResultType(), cell);
        }
        return getStringByCellType(cellType, cell);
    }

    public static Integer getIntValue(Row row, int columnIndex){
        Cell cell = locateCell(row, columnIndex);
        return generalGetInteger(cell);
    }
    public static Integer getIntValue(Sheet sheet,int rowIndex, int columnIndex){
        Cell cell = locateCell(sheet, rowIndex, columnIndex);
        return generalGetInteger(cell);
    }
    private static Integer generalGetInteger(Cell cell) {
        CellType cellType;
        try{
            cellType = cell.getCellType();
        }catch (NoSuchMethodError ex){
            cellType = cell.getCellTypeEnum();
        }

        if (cellType == CellType.FORMULA) {
            return getIntegerByCellType(cell.getCachedFormulaResultType(), cell);
        }
        return getIntegerByCellType(cellType, cell);
    }

    public static Double getDoubleValue(Row row, int columnIndex){
        Cell cell = locateCell(row, columnIndex);
        return generalGetDouble(cell);
    }
    public static Double getDoubleValue(Sheet sheet,int rowIndex, int columnIndex){
        Cell cell = locateCell(sheet, rowIndex, columnIndex);
        return generalGetDouble(cell);
    }
    private static Double generalGetDouble(Cell cell) {
        CellType cellType;
        try{
            cellType = cell.getCellType();
        }catch (NoSuchMethodError ex){
            cellType = cell.getCellTypeEnum();
        }

        if (cellType == CellType.FORMULA) {
            return getDoubleByCellType(cell.getCachedFormulaResultType(), cell);
        }
        return getDoubleByCellType(cellType, cell);
    }


    public static Date getDateValue(Row row, int columnIndex){
        Cell cell = locateCell(row, columnIndex);
        return generalGetDate(cell);
    }
    public static Date getDateValue(Sheet sheet,int rowIndex, int columnIndex){
        Cell cell = locateCell(sheet, rowIndex, columnIndex);
        return generalGetDate(cell);
    }
    private static Date generalGetDate(Cell cell) {
        CellType cellType;
        try{
            cellType = cell.getCellType();
        }catch (NoSuchMethodError ex){
            cellType = cell.getCellTypeEnum();
        }

        if (cellType == CellType.FORMULA) {
            return getDateByNumericType(cell.getCachedFormulaResultType(), cell);
        }
        return getDateByNumericType(cellType, cell);
    }


    private static String getStringByCellType(CellType cellType, Cell cell) {
        switch (cellType){
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString().replaceAll("\\.0$", "");
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case ERROR:
                return String.valueOf(cell.getErrorCellValue());
            case BLANK:
            default:
                return "";
        }
    }
    private static int getIntegerByCellType(CellType cellType, Cell cell) {
        switch (cellType){
            case STRING:
                if("".equals(cell.getStringCellValue())){
                    return 0;
                }
                return Integer.parseInt(cell.getStringCellValue());
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue()).intValue();
            case BOOLEAN:
                return cell.getBooleanCellValue()? 1: 0;
            case ERROR:
                throw new SheetUtilityException("CellType of Error can not get integer");
            case BLANK:
                return 0;
            default:
                throw new SheetUtilityException("Unknown CellType");
        }
    }
    private static Double getDoubleByCellType(CellType cellType, Cell cell){
        switch (cellType){
            case STRING:
                if("".equals(cell.getStringCellValue())){
                    return 0d;
                }
                return Double.parseDouble(cell.getStringCellValue());
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue()).doubleValue();
            case BOOLEAN:
                return cell.getBooleanCellValue()? 1d: 0d;
            case ERROR:
                throw new SheetUtilityException("CellType of Error can not get double");
            case BLANK:
                return 0d;
            default:
                throw new SheetUtilityException("Unknown CellType");
        }
    }
    private static Date getDateByNumericType(CellType cellType, Cell cell){
        if(cellType != CellType.NUMERIC){
            throw new SheetUtilityException("Cell Type must be numeric to get date");
        }
        return  cell.getDateCellValue();
    }
}
