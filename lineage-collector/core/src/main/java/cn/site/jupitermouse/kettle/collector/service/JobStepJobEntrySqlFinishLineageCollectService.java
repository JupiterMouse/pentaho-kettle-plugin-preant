package cn.site.jupitermouse.kettle.collector.service;

import java.util.Map;

import cn.site.jupitermouse.kettle.collector.constant.Key;
import cn.site.jupitermouse.kettle.collector.dto.MessageDTO;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.entries.sql.JobEntrySQL;

/**
 * <p>
 * 作业中JobEntrySql处理
 * </p>
 *
 * @author JupiterMouse 2021/01/28
 * @since 1.0
 */
public class JobStepJobEntrySqlFinishLineageCollectService extends AbstractLineageCollectService {

    private static class LazyHolder {
        private static final JobStepJobEntrySqlFinishLineageCollectService INSTANCE = new JobStepJobEntrySqlFinishLineageCollectService();
    }

    public static JobStepJobEntrySqlFinishLineageCollectService getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    protected DatabaseMeta handleEntry(LogChannelInterface logChannel, Object object, MessageDTO messageDTO) {
        handleEntry(messageDTO);
        JobEntrySQL entrySql = (JobEntrySQL) object;
        // 设置SQL
        messageDTO.setSql(entrySql.getSQL());
        // parent
        Job parentJob = entrySql.getParentJob();
        Map<String, String> job = messageDTO.getJob();
        job.put(Key.JOB_CONFIG_ID, entrySql.getPluginId());
        job.put(Key.JOB_STEP_NAME, entrySql.getName());
        job.put(Key.JOB_FILE_NAME, parentJob.getFilename());
        return entrySql.getDatabase();
    }

}
