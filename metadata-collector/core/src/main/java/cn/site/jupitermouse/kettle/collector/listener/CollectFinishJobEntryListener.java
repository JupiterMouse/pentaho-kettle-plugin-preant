package cn.site.jupitermouse.kettle.collector.listener;

import java.util.Objects;

import cn.site.jupitermouse.kettle.collector.job.sql.dto.JobEntrySqlDTO;
import cn.site.jupitermouse.kettle.collector.util.KafkaProducerUtil;
import cn.site.jupitermouse.kettle.collector.util.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobEntryListener;
import org.pentaho.di.job.entries.sql.JobEntrySQL;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;

/**
 * <p>
 * JobEntryListener监听器
 * </p>
 *
 * @author JupiterMouse 2020/11/15
 * @since 1.0
 */
public class CollectFinishJobEntryListener implements JobEntryListener {

    private ClassLoader classLoader;

    private static class LazyHolder {
        private static final CollectFinishJobEntryListener INSTANCE = new CollectFinishJobEntryListener();
    }

    public static CollectFinishJobEntryListener getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public void beforeExecution(Job job, JobEntryCopy jobEntryCopy, JobEntryInterface jobEntryInterface) {
        // nothing to do
    }

    @Override
    public void afterExecution(Job job, JobEntryCopy jobEntryCopy, JobEntryInterface jobEntryInterface, Result result) {
        if (Objects.isNull(jobEntryInterface)) {
            return;
        }
        // JobEntrySQL 时采集SQL信息
        ClassLoader temp = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.classLoader);
        if (jobEntryInterface instanceof JobEntrySQL) {
            LogChannelInterface logChannel = job.getLogChannel();
            logChannel.logBasic("collect sql start ... ");
            ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
            try {
                JobEntrySqlDTO sqlDTO = transformSqlDTO((JobEntrySQL) jobEntryInterface);
                String content = objectMapper.writeValueAsString(sqlDTO);
                logChannel.logBasic(String.format("collect sql content ... \n %s", content));
                logChannel.logBasic("send content ...");
                KafkaProducerUtil.send(logChannel, content);
            } catch (Exception e) {
                logChannel.logError(e.getMessage());
            } finally {
                Thread.currentThread().setContextClassLoader(temp);
            }
        }
    }

    private JobEntrySqlDTO transformSqlDTO(JobEntrySQL entry) {
        // base
        String sql = entry.getSQL();
        String name = entry.getName();
        // parent
        Job parentJob = entry.getParentJob();
        String parentJobName = parentJob.getName();
        String jobFileName = parentJob.getFilename();
        // db
        DatabaseMeta database = entry.getDatabase();
        String dbName = database.getName();
        String databaseName = database.getDatabaseName();
        String displayName = database.getDisplayName();
        String hostname = database.getHostname();
        String username = database.getUsername();
        String pluginId = database.getPluginId();
        String jdbcUrl = null;
        try {
            jdbcUrl = database.getURL();
        } catch (KettleDatabaseException e) {
            e.printStackTrace();
        }
        JobEntrySqlDTO.ParentJob parentJobDTO = JobEntrySqlDTO.ParentJob.builder()
                .parentJobName(parentJobName)
                .jobFileName(jobFileName)
                .build();
        JobEntrySqlDTO.DataBaseMeta dataBaseMeta = JobEntrySqlDTO.DataBaseMeta.builder()
                .name(dbName)
                .databaseName(databaseName)
                .displayName(displayName)
                .hostname(hostname)
                .username(username)
                .pluginId(pluginId)
                .jdbcUrl(jdbcUrl)
                .build();
        return JobEntrySqlDTO.builder()
                .name(name)
                .sql(sql)
                .parentJob(parentJobDTO)
                .dbMeta(dataBaseMeta)
                .build();
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
