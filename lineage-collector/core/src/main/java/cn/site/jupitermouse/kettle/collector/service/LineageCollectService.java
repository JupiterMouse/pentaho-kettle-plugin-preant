package cn.site.jupitermouse.kettle.collector.service;

import org.pentaho.di.core.logging.LogChannelInterface;

/**
 * <p>
 * 血缘信息采集
 * </p>
 *
 * @author JupiterMouse 2021/01/27
 * @since 1.0
 */
public interface LineageCollectService {

    /**
     * 收集血缘日志
     *
     * @param log    log
     * @param object object
     */
    void collect(LogChannelInterface log, Object object);
}
