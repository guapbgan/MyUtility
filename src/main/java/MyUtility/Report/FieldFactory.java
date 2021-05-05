package MyUtility.Report;

import MyUtility.Report.Exception.FieldFactoryException;
import MyUtility.Sheet.SheetUtility;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.math.MathContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class FieldFactory {
    public interface FieldCallback{
        void run(String fieldId, FieldFactory.Field field);
    }

    public static class Field {
        public final int COLUMN_INDEX;
        public final String TITLE;
        public final String DB_FIELD;
        public final Class TYPE;

        private <T> Field(int fieldCount, String title, String dbField, Class<T> type){
            this.COLUMN_INDEX = fieldCount;
            this.TITLE = title;
            this.DB_FIELD = dbField;
            this.TYPE = type;
        }
    }

    private int fieldCount = 0;

    public <T> Field createField(String title, String dbField, Class<T> type){
        return new Field(fieldCount++, title, dbField, type);
    }

    public static void writeNewSheetRowWithData(Map<String, Field> fieldMap, SXSSFSheet sheet, ResultSet mainResultSet, MathContext mathContext, Map<String, Object> specialDataMap) throws SQLException {
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        writeSheetRowWithData(fieldMap, row, mainResultSet, mathContext, specialDataMap);
    }

    public static void writeSheetRowWithData(Map<String, FieldFactory.Field> fieldMap, Row row, ResultSet mainResultSet, MathContext mathContext, Map<String, Object> specialDataMap) throws SQLException {
        for (Map.Entry<String, FieldFactory.Field> entry : fieldMap.entrySet()) {
            String fieldId = entry.getKey();
            FieldFactory.Field field = entry.getValue();

            if (field.DB_FIELD == null && specialDataMap != null && specialDataMap.containsKey(fieldId) && specialDataMap.get(fieldId) != null) {
                if (field.TYPE == String.class) {
                    SheetUtility.setStringValue(row, field.COLUMN_INDEX, (String) specialDataMap.get(fieldId));
                } else if (field.TYPE == Double.class) {
                    SheetUtility.setDoubleValue(row, field.COLUMN_INDEX, (Double) specialDataMap.get(fieldId));
                } else {
                    //basically not possible
                    throw new FieldFactoryException("Type is not support");
                }
            }else if(mainResultSet.getObject(field.DB_FIELD) != null){
                if (field.TYPE == String.class) {
                    SheetUtility.setStringValue(row, field.COLUMN_INDEX, mainResultSet.getString(field.DB_FIELD));
                } else if (field.TYPE == Double.class) {
                    SheetUtility.setDoubleValue(
                            row,
                            field.COLUMN_INDEX,
                            mathContext == null?
                                    mainResultSet.getBigDecimal(field.DB_FIELD).stripTrailingZeros().doubleValue():
                                    mainResultSet.getBigDecimal(field.DB_FIELD).round(mathContext).stripTrailingZeros().doubleValue()
                    );
                } else {
                    //basically not possible
                    throw new FieldFactoryException("Type is not support");
                }
            }
        }
    }

    public static void loopThroughFields(Map<String, FieldFactory.Field> fieldMap, FieldCallback fieldCallback) {
        for(Map.Entry<String, FieldFactory.Field> entry: fieldMap.entrySet()){
            fieldCallback.run(entry.getKey(), entry.getValue());
        }
    }


}

