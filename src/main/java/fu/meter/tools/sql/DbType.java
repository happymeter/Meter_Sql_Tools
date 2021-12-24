package fu.meter.tools.sql;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName DbType
 * @desc 定义目前支持的数据库类型
 * @date 2021/12/23 14:45
 */
public enum DbType {
    ORACLE(0, "oracle"),
    MYSQL(1, "mysql"),
    MARIADB(2, "mariadb");


    private Integer code;
    private String desc;

    DbType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DbType toType(Object o) {
        if(o==null){
            return MYSQL;
        }
        if(o instanceof String){
            return valueOf(o.toString());
        }
        return null;
    }
}
