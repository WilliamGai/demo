package com.sincetimes.website.app.security.config;

//import org.springframework.context.annotation.Configuration;
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests().anyRequest().permitAll();
//		.antMatchers("/all","/info","/ws").permitAll()
//		.anyRequest().authenticated()
//		.and().formLogin().permitAll();
//	}
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
//		auth.inMemoryAuthentication().withUser("a").password("b").roles("admin");
//	}
//}
