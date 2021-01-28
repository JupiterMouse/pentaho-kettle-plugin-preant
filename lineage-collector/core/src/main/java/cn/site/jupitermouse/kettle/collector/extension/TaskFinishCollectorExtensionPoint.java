package cn.site.jupitermouse.kettle.collector.extension;

import java.util.Objects;

import cn.site.jupitermouse.kettle.collector.listener.CollectFinishJobEntryListener;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.JobExecutionExtension;

/**
 * <p>
 * 任务切入点为
 * ExtensionPointHandler.callExtensionPoint( log, KettleExtensionPoint.JobBeforeJobEntryExecution.id, extension );
 * 可以指定为多个id String[] ids = idList.split( "," );
 * KettleExtensionPoint JobBeforeJobEntryExecution
 * </p>
 * JobBeforeJobEntryExecution
 *
 * @author JupiterMouse 2020/11/15
 * @since 1.0
 */
@ExtensionPoint(
        id = "TaskFinishCollectorExtensionPoint",
        extensionPointId = "JobBeforeJobEntryExecution",
        description = "在SQL任务开始之前插入后置监听器"
)
public class TaskFinishCollectorExtensionPoint implements ExtensionPointInterface {

    @Override
    public void callExtensionPoint(LogChannelInterface log, Object object) throws KettleException {
        if (Objects.isNull(object)) {
            return;
        }
        if (object instanceof JobExecutionExtension) {
            log.logBasic("TaskFinishCollector execute ...");
            JobExecutionExtension jobExecutionExtension = (JobExecutionExtension) object;
            CollectFinishJobEntryListener jobEntryListener = CollectFinishJobEntryListener.getInstance();
            if (!jobExecutionExtension.job.getJobEntryListeners().contains(jobEntryListener)) {
                jobExecutionExtension.job.addJobEntryListener(jobEntryListener);
                jobEntryListener.setClassLoader(getClass().getClassLoader());
            }
        }
    }
}
