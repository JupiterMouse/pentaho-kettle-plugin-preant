package cn.site.jupitermouse.kettle.collector.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.util.StringUtils;

/**
 * <p>
 * PropertiesUtil
 * </p>
 *
 * @author JupiterMouse 2020/11/16
 * @since 1.0
 */
public final class PropertiesUtil {

    private PropertiesUtil() {

    }

    public static Properties getProperties(String propertiesPath) {
        Properties properties = new Properties();
        try (InputStream in = new BufferedInputStream(new FileInputStream(propertiesPath))) {
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("获取[%s]路径出错, %s", propertiesPath, e));
        }
        return properties;
    }

    public static String getProperties(String propertiesPath, String key) {
        Properties properties = new Properties();
        try (InputStream in = new BufferedInputStream(new FileInputStream(propertiesPath))) {
            properties.load(in);
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("获取[%s]的值出错, %s", key, e));
        }
    }

    public static String getProperties(String propertiesPath, String key, String defaultValue) {
        Properties properties = new Properties();
        try (InputStream in = new BufferedInputStream(new FileInputStream(propertiesPath))) {
            properties.load(in);
            if (StringUtils.isEmpty(properties.getProperty(key))) {
                return defaultValue;
            }
            return properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }


}
