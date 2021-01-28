package cn.site.jupitermouse.kettle.collector.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author JupiterMouse 2020/12/03
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class MessageDTO {

    private String sourceType;

    /**
     * 数据库类型
     */
    private String dbType;

    /**
     * platformName
     */
    private String platformName;

    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 数据源Code
     */
    private String datasourceCode;

    /**
     * clusterName
     */
    private String clusterName;

    /**
     * catalog
     */
    private String catalogName;
    /**
     * schema
     */
    private String schemaName;

    /**
     * sql
     */
    private String sql;

    /**
     * createTime
     */
    private LocalDateTime createTime;

    private final Map<String, String> job = new HashMap<>(16);
}
