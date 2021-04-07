package MyUtility.SqlBuilder;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;

public class InsertSqlBuilder {
    private String tableName;
    //version 2020/08/11
    private StringBuilder fieldNameBuilder, fieldValueBuilder, whereBuilder;
    private String wholeSql;
    private ArrayList<Object> objectArrayList;
    private ArrayList<String> fieldNameArray;
    private boolean getSqlFlag;
    public InsertSqlBuilder(String tableName){
        this.tableName = tableName;
        this.wholeSql = "INSERT INTO " + tableName + " (%FIELD_NAME%) VALUES (%FIELD_VALUE%) ";
        this.fieldNameBuilder = new StringBuilder();
        this.fieldValueBuilder = new StringBuilder();
        this.whereBuilder = new StringBuilder();
        this.objectArrayList = new ArrayList();
        this.fieldNameArray = new ArrayList();
    }
    public String getSql(){
        String temp = this.wholeSql + whereBuilder.toString();
        temp = temp.replace("%FIELD_NAME%", fieldNameBuilder.toString().substring(0, fieldNameBuilder.length() - 1));
        temp = temp.replace("%FIELD_VALUE%", fieldValueBuilder.toString().substring(0, fieldValueBuilder.length() - 1));
        return temp;
    }
    public String[] getFieldNameArray(){
        return this.fieldNameArray.toArray(new String[0]);
    }
    public void emptyFieldNameArray(){
        this.fieldNameArray = new ArrayList();
    }
    public Object[] getObjectArray(){
        return this.objectArrayList.toArray();
    }
    public void emptyObjectArray(){
        this.objectArrayList = new ArrayList();
    }
    public String getObjectString(){
        String temp = "";
        for(Object object: this.getObjectArray()){
            temp += object.toString() + ", ";
        }
        return temp;
    }
    private void putField(String field) {
        putField(field, false);
    }
    private void putField(String field, Boolean reservedWord){
        this.fieldNameBuilder.append(field).append(",");
        this.fieldNameArray.add(field);
        if(!reservedWord){
            this.fieldValueBuilder.append("?").append(",");
        }
    }
    private void putValue(Object value) {
        putValue(value, false);
    }
    private void putValue(Object value, Boolean reservedWord){
        if(!reservedWord) {
            this.objectArrayList.add(value);
        }else{
            this.fieldValueBuilder.append(value).append(",");
        }
    }
    public void putFieldAndValue(String field, Object value) {
        putField(field, false);
        putValue(value, false);
    }

    public void putFieldAndValue(String field, Object value, Boolean reservedWord){
        putField(field, reservedWord);
        putValue(value, reservedWord);
    }

    public void putWhere(String whereCondition){
        this.whereBuilder.append(whereCondition);
    }

    public int update(JdbcTemplate jt){
        return jt.update(this.getSql(), this.getObjectArray());
    }

    public String getTableName() {
        return tableName;
    }
}
