package cn.site.jupitermouse.kettle.collector.job.sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.entries.sql.JobEntrySQL;

/**
 * <p>
 * 传输对象
 * </p>
 *
 * @author JupiterMouse 2020/11/16
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntrySqlDTO {
    /**
     * sql
     */
    private String sql;
    /**
     * sql节点的名称
     */
    private String name;
    /**
     * 父Job
     */
    private ParentJob parentJob;
    /**
     * 数据库元数据信息
     */
    private DataBaseMeta dbMeta;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParentJob {
        /**
         * @see JobEntrySQL#getParentJob()
         * @see Job#getJobMeta()
         * @see org.pentaho.di.job.JobMeta#getName()
         */
        private String parentJobName;
        /**
         * jobEntryInterface .getParentJob().getJobMeta().getFilename()
         */
        private String jobFileName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataBaseMeta {
        private String name;
        private String displayName;
        private String jdbcUrl;
        private String hostname;
        private String databaseName;
        private String username;
        private String pluginId;
    }
}
