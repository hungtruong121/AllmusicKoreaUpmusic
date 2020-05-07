package com.upmusic.web.config;

import com.upmusic.web.handler.UPMusicAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	Environment env;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	OAuth2ClientContext oauth2ClientContext;

	@Autowired
	private UPMusicAuthenticationSuccessHandler successHandler;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		http.csrf().disable();
//		http.addFilterBefore(filter, CsrfFilter.class);

//        .csrf().disable().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

		http
				.antMatcher("/**")
				.authorizeRequests()
				//.antMatchers("/**").permitAll()
				.antMatchers("/").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/fonts/**").permitAll()
				.antMatchers("/img/**").permitAll()
				.antMatchers("/js/**").permitAll()
				.antMatchers("/plugins/**").permitAll()
				.antMatchers("/webjars/**").permitAll()
				.antMatchers("/swagger-ui.html/**").permitAll()
				.antMatchers("/terms/**").permitAll()
				.antMatchers("/auth/**").permitAll()
				.antMatchers("/error/**").permitAll()
				.antMatchers("/dummy/**").permitAll()
				.antMatchers("/music").permitAll()
				.antMatchers("/music/*").permitAll()
				.antMatchers("/music/store/request/*").permitAll()
				.antMatchers("/music/artist/**").permitAll()
				.antMatchers("/music/album/**").permitAll()
				.antMatchers("/music/track/*").permitAll()
				.antMatchers("/music/guide_vocal/*").permitAll()
				.antMatchers("/music/vocal_casting/*").permitAll()
				.antMatchers("/video/**").permitAll()
				.antMatchers("/crowd_funding/**").permitAll()
				.antMatchers("/upm_news/**").permitAll()
				.antMatchers("/search/**").permitAll()
				.antMatchers("/api/**").permitAll()
				.antMatchers("/component/**").permitAll()
				.antMatchers("/feed/share/**").permitAll()
				// .antMatchers("/upload/**").permitAll()
				.antMatchers("/admin/**").hasRole(UPMusicConstants.ROLE_ADMIN)
				//.antMatchers("/admin/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.csrf().ignoringAntMatchers("/api/**").ignoringAntMatchers("/auth/**")
				.and()
				.csrf().ignoringAntMatchers("/pay/**").ignoringAntMatchers("/auth/**")
				.and()
				.exceptionHandling()
				.and()
				.formLogin().usernameParameter("email").passwordParameter("password").loginPage("/auth/login").successHandler(successHandler).permitAll()
				.and()
				.logout().logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout")).logoutSuccessUrl("/")
				.and()
				.rememberMe().key("unique-and-secret").rememberMeCookieName("upmusic-remember-me-cookie").tokenValiditySeconds(60 * 60 * 24 * 7)
				.and()
				.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);

//        if (!env.getActiveProfiles().equals("default")) {
//		if (Arrays.asList(env.getActiveProfiles()).contains("dev") ||
//				Arrays.asList(env.getActiveProfiles()).contains("prod") ) {
			if(env.acceptsProfiles("dev","prod")){

			http.requiresChannel().anyRequest().requiresSecure()
					.and().portMapper().http(8080).mapsTo(8443);
		}

	}

	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

	@Bean
	public AuthenticationManager customAuthenticationManager() throws Exception {
		return authenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@ConfigurationProperties("facebook")
	public ClientResources facebook() {
		return new ClientResources();
	}

	@Bean
	@ConfigurationProperties("google")
	public ClientResources google() {
		return new ClientResources();
	}

	@Bean
	@ConfigurationProperties("kakao")
	public ClientResources kakao() {
		return new ClientResources();
	}

	@Bean
	@ConfigurationProperties("naver")
	public ClientResources naver() {
		return new ClientResources();
	}

	@Bean
	public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<OAuth2ClientContextFilter>();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

	private Filter ssoFilter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		filters.add(ssoFilter(facebook(), "/auth/facebook", "/auth/facebook/complete"));
		filters.add(ssoFilter(google(), "/auth/google", "/auth/google/complete"));
		filters.add(ssoFilter(kakao(), "/auth/kakao", "/auth/kakao/complete"));
		filters.add(ssoFilter(naver(), "/auth/naver", "/auth/naver/complete"));
		filter.setFilters(filters);
		return filter;
	}

	private Filter ssoFilter(ClientResources client, String path, String redirectUrl) {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);

		filter.setRestTemplate(template);
		filter.setTokenServices(new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId()));
		filter.setAuthenticationSuccessHandler((request, response, authentication) -> response.sendRedirect(redirectUrl.toString()));
		filter.setAuthenticationFailureHandler((request, response, exception) -> response.sendRedirect("/error"));
		return filter;
	}

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

}
