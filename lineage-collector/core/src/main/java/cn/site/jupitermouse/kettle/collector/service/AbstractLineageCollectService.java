package cn.site.jupitermouse.kettle.collector.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import cn.site.jupitermouse.kettle.collector.constant.Constant;
import cn.site.jupitermouse.kettle.collector.constant.Key;
import cn.site.jupitermouse.kettle.collector.dto.MessageDTO;
import cn.site.jupitermouse.kettle.collector.util.KafkaProducerUtil;
import cn.site.jupitermouse.kettle.collector.util.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.util.EnvUtil;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 抽象实现
 * </p>
 *
 * @author JupiterMouse 2021/01/28
 * @since 1.0
 */
public abstract class AbstractLineageCollectService implements LineageCollectService {
    @Override
    public void collect(LogChannelInterface log, Object object) {
        // 收集主要信息
        MessageDTO messageDTO = new MessageDTO();
        // 处理采集对象
        DatabaseMeta databaseMeta = handleEntry(log, object, messageDTO);
        // 收集db信息
        transformDb(log, messageDTO, databaseMeta);
        // 发送消息
        sendMessage(log, messageDTO);
    }

    /**
     * 处理sql step
     *
     * @param log        log
     * @param object     object
     * @param messageDTO messageDTO
     * @return MessageDTO
     */
    protected abstract DatabaseMeta handleEntry(LogChannelInterface log,
                                                Object object,
                                                MessageDTO messageDTO);

    /**
     * 处理DB节点
     *
     * @param logChannel   logChannel
     * @param messageDTO   messageDTO
     * @param databaseMeta databaseMeta
     */
    protected void transformDb(LogChannelInterface logChannel, MessageDTO messageDTO, DatabaseMeta databaseMeta) {
        if (databaseMeta == null) {
            return;
        }
        messageDTO.setDbType(databaseMeta.getPluginId());
        messageDTO.setClusterName(databaseMeta.getDisplayName());
        // 如果数据源类型为schema类型时的处理
        String dbSchemaStr = EnvUtil.getSystemProperty(Key.DB_SCHEMA_LIST, Constant.DB_SCHEMA_LIST);
        String[] dbSchemaArr = StringUtils.delimitedListToStringArray(dbSchemaStr, ",");
        messageDTO.setCatalogName(databaseMeta.getDatabaseName());
        for (String dbSchema : dbSchemaArr) {
            if (messageDTO.getDbType().equalsIgnoreCase(dbSchema)) {
                messageDTO.setSchemaName(databaseMeta.getDatabaseName());
                messageDTO.setCatalogName(null);
                break;
            }
        }
        // 填充tenantId 和 datasourceCode
        String[] clusterNameArr = StringUtils.split(databaseMeta.getDisplayName(), "_");
        if (Objects.nonNull(clusterNameArr)) {
            try {
                Long tenantId = Long.valueOf(clusterNameArr[0]);
                messageDTO.setTenantId(tenantId);
                messageDTO.setDatasourceCode(clusterNameArr[1]);
            } catch (Exception e) {
                logChannel.logError("The database name does not meet the specification，tenantId_datasourceCode");
                e.printStackTrace();
            }
        }
        // 额外信息
        Map<String, String> job = messageDTO.getJob();
        try {
            job.put(Key.JDBC_URL, databaseMeta.getURL());
        } catch (Exception e) {
            // ignore
        }
    }

    protected void sendMessage(LogChannelInterface log, MessageDTO messageDTO) {
        ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
        String content = "";
        try {
            content = objectMapper.writeValueAsString(messageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.logBasic(String.format("collect sql content ... \n %s", content));
        log.logBasic("send content ...");
        KafkaProducerUtil.send(log, content);
    }

    protected void handleEntry(MessageDTO messageDTO) {
        // 当前创建时间
        messageDTO.setCreateTime(LocalDateTime.now());
        // 额外信息
        Map<String, String> job = messageDTO.getJob();
        job.put(Key.TYPE, Constant.TYPE_KETTLE);
        // 默认变量
        messageDTO.setSourceType(EnvUtil.getSystemProperty(Key.LINEAGE_SQL_TYPE, Constant.SQL_PARSER));
        messageDTO.setPlatformName(EnvUtil.getSystemProperty(Key.LINEAGE_PLATFORM, Constant.DEFAULT_PLATFORM));
    }

}
