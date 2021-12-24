package fu.meter.tools.sql.common;

import fu.meter.tools.sql.MeterSqlException;
import fu.meter.tools.sql.utils.ClassUtils;
import fu.meter.tools.sql.utils.FileUtils;
import fu.meter.tools.sql.utils.JdbcUtils;
import fu.meter.tools.sql.utils.SqlString;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName MysqlDumpData
 * @desc 导出数据公共类
 * @date 2021/12/23 17:00
 */
@Slf4j
public abstract class DumpData {
    /**
     * @param data
     * @param savePath
     * @param fileName
     * @return void
     * @desc 写入文件
     * @author meter
     * @date 2021/12/21 16:20
     */
    public void outputToFile(String data, String savePath,String fileName) {
        if (data == null || data.length() == 0) {
            return;
        }
        if (savePath == null || savePath.length() == 0 || ".".equals(savePath)) {
            URL resource = ClassUtils.getDefaultClassLoader().getResource("");
            if(resource ==null){
                savePath=DumpData.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                File file = new File(savePath);
                if(file.exists()){
                    savePath=file.getParent();
                }else{
                    throw new MeterSqlException("parse save path failed.");
                }
            }else {
                savePath = resource.getPath();
            }
        } else if (savePath.startsWith("classpath:")) {
            try {
                savePath = FileUtils.getURL("classpath:").getPath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        log.info("输出SQL语句到目录:{},文件名:{}",savePath,fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(savePath, fileName), true);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             final BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            bufferedWriter.write(data);
            bufferedWriter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param valueBuilder
     * @param value
     * @return void
     * @desc 拼接字段数据
     * @author meter
     * @date 2021/12/20 15:05
     */
    protected void appendValue(StringBuilder valueBuilder,  Object value) {
        if(value != null){
            Class type=value.getClass();
            if(type==Boolean.class || type==boolean.class){
                if((boolean) value){
                    valueBuilder.append(1).append(SqlString.COMMA);
                }else{
                    valueBuilder.append(0).append(SqlString.COMMA);
                }
            }else if(checkNumberType(type)){
                //数字类型
                valueBuilder.append(value).append(SqlString.COMMA);
            }else if(value instanceof Timestamp){
                //日期格式
                valueBuilder.append(SqlString.SINGLE_QUOTE).append(value).append(SqlString.SINGLE_QUOTE).append(SqlString.COMMA);
            }else{

                if(type == Date.class){
                    //日期类型
                    value=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(value);
                }else if(type == LocalDate.class){
                    value= DateTimeFormatter.ofPattern("yyyy-MM-dd").format((LocalDate) value);
                }else if(type== LocalDateTime.class){
                    value= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format((LocalDateTime) value);
                }
                valueBuilder.append(SqlString.SINGLE_QUOTE).append(value).append(SqlString.SINGLE_QUOTE).append(SqlString.COMMA);
            }
        }else{
            valueBuilder.append("null").append(SqlString.COMMA);
        }
    }
    /**
     * @param type
     * @return boolean
     * @desc 判读是否是数字类型
     * @author meter
     * @date 2021/12/20 11:09
     */
    protected boolean checkNumberType(Class<?> type) {
        if(type.isPrimitive() && type != char.class && type != boolean.class){
            return true;
        }
        if(type==Integer.class || type==Byte.class ||
                type==Long.class || type == Double.class
                ||type==Float.class){
            return true;
        }
        return false;

    }

    /**
     * @param columns
     * @return java.lang.String
     * @desc 拼接要插入的字段(包含括号)
     * @author meter
     * @date 2021/12/20 17:10
     */
    protected String makeColumns(List<Columns> columns) {
        StringBuilder columnBuilder = new StringBuilder(SqlString.LEFT_BRACKET);
        for (Columns column : columns) {
            log.debug("Column:{}, Comment:{}.",column.getColumnName(),column.getColumnComment());
            columnBuilder.append(column.getColumnName()).append(SqlString.COMMA);
        }
        columnBuilder.deleteCharAt(columnBuilder.length()-1).append(SqlString.RIGHT_BRACKET);
        return columnBuilder.toString();
    }

    /**
     * @param dataMap
     * @param columns
     * @return java.lang.String
     * @desc 生成插入的值得内容
     * @author meter
     * @date 2021/12/20 17:19
     */
    protected String makeValues(Map<String, Object> dataMap, List<Columns> columns) {
        StringBuilder valueBuilder = new StringBuilder();
        for (Columns column : columns) {
            String columnName = column.getColumnName();
            Object value = dataMap.get(columnName);
            appendValue(valueBuilder,value);

        }
        valueBuilder.deleteCharAt(valueBuilder.length()-1);
        return valueBuilder.toString();
    }

    /**
     * @param tableName
     * @param batchStatement
     * @param columns
     * @param dataList
     * @return java.lang.String
     * @desc 用于拼接insert语句
     * @author meter
     * @date 2021/12/22 18:13
     */
    protected String makeInsertStatement(String tableName, Boolean batchStatement, List<Columns> columns, List<Map<String, Object>> dataList) {
        String insertTableColumns=makeColumns(columns);
        StringBuilder sqlBuilder = new StringBuilder();
        if(batchStatement){
            sqlBuilder.append(SqlString.INSERT_INTO).append(tableName).append(insertTableColumns).append(SqlString.VALUES).append(SqlString.NEWLINE);
        }
        for (Map<String, Object> dataMap : dataList) {
            String values=makeValues(dataMap, columns);
            if(batchStatement){
                //批量插入
                sqlBuilder.append(SqlString.LEFT_BRACKET)
                        .append(values)
                        .append(SqlString.RIGHT_BRACKET)
                        .append(SqlString.COMMA).append(SqlString.NEWLINE);
            }else{
                sqlBuilder.append(SqlString.INSERT_INTO).append(tableName)
                        .append(insertTableColumns).append(SqlString.VALUES)
                        .append(SqlString.LEFT_BRACKET)
                        .append(values)
                        .append(SqlString.RIGHT_BRACKET)
                        .append(SqlString.SEMICOLON).append(SqlString.NEWLINE);
            }
        }
        if(batchStatement) {
            sqlBuilder.replace(sqlBuilder.length() - 2, sqlBuilder.length(), SqlString.SEMICOLON);
        }
        return sqlBuilder.append(SqlString.NEWLINE).toString();
    }
    /**
     * @param connection
     * @param tableName
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @desc 查询全部数据
     * @author meter
     * @date 2021/12/23 17:46
     */
    protected List<Map<String,Object>> getTableData(Connection connection,String tableName){
        try {
            return JdbcUtils.executeQuery(connection,SqlString.SELECT_DATA_SQL, Arrays.asList(tableName));
        } catch (SQLException e) {
            log.error("Query table[{}] data failed.",tableName);
            e.printStackTrace();
        }
        return null;
    }
    protected Integer getTableDataCount(Connection connection,String tableName){
        return JdbcUtils.executeQueryInteger(connection,"select count(1) from "+tableName);
    }
    protected abstract Page<Map<String,Object>> getTableDataByPage(Connection connection,String tableName,Page queryPage);
    protected abstract List<Columns> getTableColumns(Connection connection, String tableName);
    protected  abstract List<String> getAllTableNames(Connection connection);

    protected abstract String getCreateTableStatement(Connection connection,List<String> tableNames);

    public  String getInsertStatement(String tableName, List<Columns> tableColumns, List<Map<String, Object>> records, Boolean batch){
        if(records != null && !records.isEmpty()){
            return makeInsertStatement(tableName,batch,tableColumns,records);
        }else{
           log.error("Table ["+tableName+"] data is null.");
           return null;
        }
    }
}
