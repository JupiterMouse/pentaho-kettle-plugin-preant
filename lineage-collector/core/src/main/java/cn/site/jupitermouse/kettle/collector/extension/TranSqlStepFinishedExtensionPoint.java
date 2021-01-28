package cn.site.jupitermouse.kettle.collector.extension;

import cn.site.jupitermouse.kettle.collector.service.TranSqlStepFinishedLineageCollectService;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.pentaho.di.trans.steps.sql.ExecSQLMeta;

/**
 * <p>
 * StepAfterInitialize
 * </p>
 *
 * @author JupiterMouse 2021/01/27
 * @since 1.0
 */
@ExtensionPoint(
        id = "TranSqlStepFinishedExtensionPoint",
        extensionPointId = "StepFinished",
        description = "转换中SQL任务执行完后的处理"
)
public class TranSqlStepFinishedExtensionPoint implements ExtensionPointInterface {
    @Override
    public void callExtensionPoint(LogChannelInterface log, Object object) throws KettleException {
        if (object == null) {
            return;
        }
        if (!(object instanceof StepMetaDataCombi)) {
            return;
        }
        StepMetaDataCombi combi = (StepMetaDataCombi) object;
        if (!(combi.meta instanceof ExecSQLMeta)) {
            return;
        }
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            TranSqlStepFinishedLineageCollectService.getInstance().collect(log, object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

}
