package fu.meter.tools.sql.utils;

import java.util.Map;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName MapUtils
 * @desc 用于处理map相关
 * @date 2021/12/23 17:32
 */
public class MapUtils {
    public static String getString(Map map,Object key){
        final Object o = map.get(key);
        if(o != null){
            return o.toString();
        }
        return null;
    }

    public static Integer getInteger(Map map,Object key){
        final Object o = map.get(key);
        if(o != null){
            if(o instanceof Integer){
                return (Integer) o;
            }
            final String s = o.toString();
            if(!"".equals(s)){
                return Integer.parseInt(s);
            }
        }
        return null;
    }
}
