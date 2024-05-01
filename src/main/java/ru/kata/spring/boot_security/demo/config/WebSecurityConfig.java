package ru.kata.spring.boot_security.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SuccessUserHandler successUserHandler;
    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserDetailsService userService) {
        this.successUserHandler = successUserHandler;
        this.userDetailsService = userService;
    }

    //конфигурируем Spring_Security
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //конфигурируем Авторизацию
                .authorizeRequests()
                .antMatchers("/error").permitAll()
                .antMatchers("/").authenticated()
                .anyRequest().hasRole("ADMIN")
                //конфигурируем login
                .and()
                .formLogin().loginPage("/login").usernameParameter("email")
                .loginProcessingUrl("/process_login")
                .defaultSuccessUrl("/", true).failureUrl("/login?error")
                .permitAll()
                //конфигурируем logout
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout")
                .permitAll();
    }

    //Настроаиваем аутентификацию
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // аутентификация через Базу Данных
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(getPasswordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }

    // аутентификация inMemory
/*    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("user")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }*/
}