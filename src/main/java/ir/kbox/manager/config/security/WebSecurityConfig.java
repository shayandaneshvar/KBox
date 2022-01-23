package ir.kbox.manager.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)

@RequiredArgsConstructor(onConstructor_ = @Autowired)// lombok
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;


    @Override // authN
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    //    @Primary
//    @Qualifier()
//    @Profile()
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override // general
    public void configure(WebSecurity web) throws Exception {
        web.debug(false)
                .ignoring()
                .antMatchers("/**/*.svg", "/**/*.js", "/**/*.css");
    }

    @Override // authZ
    protected void configure(HttpSecurity http) throws Exception {
        //XSRF-TOKEN
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/", "/index", "index.html", "/home", "/auth/**","/login*", "/error")
                .permitAll()
                .anyRequest()
                .authenticated();

        http.formLogin()
                .defaultSuccessUrl("/home")
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .failureUrl("/auth/login?error=true")
                .and()
                .logout()
                .logoutUrl("/auth/logout")
                .deleteCookies("JSESSIONID");
    }
}
