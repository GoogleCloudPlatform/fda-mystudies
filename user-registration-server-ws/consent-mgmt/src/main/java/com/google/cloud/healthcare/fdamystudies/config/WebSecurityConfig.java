package com.google.cloud.healthcare.fdamystudies.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private AuthenticationEntryPoint authEntryPoint;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.cors();
    //		// All requests send to the Web Server request must be authenticated
    //		http.authorizeRequests().anyRequest().authenticated();
    //
    //		// Use AuthenticationEntryPoint to authenticate user/password
    //		http.httpBasic().authenticationEntryPoint(authEntryPoint);
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  //	@Autowired
  //	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
  //		String appPd = this.passwordEncoder().encode(appConfig.getAppPasswd());
  //		InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> mngConfig =
  // auth.inMemoryAuthentication();
  //		UserDetails u1 = User.withUsername(appConfig.getAppUserId()).password(appPd).roles("USER")
  //				.build();
  //		mngConfig.withUser(u1);
  //	}

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/**");
  }
  //	@Bean
  //	public FilterRegistrationBean<AuthenticationFilter> loggingFilter(){
  //	    FilterRegistrationBean<AuthenticationFilter> authenticationBean = new
  // FilterRegistrationBean<>();
  //	    authenticationBean.setFilter(new AuthenticationFilter());
  //	     authenticationBean.addUrlPatterns("/user/*");
  //	    // authenticationBean.addUrlPatterns("/query/list");
  //	    return authenticationBean;
  //	}

}
