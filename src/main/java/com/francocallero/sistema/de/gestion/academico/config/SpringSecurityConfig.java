package com.francocallero.sistema.de.gestion.academico.config;

import com.francocallero.sistema.de.gestion.academico.config.filtros.FiltroPerfilCompleto;
import com.francocallero.sistema.de.gestion.academico.config.filtros.FiltroValidacionCambioContrasenia;
import com.francocallero.sistema.de.gestion.academico.persona.Persona;
import com.francocallero.sistema.de.gestion.academico.persona.Rol;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoUsuarios;
import com.francocallero.sistema.de.gestion.academico.servicios.UsuarioService;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain mainConfig(HttpSecurity http){
        http.csrf(Customizer.withDefaults())
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/contrasenias/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("admin")
                        .requestMatchers("/docente/**").hasRole("docente")
                        .requestMatchers("/alumno/**").hasRole("alumno")
                        .anyRequest().authenticated()
        )
                .formLogin(form-> form.loginPage("/login").permitAll())
                .logout(logoutConfig-> logoutConfig
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .addFilterAfter(filtroValidacionCambioContrasenia(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(filtroPerfilCompleto(),
                        FiltroValidacionCambioContrasenia.class);

        return http.build();
    }

    @Value("${spring.jpa.hibernate.ddl-auto:update}")
    private String ddlAutoValue;
    @Bean
    public UsuarioService userDetailsService(RepoUsuarios repoUsuarios){
        UsuarioService usuarioService = new UsuarioService(repoUsuarios, passwordEncoder());


        if(ddlAutoValue.equalsIgnoreCase("create-drop")){
            Usuario adminUser = new Usuario("admin", "admin");

            Persona admin = new Persona();
            admin.setRol(Rol.ADMIN);
            adminUser.setPersona(admin);

            adminUser.agregarRol("admin");

            usuarioService.crearUser(adminUser);
        }

        return usuarioService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return  PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Autowired
    private RepoUsuarios repoUsuarios;

    public FiltroValidacionCambioContrasenia filtroValidacionCambioContrasenia(){
        return new FiltroValidacionCambioContrasenia(repoUsuarios);
    }

    public FiltroPerfilCompleto filtroPerfilCompleto(){
        return new FiltroPerfilCompleto();
    }

}
