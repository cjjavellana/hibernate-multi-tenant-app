package org.cjavellana.db;


import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.rits.cloning.Cloner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class TenantDataSourceRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantDataSourceRepository.class);

    private String yamlPath;
    private List<TenantDataSource> tenantDataSources = new ArrayList<>();

    private TenantDataSourceRepository(String file) {
        this.yamlPath = file;
    }

    private void init() throws FileNotFoundException, YamlException {
        YamlReader yamlReader = new YamlReader(new FileReader(this.yamlPath));

        Map<String, Object> yamlMap = (Map<String, Object>) yamlReader.read();
        Map<String, Object> tenantSettingsMap = tenantSettingsMap(yamlMap);
        Map<String, Object> defaults = readDefaults(tenantSettingsMap);

        List<Map<String, Object>> tenants = (List<Map<String, Object>>) tenantSettingsMap.get("tenants");
        if (CollectionUtils.isEmpty(tenants)) {
            throw new RuntimeException("Invalid configuration file; No tenants found.");
        }

        tenants.forEach(tenantMap -> {
            // Create new object with default values
            Map<String, Object> tenantSettings = new HashMap<>(defaults);

            // super impose tenant configuration, overriding defaults.
            tenantSettings.putAll(tenantMap);

            TenantDataSource tenantDataSource = new TenantDataSource();
            tenantDataSource.dialect = (String) tenantSettings.get("dialect");
            tenantDataSource.driverClassName = (String) tenantSettings.get("driverClassName");
            tenantDataSource.id = (String) tenantSettings.get("id");
            tenantDataSource.name = (String) tenantSettings.get("name");
            tenantDataSource.username = (String) tenantSettings.get("username");
            tenantDataSource.password = (String) tenantSettings.get("password");
            tenantDataSource.validationQuery = (String) tenantSettings.get("validationQuery");
            tenantDataSource.jdbcUrl = (String) tenantSettings.get("jdbcUrl");

            tenantDataSources.add(tenantDataSource);
        });
    }

    private Map<String, Object> tenantSettingsMap(Map<String, Object> yamlMap) {
        LOGGER.debug("Checking for 'tenantSettings'");
        Map<String, Object> tenantSettings = (Map<String, Object>) yamlMap.get("tenantSettings");
        if (CollectionUtils.isEmpty(tenantSettings)) {
            throw new RuntimeException("Unable to find top level entry 'tenantSettings'");
        }

        return tenantSettings;
    }

    private Map<String, Object> readDefaults(Map<String, Object> yamlMap) {
        // clone map read by yaml reader
        Cloner cloner = new Cloner();
        Map<String, Object> defaultSettingsMap = cloner.deepClone(yamlMap);

        // only default settings are left after tenants is removed
        defaultSettingsMap.remove("tenants");
        return defaultSettingsMap;
    }

    public static TenantDataSourceRepository fromYaml(String file) throws FileNotFoundException, YamlException {
        TenantDataSourceRepository repo = new TenantDataSourceRepository(file);
        repo.init();
        return repo;
    }

    public List<TenantDataSource> getTenantDataSources() {
        return Collections.unmodifiableList(tenantDataSources);
    }
}
