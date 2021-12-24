package fu.meter.tools.sql.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @desc 用于映射MySQL数据库字段表：information_schema.COLUMNS
 * @author meter
 * @date 2021/12/16 15:25
 */
@Data
public class Columns implements Serializable {

    private String tableName;

    private String columnName;

    private String dataType;

    private String columnComment;

    private static final long serialVersionUID = 1L;
}