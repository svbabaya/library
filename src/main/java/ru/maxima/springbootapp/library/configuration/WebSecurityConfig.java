package ru.maxima.springbootapp.library.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.maxima.springbootapp.library.services.PersonDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    public WebSecurityConfig(PersonDetailsService personDetailsService) {
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .anonymous().principal("Guest")
                .and()
                .csrf().disable()
                .authorizeHttpRequests()
                    .requestMatchers("/admin", "/people/**", "/books/**").hasRole("ADMIN")
                    .requestMatchers("/reader/**").hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/**").permitAll()
                .and()
                    .formLogin()
                    .loginPage("/login")
//                    .defaultSuccessUrl("/home", true)
                    .failureUrl("/login?error")
                    .permitAll()
                .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .permitAll();
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User
//                .withUsername("user")
//                .password("user")
//                .roles("USER")
//                .build();
//        UserDetails admin = User
//                .withUsername("admin")
//                .password("admin")
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }

    @Bean
    protected PasswordEncoder getPasswordEncoder() {

        return NoOpPasswordEncoder.getInstance();
    }

}
