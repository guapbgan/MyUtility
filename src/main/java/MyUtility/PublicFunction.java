package MyUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class PublicFunction {
    private PublicFunction() {
        throw new AssertionError("MyUtility.PublicFunction is noninstantiable");
    }

    public static List<Map<String, String>> allToString(List queryResult){
        // 2020/06/16 update
        List content = new ArrayList();
        if(queryResult.size() != 0){
            Iterator iterator = queryResult.iterator();
            while(iterator.hasNext()){
                Map<String, String> map = (Map)iterator.next();
                map = allToString(map);
                content.add(map);
            }
        }
        return content;
    }
    public static Map<String, String> allToString(Map oldMap){
        // 2020/07/30 update
        Map<String, String> newMap = new HashMap();
        for(Object object: oldMap.entrySet()){
            Map.Entry<String, String> entry = (Map.Entry)object;
            if(entry.getValue() == null){
                newMap.put(entry.getKey(), "");
            }else{
                newMap.put(entry.getKey(), ((Object)entry.getValue()).toString());
            }
        }
        return newMap;
    }

    public static List<Map<String, Object>> nullToEmptyString(List queryResult){
        // 2020/06/16 update
        List content = new ArrayList();
        if(queryResult.size() != 0){
            Iterator iterator = queryResult.iterator();
            while(iterator.hasNext()){
                Map<String, Object> map = (Map)iterator.next();
                map = nullToEmptyString(map);
                content.add(map);
            }
        }
        return content;
    }

    public static Map<String, Object> nullToEmptyString(Map oldMap){
        // 2020/06/16 update
        Map<String, Object> newMap = new HashMap();
        for(Object object: oldMap.entrySet()){
            Map.Entry<String, Object> entry = (Map.Entry)object;
            if(entry.getValue() == null){
                newMap.put(entry.getKey(), "");
            }else{
                newMap.put(entry.getKey(), ((Object)entry.getValue()));
            }
        }
        return newMap;
    }
    public static boolean isSameDate(Calendar calendar1, Calendar calendar2){
        return calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR) &&
                calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
    }

    public static boolean compareCalendarDate(Date date1, String operator, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        return compareCalendarDate(calendar1, operator, calendar2);
    }

    public static boolean compareCalendarDate(Calendar calendar1, String operator, Calendar calendar2){
        if(operator.contains("<")) {
            if(calendar1.get(Calendar.YEAR) < calendar2.get(Calendar.YEAR)){
                return true;
            }else if(calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)){
                if(calendar1.get(Calendar.DAY_OF_YEAR) < calendar2.get(Calendar.DAY_OF_YEAR)){
                    return true;
                }
            }
        }
        if(operator.contains("=") &&
            calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR) &&
            calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)){
            return true;
        }
        if(operator.contains(">")) {
            if(calendar1.get(Calendar.YEAR) > calendar2.get(Calendar.YEAR)){
                return true;
            }else if(calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)){
                if(calendar1.get(Calendar.DAY_OF_YEAR) > calendar2.get(Calendar.DAY_OF_YEAR)){
                    return true;
                }
            }
        }
        return false;
    }
    public static Map<String, Object> jsonToMap(JSONObject json) throws org.json.JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws org.json.JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws org.json.JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
    public static String fillCharRight(String oldString, int len, String padding){
        int gap = len - oldString.length();
        if(gap > 0){
            return oldString + new String(new char[gap]).replace("\0", padding);
        }
        return oldString;
    }
    public static String fillCharLeft(String oldString, int len, String padding){
        int gap = len - oldString.length();
        if(gap > 0){
            return new String(new char[gap]).replace("\0", padding) + oldString;
        }
        return oldString;
    }
    public static double round(double value, int precision){
        double base = Math.pow(10d, precision);
        return Math.round(value * base) / base;
    }
}
