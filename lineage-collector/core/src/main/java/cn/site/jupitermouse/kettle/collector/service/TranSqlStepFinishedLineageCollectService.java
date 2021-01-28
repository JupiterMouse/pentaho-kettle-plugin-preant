package cn.site.jupitermouse.kettle.collector.service;

import cn.site.jupitermouse.kettle.collector.constant.Key;
import cn.site.jupitermouse.kettle.collector.dto.MessageDTO;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.pentaho.di.trans.steps.sql.ExecSQLMeta;

/**
 * <p>
 * 转换中对StepMetaDataCombi进行采集
 * </p>
 *
 * @author JupiterMouse 2021/01/28
 * @since 1.0
 */
public class TranSqlStepFinishedLineageCollectService extends AbstractLineageCollectService {

    private static class LazyHolder {
        private static final TranSqlStepFinishedLineageCollectService INSTANCE = new TranSqlStepFinishedLineageCollectService();
    }

    public static TranSqlStepFinishedLineageCollectService getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    protected DatabaseMeta handleEntry(LogChannelInterface log, Object object, MessageDTO messageDTO) {
        handleEntry(messageDTO);
        StepMetaDataCombi stepMetaDataCombi = (StepMetaDataCombi) object;
        // 设置基础信息
        StepMeta stepMeta = stepMetaDataCombi.stepMeta;
        messageDTO.getJob().put(Key.TRAN_STEP_ID, stepMeta.getStepID());
        messageDTO.getJob().put(Key.TRAN_STEP_NAME, stepMeta.getName());
        messageDTO.getJob().put(Key.TRAN_FILE_NAME, stepMeta.getParentTransMeta().getFilename());
        // 设置SQL
        ExecSQLMeta execSqlMeta = (ExecSQLMeta) stepMetaDataCombi.meta;
        messageDTO.setSql(execSqlMeta.getSql());
        return execSqlMeta.getDatabaseMeta();
    }
}
