
package fu.meter.tools.sql.mysql;


import fu.meter.tools.sql.common.Columns;
import fu.meter.tools.sql.utils.FileUtils;
import fu.meter.tools.sql.utils.JdbcUtils;
import fu.meter.tools.sql.utils.SqlString;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author meter
 * @desc MySQL工具类
 * @date 2021/12/23 16:12
 */
public class MySqlUtils {

    private static Set<String> keywords;

    /**
     * @param name
     * @return boolean
     * @desc 判断是否是MySQL系统关键字
     * @author meter
     * @date 2021/12/23 16:11
     */
    public static boolean isKeyword(String name) {
        if (name == null) {
            return false;
        }
        String name_lower = name.toLowerCase();
        Set<String> words = keywords;
        if (words == null) {
            synchronized (MySqlUtils.class) {
                if (words == null) {
                    words = new HashSet<>();
                    FileUtils.loadFromFile("META-INF/mysql/keywords", words);
                    keywords = words;
                }
            }
        }
        return words.contains(name_lower);
    }

    /**
     * @param conn
     * @return java.util.List<java.lang.String>
     * @desc 获取数据库所有表的名称
     * @author meter
     * @date 2021/12/23 16:03
     */
    public static List<String> showTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<String>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("show tables");
            while (rs.next()) {
                String tableName = rs.getString(1);
                tables.add(tableName);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return tables;
    }

    /**
     * @param conn
     * @param tables
     * @return java.util.List<java.lang.String>
     * @desc 获取所有的创建表语句
     * @author meter
     * @date 2021/12/23 16:04
     */
    public static List<String> getTableDDL(Connection conn, List<String> tables) throws SQLException {
        List<String> ddlList = new ArrayList<String>();
        Statement stmt = null;
        try {
            for (String table : tables) {
                if (stmt == null) {
                    stmt = conn.createStatement();
                }

                if (isKeyword(table)) {
                    table = SqlString.BACKTICK + table + SqlString.BACKTICK;
                }
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery("show create table " + table);
                    if (rs.next()) {
                        String ddl = rs.getString(2);
                        ddlList.add(ddl);
                    }
                } finally {
                    JdbcUtils.close(rs);
                }
            }
        } finally {
            JdbcUtils.close(stmt);
        }
        return ddlList;
    }

    /**
     * @param conn
     * @return java.lang.String
     * @desc 获取数据库全部创建表语句
     * @author meter
     * @date 2021/12/23 16:13
     */
    public static String getCreateTableStatement(Connection conn) throws SQLException {
        List<String> tables = showTables(conn);
        return getCreateTableStatement(conn, tables);
    }

    /**
     * @param conn
     * @return java.lang.String
     * @desc 获取数据库表创建表语句
     * @author meter
     * @date 2021/12/23 16:13
     */
    public static String getCreateTableStatement(Connection conn, List<String> tables) throws SQLException {
        List<String> ddlList = getTableDDL(conn, tables);
        StringBuilder buf = new StringBuilder();
        for (String ddl : ddlList) {
            buf.append(ddl);
            buf.append(SqlString.SEMICOLON);
        }
        return buf.toString();
    }

}
