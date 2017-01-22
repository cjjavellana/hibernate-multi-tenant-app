package org.cjavellana.db;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the datasources connected to each tenant's databases
 */
public class DataSourcePool {

    private Map<String, DataSource> dataSourceMap = new HashMap<>();

    public void addTenantDataSource(String tenantId, DataSource dataSource) {
        dataSourceMap.put(tenantId, dataSource);
    }

    public void removeTenantDataSource(String tenantId) {
        dataSourceMap.remove(tenantId);
    }

    public DataSource getDataSourceFor(String tenantId) {
        return dataSourceMap.get(tenantId);
    }
}
