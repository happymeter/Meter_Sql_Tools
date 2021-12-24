package fu.meter.tools.sql.utils;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName SqlString
 * @desc 常用SQL字符串
 * @date 2021/12/23 16:08
 */
public interface SqlString {
    String INSERT_INTO="insert into ";
    String VALUES = " values ";
    String COMMA = ",";
    String EMPTY = "";
    String LEFT_BRACKET = "(";
    String DOT_NEWLINE = ",\n";
    String NEWLINE = "\n";
    String QUOTE = "\"";
    String RETURN = "\r";
    String TAB = "\t";
    String RIGHT_BRACKET = ")";
    String SEMICOLON = ";";
    String SINGLE_QUOTE = "'";
    String BACKTICK = "`";
    String SPACE = " ";
    String CRLF = "\r\n";
    String SELECT_DATA_SQL = "SELECT * FROM ?";
}
