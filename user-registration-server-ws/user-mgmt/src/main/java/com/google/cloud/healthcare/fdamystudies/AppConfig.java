/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies;

// @Configuration
// @EnableTransactionManagement
// @EnableJpaRepositories(
//    basePackages = "com.btc.fda.repository",
//    entityManagerFactoryRef = "entityManagerFactory",
//    transactionManagerRef = "transactionManager")
public class AppConfig {

  /* @Autowired private Environment env;

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    String driver = "com.mysql.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://localhost:3306/mystudies_userregistration";
    String username = "root";
    String password = "boston098";

    dataSource.setDriverClassName(driver);
    dataSource.setUrl(jdbcUrl);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    return dataSource;
  }

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      EntityManagerFactoryBuilder builder, DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages("com.btc.fda.model")
        .persistenceUnit("dm")
        .build();
  }

  @Bean
  @Primary
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  //	@Bean
  public TokenStore tokenStore() {
    return new JdbcTokenStore(dataSource());
  }

  //	@Primary
  //	@Bean
  public RemoteTokenServices tokenService() {
    RemoteTokenServices tokenService = new RemoteTokenServices();
    tokenService.setCheckTokenEndpointUrl(
        "http://localhost:8080/spring-security-oauth-server/oauth/check_token");
    //	    tokenService.setClientId("fooClientIdPassword");
    //	    tokenService.setClientId("clientIdPassword");
    tokenService.setClientId("sampleClientId");
    tokenService.setClientSecret("secret");
    return tokenService;
  }*/
}
