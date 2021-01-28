package cn.site.jupitermouse.kettle.collector.listener;

import java.util.Objects;

import cn.site.jupitermouse.kettle.collector.service.JobStepJobEntrySqlFinishLineageCollectService;
import org.pentaho.di.core.Result;
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
            try {
                JobStepJobEntrySqlFinishLineageCollectService.getInstance().collect(job.getLogChannel(), jobEntryInterface);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Thread.currentThread().setContextClassLoader(temp);
            }
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
