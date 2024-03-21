package com.app.config;

import com.app.Jwt.JwtAuthenticationFilter;
import com.app.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    // Configuración del filtro de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // Deshabilita CSRF
                .csrf(csrf -> csrf.disable())
                // Establece la política de creación de sesiones como STATELESS (sin estado)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configura el proveedor de autenticación y el filtro JWT
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    //Creamos el bean del authentication manager el cual va a encargarse de las autenticaciones
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //Creamos el proveedor de autenticacion, este proveedor DAO busca a los usuarios en la BD
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    //Utilizamos BCrypt para codificar los passwords.
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    //Crear usuarios en memoria
    /*@Bean
    public UserDetailsService userDetailsService(){
        List<UserDetails> userDetailsList = new ArrayList<>();

        *//*userDetailsList.add(User.withUsername("elian")
                .password("1234")
                .roles("ADMIN")
                .authorities("READ", "CREATE")
                .build());

        userDetailsList.add(User.withUsername("valentina")
                .password("1234")
                .roles("USER")
                .authorities("READ")
                .build());*//*

        return new InMemoryUserDetailsManager(userDetailsList);
    }*/

    //Filtro de seguridad seteando aqui las autorizaciones o permisos requeridos para cada endpoint
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    //configuro endpoints publicos
                    http.requestMatchers(HttpMethod.GET, "/auth/hello").permitAll();

                    //configuro endpoints privados
                    http.requestMatchers(HttpMethod.GET, "/auth/helloSecured").hasAuthority("READ");

                    //configuro endpoints no especificados
                    http.anyRequest().authenticated();
                    //http.anyRequest().denyAll();

                })
                .httpBasic(Customizer.withDefaults())
                .build();
    }*/
}
