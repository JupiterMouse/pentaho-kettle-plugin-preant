package cn.site.jupitermouse.kettle.collector.util;

import java.io.File;
import java.util.Objects;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
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

    private static final String TOPIC = "topic";

    public static void send(LogChannelInterface logChannel, String msg) {
        if (Objects.isNull(properties)) {
            String kafkaPropertyPath = System.getProperty("user.dir") + File.separator + "kafka.properties";
            properties = PropertiesUtil.getProperties(kafkaPropertyPath);
        }
        Producer<String, String> producer = new KafkaProducer<>(properties);
        String topic = properties.getProperty(TOPIC);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, msg);
        producer.send(record, (recordMetadata, e) -> {
            if (e != null) {
                // 异常处理
                logChannel.logBasic(String.format("topic[%s],msg[%s]", topic, msg), e);
            } else {
                logChannel.logBasic("send success");
            }
            // close
            producer.close();
        });
    }

}
