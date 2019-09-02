package blog.config;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.CompositeFilter;

import blog.support.MyUserDetailsService;;

@Configuration
@EnableOAuth2Client
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //  启用方法级别的权限认证

public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	OAuth2ClientContext oauth2ClientContext;
	
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //  允许所有用户访问"/"和"/index.html"
        http.authorizeRequests()
                .antMatchers("/", "/index","/webjars/**","/loginform/**","/css/**","/images/**").permitAll() //loginform 对应登陆界面配套的板式内容
                .anyRequest().authenticated()   // 其他地址的访问均需验证权限
                .and()
                .formLogin()
                .loginPage("/login")   //  登录页
                .failureUrl("/login-error").permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/index")
                //oAuth2
                .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)
        ;
    }

    
    /**
     * OAuth2 methods
     * @return
     */
	private Filter ssoFilter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		//filters.add(ssoFilter(facebook(), "/login/facebook"));
		filters.add(ssoFilter(weibo(), "/login/weibo"));
		filters.add(ssoFilter(github(), "/login/github"));
		filters.add(ssoFilter(wechat(), "/login/wechat"));
		filter.setFilters(filters);
		return filter;
	}
	
	private Filter ssoFilter(ClientResources client, String path) {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
		filter.setRestTemplate(template);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(
				client.getResource().getUserInfoUri(), client.getClient().getClientId());
		tokenServices.setRestTemplate(template);
		filter.setTokenServices(tokenServices);
		return filter;
	}
	
	@Bean
	@ConfigurationProperties("github")
	public ClientResources github() {
	  return new ClientResources();
	}
	
	/*
	@Bean
	@ConfigurationProperties("facebook")
	public ClientResources facebook() {
	  return new ClientResources();
	}*/
	
	@Bean
	@ConfigurationProperties("wechat")
	public ClientResources wechat() {
	  return new ClientResources();
	}
	
	@Bean
	@ConfigurationProperties("weibo")
	public ClientResources weibo() {
	  return new ClientResources();
	}
	
	@Bean
	public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(
			OAuth2ClientContextFilter filter) {
		FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<OAuth2ClientContextFilter>();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}
	
	/**
	 * Local login methods
	 */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}



/**
 * ClientResources used by oAuth2
 * @author xumingkuan
 *
 */
class ClientResources {

	  @NestedConfigurationProperty
	  private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

	  @NestedConfigurationProperty
	  private ResourceServerProperties resource = new ResourceServerProperties();

	  public AuthorizationCodeResourceDetails getClient() {
	    return client;
	  }

	  public ResourceServerProperties getResource() {
	    return resource;
	  }
	}