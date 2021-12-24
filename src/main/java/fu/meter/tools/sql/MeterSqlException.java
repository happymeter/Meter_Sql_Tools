package fu.meter.tools.sql;

import lombok.AllArgsConstructor;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName MeterSqlException
 * @desc 自定义Sql异常
 * @date 2021/12/23 16:54
 */

public class MeterSqlException extends RuntimeException{
    public MeterSqlException() {
        super();
    }

    public MeterSqlException(String message) {
        super(message);
    }

    public MeterSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public MeterSqlException(Throwable cause) {
        super(cause);
    }
}
