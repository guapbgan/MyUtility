package MyUtility.SqlBuilder;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;

public class UpdateSqlBuilder{
    //version 2020/06/20
    private String tableName;
    private StringBuilder stringBuilder;
    private ArrayList<Object> objectArrayList;
    private boolean valueFlag;
    private boolean whereFlag;
    public UpdateSqlBuilder(String tableName){
        this.tableName = tableName;
        this.stringBuilder = new StringBuilder("UPDATE " + tableName + " SET ");
        this.objectArrayList = new ArrayList<>();
        this.whereFlag = false;
        this.valueFlag = false;
    }
    public String getSql(){
        return this.stringBuilder.toString();
    }
    public Object[] getObjectArray(){
        return this.objectArrayList.toArray();
    }
    public String getObjectString(){
        String temp = "";
        for(Object object: this.getObjectArray()){
            temp += object.toString() + ", ";
        }
        return temp;
    }
    public void putFieldAndValue(String field, Object value){
        if(this.whereFlag){
            throw new RuntimeException("putFieldAndValue method must be called before putWhere");
        }
        this.valueFlag = true;
        this.stringBuilder.append(field).append("=?, ");
        this.objectArrayList.add(value);
    }
    public void putFieldAndValue(String valueString, Object[] objects){
        if(this.whereFlag){
            throw new RuntimeException("putFieldAndValue method must be called before putWhere");
        }
        this.valueFlag = true;
        if(objects == null){
            this.stringBuilder.append(valueString).append(", ");
        }else {
            this.stringBuilder.append(valueString).append(", ");
            this.objectArrayList.addAll(Arrays.asList(objects));
        }
    }
    public void putFieldAndValue(String valueString){
        putFieldAndValue(valueString, null);
    }
    public void putWhere(String field, Object value){
        initPutWhere();
        this.stringBuilder.append("and ").append(field).append("=? ");
        this.objectArrayList.add(value);
    }
    public void putWhere(String valueString, Object[] objects){
        initPutWhere();
        if(objects == null){
            this.stringBuilder.append("and ").append(valueString).append(" ");
        }else{
            this.stringBuilder.append("and ").append(valueString).append(" ");
            this.objectArrayList.addAll(Arrays.asList(objects));
        }
    }
    public void putWhere(String valueString){
        putWhere(valueString, null);
    }

    private void initPutWhere() {
        if(!this.valueFlag){
            throw new RuntimeException("No putFieldAndValue method has been called");
        }
        if(!this.whereFlag){
            //first call putWherePart
            this.stringBuilder = new StringBuilder(this.stringBuilder.substring(0, this.stringBuilder.toString().length() - 2));//remove ", "
            this.stringBuilder.append(" where 1=1 ");
        }
        this.whereFlag = true;
    }

    public int update(JdbcTemplate jt){
        return jt.update(this.getSql(), this.getObjectArray());
    }

    public String getTableName() {
        return tableName;
    }
}
