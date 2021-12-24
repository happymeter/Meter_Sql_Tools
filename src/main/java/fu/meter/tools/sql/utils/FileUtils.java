package fu.meter.tools.sql.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.util.Assert;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName FileUtils
 * @desc 文件操作工具类
 * @date 2021/12/23 15:11
 */
@Slf4j
public class FileUtils {

    /**
     * @param path
     * @param set
     * @return void
     * @desc 用于读取文件内容
     * @author meter
     * @date 2021/12/23 15:13
     */
    public static void loadFromFile(String path, Set<String> set) {
        try(InputStream is = getInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            for (; ; ) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                line = line.trim().toLowerCase();

                if (line.length() == 0) {
                    continue;
                }
                set.add(line);
            }
        } catch (Exception ex) {
            log.error("Load file [{}] failed。",path);
            ex.printStackTrace();
        }
    }
    /**
     * @param fileName
     * @return java.io.InputStream
     * @desc 读取classpath下的文件流
     * @author meter
     * @date 2021/12/23 16:46
     */
    public static InputStream getInputStream(String fileName){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }
    public static URL getURL(String resourceLocation) throws FileNotFoundException {
        if (resourceLocation.startsWith("classpath:")) {
            String path = resourceLocation.substring("classpath:".length());
            ClassLoader cl = ClassUtils.getDefaultClassLoader();
            URL url = cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path);
            if (url == null) {
                String description = "class path resource [" + path + "]";
                throw new FileNotFoundException(description + " cannot be resolved to URL because it does not exist");
            } else {
                return url;
            }
        } else {
            try {
                return new URL(resourceLocation);
            } catch (MalformedURLException var6) {
                try {
                    return (new File(resourceLocation)).toURI().toURL();
                } catch (MalformedURLException var5) {
                    throw new FileNotFoundException("Resource location [" + resourceLocation + "] is neither a URL not a well-formed file path");
                }
            }
        }
    }
}
