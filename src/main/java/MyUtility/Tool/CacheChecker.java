package MyUtility.Tool;

import java.util.HashSet;
import java.util.Set;

public abstract class CacheChecker<T> {
    Set<T> set;
    public CacheChecker(){
        set = new HashSet<>();
    }

    public boolean isExist(T key){
        if(!set.contains(key)){
            T receive = cache(key);
            if(receive != null){
                set.add(receive);
                return true;
            }else{
                return false;
            }
        }
        return true;
    }

    abstract public T cache(T key);
}
