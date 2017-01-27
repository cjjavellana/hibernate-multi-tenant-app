package org.cjavellana.db;

import com.esotericsoftware.yamlbeans.YamlException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class DatabaseConfigurations {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfigurations.class);
    public static final String UNDEFINED = "**UNDEFINED**";

    @Autowired
    @Qualifier("databaseProperties")
    private Properties databaseProperties;

    @Autowired
    private MultiTenantConnectionProvider multiTenantConnectionProvider;

    @Autowired
    private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;

    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider() {
        return new MultiTenantConnectionProviderImpl();
    }

    @Bean
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new CurrentTenantIdentifierResolverImpl();
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        try {
            return getDataSource();
        } catch (Exception pve) {
            throw new RuntimeException("Unable to create  datasource", pve);
        }
    }

    protected DataSource getDataSource() throws PropertyVetoException, SQLException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        //dataSource.getConnection().setAutoCommit(false);
        dataSource.setDriverClass(getDriverClassName());
        dataSource.setJdbcUrl(getUrl());
        dataSource.setUser(getUser());
        dataSource.setPassword(getPassword());
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setTestConnectionOnCheckout(true);
        dataSource.setPreferredTestQuery(getDatabaseValidationQuery());
        dataSource.setIdleConnectionTestPeriod(1800000);
        dataSource.setMinPoolSize(15);
        dataSource.setMaxPoolSize(50);
        return dataSource;
    }


    protected Map<String, Object> getJpaProperties() {
        Map<String, Object> map = new HashMap<>();
        map.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
        map.put("hibernate.tenant_identifier_resolver", currentTenantIdentifierResolver);
        map.put("hibernate.multiTenancy", "DATABASE");
        return map;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws SQLException {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform(getDatabaseDialect().getName());
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(getEntityPackage());
        factory.setJpaPropertyMap(getJpaProperties());
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    protected Class<? extends Dialect> getDatabaseDialect() {
        return MySQL5Dialect.class;
    }

    public String getUrl(String prefix) {
        return lookupProperty(prefix, "database.url", UNDEFINED);
    }

    public String getUrl() {
        return lookupProperty(null, "database.url", UNDEFINED);
    }

    public String getUser(String prefix) {
        return lookupProperty(prefix, "database.username", UNDEFINED);
    }

    public String getUser() {
        return lookupProperty(null, "database.username", UNDEFINED);
    }

    public String getPassword(String prefix) {
        return lookupProperty(prefix, "database.password", UNDEFINED);
    }

    public String getPassword() {
        return lookupProperty(null, "database.password", UNDEFINED);
    }

    public String getDriverClassName() {
        return lookupProperty(null, "database.driverClassName", UNDEFINED);
    }

    public String getEntityPackage() {
        return getEnvironment().getProperty("entity.package", UNDEFINED);
    }

    public String getDialect() {
        return getEnvironment().getProperty("database.dialect", UNDEFINED);
    }

    public String getDatabaseVendor() {
        return getEnvironment().getProperty("database.vendor", UNDEFINED);
    }

    public String getDatabaseValidationQuery() {
        return getEnvironment().getProperty("database.validation.query", UNDEFINED);
    }

    /**
     * @return the environment
     */
    protected Properties getEnvironment() {
        return databaseProperties;
    }

    private String lookupProperty(String prefix, String propertyName, String defaultValue) {
        if (StringUtils.hasText(prefix)) {
            propertyName = prefix + "." + propertyName;
        }
        return getEnvironment().getProperty(propertyName, defaultValue);
    }

    @Bean
    public Properties databaseProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("META-INF/spring/defaultdb.properties"));
        Properties properties;
        try {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load database configuration");
        }
        return properties;
    }

    @Bean
    public DataSourcePool dataSourcePool() throws FileNotFoundException, YamlException {
        DataSourcePool pool = new DataSourcePool();

        InputStream is = DatabaseConfigurations.class.getResourceAsStream("/META-INF/spring/tenants.yml");
        TenantDataSourceRepository dataSourceRepository = TenantDataSourceRepository.fromInputStream(is);

        dataSourceRepository.getTenantDataSources().forEach(ds -> {
            try {
                ComboPooledDataSource dataSource = new ComboPooledDataSource();
                //dataSource.getConnection().setAutoCommit(false);
                dataSource.setDriverClass(ds.driverClassName);
                dataSource.setJdbcUrl(ds.jdbcUrl);
                dataSource.setUser(ds.username);
                dataSource.setPassword(ds.password);
                dataSource.setTestConnectionOnCheckin(true);
                dataSource.setTestConnectionOnCheckout(true);
                dataSource.setPreferredTestQuery(ds.validationQuery);
                dataSource.setIdleConnectionTestPeriod(1800000);
                dataSource.setMinPoolSize(15);
                dataSource.setMaxPoolSize(50);
                pool.addTenantDataSource(ds.id, dataSource);
            } catch (Exception e) {
                LOGGER.error("Unable to initialize datasource for {}", ds.name, e);
            }

        });
        return pool;
    }
}
