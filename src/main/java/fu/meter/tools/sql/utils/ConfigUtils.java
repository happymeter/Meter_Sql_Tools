package fu.meter.tools.sql.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName ConfigUtils
 * @desc 读取配置文件
 * @date 2021/12/23 16:47
 */
public class ConfigUtils {
    private final static Properties PROPERTIES;
    static {
        PROPERTIES=new Properties();
        try {
            PROPERTIES.load(FileUtils.getInputStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key){
        final Object value = PROPERTIES.get(key);
        if(value != null){
            return value.toString();
        }
        return null;
    }
    public static String getValue(String key,String defaults){
        final String value = getValue(key);
        if(value==null){
            return defaults;
        }
        return value;
    }
    public static Integer getInteger(String key){
        final String value = getValue(key);
        if(value != null && !"".equals(value)){
            return Integer.parseInt(value);
        }
        return null;
    }
    public static Integer getInteger(String key,Integer defaults){
        final Integer integer = getInteger(key);
        if(integer==null){
            return defaults;
        }
        return integer;
    }

    public static Boolean getBoolean(String key){
        final String value = getValue(key);
        if(value != null && !"".equals(value)){
            return Boolean.parseBoolean(value);
        }
        return null;
    }

    public static Boolean getBoolean(String key,Boolean defaults){
        final Boolean aBoolean = getBoolean(key);
        if(aBoolean == null){
            return defaults;
        }
        return aBoolean;
    }
    public static Set<String> getSet(String key){
        return getSet(key,SqlString.COMMA);
    }
    public static Set<String> getSet(String key,String splitter) {
        final String value = getValue(key);
        if(value != null && !"".equals(value)){
            final Set<String> objects = new HashSet<>();
           objects.addAll(Arrays.asList( value.split(splitter)));
            return objects;
        }
        return null;
    }
}
