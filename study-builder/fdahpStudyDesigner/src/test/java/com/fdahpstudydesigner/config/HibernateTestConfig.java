/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({"com.fdahpstudydesigner.dao"})
public class HibernateTestConfig {

  @Bean(name = "dataSource")
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl(
        "jdbc:h2:mem:testdb;IFEXISTS=FALSE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL;NON_KEYWORDS=value");
    dataSource.setUsername("mockit");
    dataSource.setPassword("password");

    // Ref https://groups.google.com/g/h2-database/c/yxnv64Ak-u8/m/n-kqYV_yBQAJ
    org.h2.engine.Mode mode = org.h2.engine.Mode.getInstance("MySQL");
    mode.limit = true;
    return dataSource;
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());
    sessionFactory.setPackagesToScan("com.fdahpstudydesigner.bo");
    sessionFactory.setHibernateProperties(hibernateProperties());

    return sessionFactory;
  }

  private final Properties hibernateProperties() {
    Properties hibernateProperties = new Properties();
    hibernateProperties.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
    hibernateProperties.setProperty("hibernate.connection.username", "root");
    hibernateProperties.setProperty("hibernate.connection.password", "password");
    hibernateProperties.setProperty(
        "hibernate.connection.url",
        "jdbc:h2:mem:testdb;IFEXISTS=FALSE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;DEFAULT_LOCK_TIMEOUT=10000;LOCK_MODE=0");
    hibernateProperties.setProperty(
        "hibernate.dialect", "com.fdahpstudydesigner.config.H2DialectExtended");
    hibernateProperties.setProperty("hibernate.format_sql", "false");
    hibernateProperties.setProperty("hibernate.show_sql", "false");
    hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");

    hibernateProperties.setProperty("hibernate.hbm2ddl.import_files", "import.sql");
    hibernateProperties.setProperty("hibernate.c3p0.acquire_increment", "2");
    hibernateProperties.setProperty("hibernate.c3p0.max_size", "500");

    hibernateProperties.setProperty("hibernate.c3p0.min_size", "5");
    hibernateProperties.setProperty("hibernate.c3p0.timeout", "1800");
    hibernateProperties.setProperty("hibernate.c3p0.idle_test_period", "200");
    return hibernateProperties;
  }

  @Bean
  public PlatformTransactionManager hibernateTransactionManager() {
    HibernateTransactionManager transactionManager = new HibernateTransactionManager();
    transactionManager.setSessionFactory(sessionFactory().getObject());
    return transactionManager;
  }
}
