package org.cjavellana.db;


import com.esotericsoftware.yamlbeans.YamlException;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TenantDataSourceRepositoryTest {

    private static final String YAML_PATH = "src/test/resources/tenants.yml";

    @Test
    public void itIsAbleToCreateDataSourceRepositoryFromYaml() throws YamlException, FileNotFoundException {
        TenantDataSourceRepository dataSourceRepository = TenantDataSourceRepository.fromYaml(YAML_PATH);
        assertNotNull(dataSourceRepository);
    }

    @Test
    public void itReturnsAListOfTenantsFoundInTheYamlFile() throws YamlException, FileNotFoundException {
        TenantDataSourceRepository tenantDataSourceRepository = TenantDataSourceRepository.fromYaml(YAML_PATH);
        assertEquals(3, tenantDataSourceRepository.getTenantDataSources().size());
    }
}