package fu.meter.tools.sql.mysql;


import fu.meter.tools.sql.MeterSqlException;
import fu.meter.tools.sql.common.Columns;
import fu.meter.tools.sql.common.DumpData;
import fu.meter.tools.sql.common.Page;
import fu.meter.tools.sql.utils.JdbcUtils;
import fu.meter.tools.sql.utils.MapUtils;
import fu.meter.tools.sql.utils.SqlString;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName MysqlDumpData
 * @desc 用于导出MySQL相关数据
 * @date 2021/12/23 16:59
 */
@Slf4j
public class MysqlDumpData extends DumpData {

    @Override
    public Page<Map<String, Object>> getTableDataByPage(Connection connection, String tableName, Page queryPage) {
        final Page<Map<String, Object>> resultPage = new Page<>();
        if(queryPage.getTotal() ==null){
            final Integer tableDataCount = getTableDataCount(connection, tableName);
            if(tableDataCount == null || tableDataCount ==0){
                queryPage.setTotal(0);
                return queryPage;
            }else{
                queryPage.setTotal(tableDataCount);
                resultPage.setTotal(tableDataCount);
            }
        }
        final StringBuilder builder = new StringBuilder();
            builder.append("select * from ").append(tableName).append(" limit ")
                    .append(queryPage.offset()).append(SqlString.COMMA);
            if(queryPage.getSize()>0){
                builder.append(queryPage.getSize());
            }else {
                return null;
            }
        final List<Map<String, Object>> maps;
        try {
            maps = JdbcUtils.executeQuery(connection, builder.toString(), null);
            resultPage.setCurrent(queryPage.getCurrent());
            resultPage.setRecords(maps);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultPage;
    }

    /**
     * @param connection
     * @param tableName
     * @return java.util.List<fu.meter.tools.sql.common.Columns>
     * @desc 获取数据库字段列表
     * @author meter
     * @date 2021/12/23 17:36
     */
    public  List<Columns> getTableColumns(Connection connection, String tableName){
        List<Columns> columns=new ArrayList<>();
        try {
            final List<Map<String, Object>> resultMapList = JdbcUtils.executeQuery(connection,
                    "SELECT t.column_name,t.data_type,t.column_comment FROM information_schema.COLUMNS t WHERE table_name = ?", Arrays.asList(tableName));
            if(resultMapList != null && !resultMapList.isEmpty()){
                for (Map<String, Object> map : resultMapList) {
                    final Columns column = new Columns();
                    column.setTableName(tableName);
                    column.setColumnName( MapUtils.getString(map,"column_name"));
                    column.setColumnComment( MapUtils.getString(map,"column_comment"));
                    column.setDataType( MapUtils.getString(map,"data_type"));
                    columns.add(column);
                }
            }else{
                log.error("Query table[{}] columns :no data find.",tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MeterSqlException("Query table ["+tableName+"] columns failed.");
        }
        return columns;
    }

    @Override
    public List<String> getAllTableNames(Connection connection) {
        try {
            return MySqlUtils.showTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getCreateTableStatement(Connection connection, List<String> tableNames) {
        try {
            return MySqlUtils.getCreateTableStatement(connection,tableNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param tableName
     * @return java.util.List<fu.meter.tools.sql.common.Columns>
     * @desc 获取数据库字段列表
     * @author meter
     * @date 2021/12/23 17:36
     */
    public  List<Columns> getTableColumns(String tableName){
        return getTableColumns(JdbcUtils.getConnection(),tableName);
    }

    public static MysqlDumpData getInstance(){
        return Holder.INSTANCE;
    }

    private MysqlDumpData(){}

    private final static class Holder{
        private final static MysqlDumpData INSTANCE= new  MysqlDumpData();
    }
}
