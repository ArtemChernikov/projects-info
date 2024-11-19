package ru.projects.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.projects.view.login.LoginView;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration extends VaadinWebSecurity {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("12345"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider inMemoryAuthProvider = new DaoAuthenticationProvider();
        inMemoryAuthProvider.setUserDetailsService(inMemoryUserDetailsManager(passwordEncoder()));
        inMemoryAuthProvider.setPasswordEncoder(passwordEncoder());

        DaoAuthenticationProvider dbAuthProvider = new DaoAuthenticationProvider();
        dbAuthProvider.setUserDetailsService(userDetailsServiceImpl);
        dbAuthProvider.setPasswordEncoder(passwordEncoder());

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(inMemoryAuthProvider)
                .authenticationProvider(dbAuthProvider)
                .build();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll());

        http.authorizeHttpRequests(
                authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/api/backup")).permitAll());

        http.authorizeHttpRequests(
                authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/api/restore")).permitAll());

        // Icons from the line-awesome addon
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll());

        super.configure(http);
        setLoginView(http, LoginView.class);
    }

}
