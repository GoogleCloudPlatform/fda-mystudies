/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
@ConditionalOnProperty(
    value = "hibernate.transaction.management.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class HibernateConfiguration {
  @Value("${spring.datasource.driverClassName}")
  private String driverClassName;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.jpa.database-platform}")
  private String dialect;

  @Value("${spring.jpa.show-sql:false}")
  private String showSql;

  @Value("${spring.jpa.hibernate.ddl-auto:none}")
  private String autoDdl;

  @Value("${entitymanager.packagesToScan}")
  private String scanPackages;

  @Value("${spring.jpa.properties.hibernate.hbm2ddl.import_files:}")
  private String dataSqlFile;

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(driverClassName);
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    return dataSource;
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());
    sessionFactory.setPackagesToScan(scanPackages);
    Properties hibernateProperties = new Properties();
    hibernateProperties.put("hibernate.dialect", dialect);
    hibernateProperties.put("hibernate.show_sql", showSql);
    hibernateProperties.put("hibernate.hbm2ddl.auto", autoDdl);

    if (StringUtils.isNotEmpty(dataSqlFile)) {
      hibernateProperties.put("hibernate.hbm2ddl.import_files", dataSqlFile);
    }

    sessionFactory.setHibernateProperties(hibernateProperties);

    return sessionFactory;
  }

  @Bean
  public HibernateTransactionManager transactionManager() {
    HibernateTransactionManager transactionManager = new HibernateTransactionManager();
    transactionManager.setSessionFactory(sessionFactory().getObject());
    return transactionManager;
  }
}
