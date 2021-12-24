package fu.meter.tools.sql;

import fu.meter.tools.sql.common.Columns;
import fu.meter.tools.sql.common.Page;
import fu.meter.tools.sql.mysql.MysqlDumpData;
import fu.meter.tools.sql.utils.ConfigUtils;
import fu.meter.tools.sql.utils.JdbcUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName SqlApp
 * @desc 处理SQL导出程序入口
 * @date 2021/12/24 10:22
 */
@Slf4j
public class SqlApp {
    private static  final Integer limit = ConfigUtils.getInteger("dump.data.statement.insert.limit");
    private static  final Integer pageSize = ConfigUtils.getInteger("dump.data.statement.insert.pageSize",1000);
    private static  final Boolean batch = ConfigUtils.getBoolean("dump.data.statement.insert.batch");
    private static  final Boolean createStatement = ConfigUtils.getBoolean("dump.data.statement.create", false);
    private static  final Set<String> ignoreTableNames=ConfigUtils.getSet("dump.data.ignore.table.names");
    private static  final Boolean ignoreCase = ConfigUtils.getBoolean("dump.data.ignore.table.case", true);
    private static  final String ignoreTablePrefix=ConfigUtils.getValue("dump.data.ignore.table.name.prefix");
    private static  final String ignoreTableSuffix=ConfigUtils.getValue("dump.data.ignore.table.name.suffix");
    private static  final String sqlFilePath=ConfigUtils.getValue("dump.data.file.path");
    /**
     * @param name
     * @return java.lang.Boolean
     * @desc 效验是否是需要导出的表名
     * @author meter
     * @date 2021/12/21 11:15
     */
    private static Boolean checkTableNames(String name) {
        if (ignoreTableNames != null && ignoreTableNames.contains(name)) {
            return false;
        }
        if (ignoreTablePrefix != null && !"".equals(ignoreTablePrefix)) {
            if (ignoreCase) {
                if (name.toLowerCase().startsWith(ignoreTablePrefix.toLowerCase())) {
                    return false;
                }
            } else {
                if (name.startsWith(ignoreTablePrefix)) {
                    return false;
                }
            }

        }
        if (ignoreTableSuffix != null && !"".equals(ignoreTableSuffix)) {
            if (ignoreCase) {
                if (name.toLowerCase().endsWith(ignoreTableSuffix.toLowerCase())) {
                    return false;
                }
            } else {
                if (name.endsWith(ignoreTableSuffix)) {
                    return false;
                }
            }

        }
        return true;
    }
    public static void main(String[] args) {

        final MysqlDumpData dumpData = MysqlDumpData.getInstance();
        final Connection connection = JdbcUtils.getConnection();
         List<String> tableNames = dumpData.getAllTableNames(connection);
        tableNames = tableNames.stream().filter(name -> checkTableNames(name)).collect(Collectors.toList());
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-"));
        if(tableNames != null && !tableNames.isEmpty()){
            if (createStatement) {
                String createTableStatement = dumpData.getCreateTableStatement(connection,tableNames);
                if (createTableStatement != null && createTableStatement.length() > 0) {
                    dumpData.outputToFile(createTableStatement, sqlFilePath,today + "create.sql");
                } else {
                    log.error("获取表结构失败:没有获取到数据。");
                    throw new MeterSqlException("Get table create statement failed.");
                }
            }
            for (String tableName : tableNames) {
                final List<Columns> tableColumns = dumpData.getTableColumns(connection, tableName);
                if(tableColumns != null && !tableColumns.isEmpty()){
                    final Page<Map<String, Object>> queryPage = new Page<>();
                    queryPage.setLimit(limit);
                    queryPage.setSize(pageSize);

                     Page<Map<String, Object>> resultPage = dumpData.getTableDataByPage(connection, tableName, queryPage);
                    if(resultPage != null){
                        String insertSql = dumpData.getInsertStatement(tableName, tableColumns,resultPage.getRecords(),batch);
                        dumpData.outputToFile(insertSql, sqlFilePath, today + "insert.sql");
                        if (queryPage.getPages() > 1) {
                            for (int i = 2; i < queryPage.getPages(); i++) {
                                queryPage.setCurrent(i);
                                resultPage = dumpData.getTableDataByPage(connection, tableName, queryPage);
                                if(resultPage != null) {
                                    insertSql = dumpData.getInsertStatement(tableName, tableColumns, resultPage.getRecords(), batch);
                                    dumpData.outputToFile(insertSql, sqlFilePath, today + "insert.sql");
                                }else{
                                    log.info("Query table[{}] data end.",tableName);
                                }
                            }
                        }
                    }else{
                        log.error("Query table[{}] data failed,Result page is null.",tableName);
                        continue;
                    }
                }
            }
        }else{
            log.error("No table name data find.");
        }

    }
}
