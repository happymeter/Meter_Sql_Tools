package fu.meter.tools.sql.utils;


import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import fu.meter.tools.sql.DbType;
import fu.meter.tools.sql.MeterSqlException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.PrintStream;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName JdbcUtils
 * @desc jdbc相关操作
 * @date 2021/12/23 14:36
 */
@Slf4j
public class JdbcUtils {

    private final static DbType DB_TYPE;
    static {
        DB_TYPE=DbType.toType(ConfigUtils.getValue("jdbc.dbtype"));
        if(DbType.MARIADB ==DB_TYPE ||DbType.MYSQL ==DB_TYPE){
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    throw new MeterSqlException("Can not find mysql Driver class.");
                }
            }
        }else{
            throw new MeterSqlException("Can not find supported Driver.");
        }
    }
    /**
     * @return java.sql.Connection
     * @desc 获取数据库连接
     * @author meter
     * @date 2021/12/23 14:38
     */
    public static Connection getConnection() {
         Connection connection=null;
        try {
            connection = DriverManager.getConnection(ConfigUtils.getValue("jdbc.url"),ConfigUtils.getValue("jdbc.username"),ConfigUtils.getValue("jdbc.password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * @param connection
     * @return void
     * @desc 关闭连接
     * @author meter
     * @date 2021/12/23 14:50
     */
    public static void close(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            if (connection.isClosed()) {
                return;
            }
            connection.close();
        } catch (SQLRecoverableException e) {
            // skip
        } catch (Exception e) {

            log.error("close connection error", e);
        }
    }
    /**
     * @param statement
     * @return void
     * @desc 用于关闭statement
     * @author meter
     * @date 2021/12/23 14:58
     */
    public static void close(Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        } catch (Exception e) {
            log.error("close statement error", e);
        }
    }
    /**
     * @param resultSet
     * @return void
     * @desc 用于关闭结果集
     * @author meter
     * @date 2021/12/23 15:01
     */
    public static void close(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        } catch (Exception e) {
            log.error("close result set error", e);
        }
    }
    /**
     * @param closeable
     * @return void
     * @desc 用于关闭可关闭的对象
     * @author meter
     * @date 2021/12/23 15:01
     */
    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            log.error("close error", e);
        }
    }

    public static void close(Blob x) {
        if (x == null) {
            return;
        }
        try {
            x.free();
        } catch (Exception e) {
            log.error("close error", e);
        }
    }

    public static void close(Clob x) {
        if (x == null) {
            return;
        }
        try {
            x.free();
        } catch (Exception e) {
            log.error("close error", e);
        }
    }
    /**
     * @param rs
     * @return void
     * @desc 用于打印结果集-测试使用
     * @author meter
     * @date 2021/12/23 15:01
     */
    public static void printResultSet(ResultSet rs) throws SQLException {
        printResultSet(rs, System.out);
    }
    /**
     * @return void
     * @desc 用于打印结果集
     * @author meter
     * @date 2021/12/23 15:01
     */
    public static void printResultSet(ResultSet rs, PrintStream out) throws SQLException {
        printResultSet(rs, out, true, "\t");
    }
    /**
     * @return void
     * @desc 用于打印结果集
     * @author meter
     * @date 2021/12/23 15:01
     */
    public static void printResultSet(ResultSet rs, PrintStream out, boolean printHeader, String seperator) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        if (printHeader) {
            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print(seperator);
                }
                out.print(metadata.getColumnName(columnIndex));
            }
        }
        out.println();
        while (rs.next()) {

            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print(seperator);
                }

                int type = metadata.getColumnType(columnIndex);

                if (type == Types.VARCHAR || type == Types.CHAR || type == Types.NVARCHAR || type == Types.NCHAR) {
                    out.print(rs.getString(columnIndex));
                } else if (type == Types.DATE) {
                    Date date = rs.getDate(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(date.toString());
                    }
                } else if (type == Types.BIT) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(value);
                    }
                } else if (type == Types.BOOLEAN) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(value);
                    }
                } else if (type == Types.TINYINT) {
                    byte value = rs.getByte(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(value);
                    }
                } else if (type == Types.SMALLINT) {
                    short value = rs.getShort(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(value);
                    }
                } else if (type == Types.INTEGER) {
                    int value = rs.getInt(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(value);
                    }
                } else if (type == Types.BIGINT) {
                    long value = rs.getLong(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(value);
                    }
                } else if (type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) {
                    out.print(rs.getTimestamp(columnIndex));
                } else if (type == Types.DECIMAL) {
                    out.print(rs.getBigDecimal(columnIndex));
                } else if (type == Types.CLOB) {
                    out.print(rs.getString(columnIndex));
                } else if (type == Types.JAVA_OBJECT) {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(object);
                    }
                } else if (type == Types.LONGVARCHAR) {
                    Object object = rs.getString(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(object);
                    }
                } else if (type == Types.NULL) {
                    out.print("null");
                } else {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        if (object instanceof byte[]) {
                            byte[] bytes = (byte[]) object;
                            String text = HexBin.encode(bytes);
                            out.print(text);
                        } else {
                            out.print(object);
                        }
                    }
                }
            }
            out.println();
        }
    }




    public static int executeUpdate(DataSource dataSource, String sql, Object... parameters) throws SQLException {
        return executeUpdate(dataSource, sql, Arrays.asList(parameters));
    }

    public static int executeUpdate(DataSource dataSource, String sql, List<Object> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return executeUpdate(conn, sql, parameters);
        } finally {
            close(conn);
        }
    }

    public static int executeUpdate(Connection conn, String sql, List<Object> parameters) throws SQLException {
        PreparedStatement stmt = null;

        int updateCount;
        try {
            stmt = conn.prepareStatement(sql);

            setParameters(stmt, parameters);

            updateCount = stmt.executeUpdate();
        } finally {
            JdbcUtils.close(stmt);
        }

        return updateCount;
    }

    public static void execute(DataSource dataSource, String sql, Object... parameters) throws SQLException {
        execute(dataSource, sql, Arrays.asList(parameters));
    }

    public static void execute(DataSource dataSource, String sql, List<Object> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            execute(conn, sql, parameters);
        } finally {
            close(conn);
        }
    }

    public static void execute(Connection conn, String sql) throws SQLException {
        execute(conn, sql, Collections.emptyList());
    }

    public static void execute(Connection conn, String sql, List<Object> parameters) throws SQLException {
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            setParameters(stmt, parameters);

            stmt.executeUpdate();
        } finally {
            JdbcUtils.close(stmt);
        }
    }
    /**
     * @param dataSource
     * @param sql
     * @param parameters
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @desc 执行查询
     * @author meter
     * @date 2021/12/23 15:07
     */
    public static List<Map<String, Object>> executeQuery(DataSource dataSource, String sql, Object... parameters)
            throws SQLException {
        return executeQuery(dataSource, sql, Arrays.asList(parameters));
    }
    /**
     * @param dataSource
     * @param sql
     * @param parameters
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @desc 执行查询
     * @author meter
     * @date 2021/12/23 15:07
     */
    public static List<Map<String, Object>> executeQuery(DataSource dataSource, String sql, List<Object> parameters)
            throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return executeQuery(conn, sql, parameters);
        } finally {
            close(conn);
        }
    }

    public static List<Map<String, Object>> executeQuery(Connection conn, String sql, List<Object> parameters)
            throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);

            setParameters(stmt, parameters);

            rs = stmt.executeQuery();

            ResultSetMetaData rsMeta = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<String, Object>();

                for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
                    String columName = rsMeta.getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);
                    row.put(columName, value);
                }

                rows.add(row);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }

        return rows;
    }

    private static void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
        if(parameters==null ||parameters.isEmpty()){
            return;
        }
        for (int i = 0, size = parameters.size(); i < size; ++i) {
            Object param = parameters.get(i);
            stmt.setObject(i + 1, param);
        }
    }

    public static void insertToTable(DataSource dataSource, String tableName, Map<String, Object> data)
            throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            insertToTable(conn, tableName, data);
        } finally {
            close(conn);
        }
    }

    public static void insertToTable(Connection conn, String tableName, Map<String, Object> data) throws SQLException {
        String sql = makeInsertToTableSql(tableName, data.keySet());
        List<Object> parameters = new ArrayList<Object>(data.values());
        execute(conn, sql, parameters);
    }

    public static String makeInsertToTableSql(String tableName, Collection<String> names) {
        StringBuilder sql = new StringBuilder() //
                .append("insert into ") //
                .append(tableName) //
                .append("("); //

        int nameCount = 0;
        for (String name : names) {
            if (nameCount > 0) {
                sql.append(",");
            }
            sql.append(name);
            nameCount++;
        }
        sql.append(") values (");
        for (int i = 0; i < nameCount; ++i) {
            if (i != 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");

        return sql.toString();
    }

    public static Integer executeQueryInteger(Connection connection, String sql) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
           return rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return null;
    }
}
