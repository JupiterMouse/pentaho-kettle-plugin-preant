package cn.site.jupitermouse.kettle.collector.util;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cn.site.jupitermouse.kettle.collector.constant.Key;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.pentaho.di.core.logging.LogChannelInterface;

/**
 * <p>
 * kafka 工具类
 * </p>
 *
 * @author JupiterMouse 2020/11/16
 * @since 1.0
 */
public class KafkaProducerUtil {

    private static Properties properties;

    public static void send(LogChannelInterface logChannel, String msg) {
        if (Objects.isNull(properties)) {
            String kafkaPropertyPath = System.getProperty("user.dir") + File.separator + "kafka.properties";
            properties = getProperties(kafkaPropertyPath);
        }
        Producer<String, String> producer = new KafkaProducer<>(properties);
        String topic = properties.getProperty(Key.TOPIC);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, msg);
        Future<RecordMetadata> future = producer.send(record, (recordMetadata, e) -> {
            if (e != null) {
                // 异常处理
                logChannel.logBasic(String.format("topic[%s],msg[%s]", topic, msg), e);
            } else {
                logChannel.logBasic(String.format("send success, %s", recordMetadata.toString()));
            }
            // close
            producer.close();
        });
        try {
            future.get();
            logChannel.logBasic("success");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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
}
