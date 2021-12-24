package fu.meter.tools.sql.utils;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName ClassUtils
 * @desc class处理工具
 * @date 2021/12/23 17:04
 */
public class ClassUtils {
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable var3) {
        }

        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable var2) {
                }
            }
        }

        return cl;
    }
}
