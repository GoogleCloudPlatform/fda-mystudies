package com.google.cloud.healthcare.fdamystudies.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({"com.google.cloud.healthcare.fdamystudies"})
public class HibernateConfiguration {
  @Value("${spring.datasource.driver-class-name}")
  private String DRIVER;

  @Value("${spring.datasource.password}")
  private String PASSWORD;

  @Value("${spring.datasource.url}")
  private String URL;

  @Value("${spring.datasource.username}")
  private String USERNAME;

  @Value("${spring.jpa.properties.hibernate.dialect}")
  private String DIALECT;

  @Value("${spring.jpa.show-sql}")
  private String SHOW_SQL;

  @Value("${spring.jpa.hibernate.ddl-auto}")
  private String DDL_AUTO;

  @Value("${spring.jpa.properties.entitymanager.packagesToScan}")
  private String PACKAGES_TO_SCAN;

  @Bean
  public LocalSessionFactoryBean sessionFactory(DataSource da) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(da);
    sessionFactory.setPackagesToScan("com.google.cloud.healthcare.fdamystudies.consent.model");
    Properties hibernateProperties = new Properties();
    hibernateProperties.put("hibernate.dialect", DIALECT);
    hibernateProperties.put("hibernate.show_sql", SHOW_SQL);
    hibernateProperties.put("hibernate.hbm2ddl.auto", DDL_AUTO);
    sessionFactory.setHibernateProperties(hibernateProperties);

    return sessionFactory;
  }

  @Bean
  public HibernateTransactionManager transactionManager(SessionFactory sf) {
    return new HibernateTransactionManager(sf);
  }
}
