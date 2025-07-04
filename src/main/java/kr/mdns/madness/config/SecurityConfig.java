package kr.mdns.madness.config;

import static org.springframework.security.config.Customizer.withDefaults;

import kr.mdns.madness.security.JwtAuthenticationFilter;
import kr.mdns.madness.security.JwtAuthorizationFilter;
import kr.mdns.madness.security.JwtUtil;
import kr.mdns.madness.security.SecurityExceptionHandler;
import lombok.RequiredArgsConstructor;
import kr.mdns.madness.security.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtUtil jwtUtil;
        private final CustomUserDetailsService userDetailsService;
        private final SecurityExceptionHandler securityExceptionHandler;

        // public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService
        // userDetailsService) {
        // this.jwtUtil = jwtUtil;
        // this.userDetailsService = userDetailsService;
        // }

        @Bean
        public WebSecurityCustomizer h2Ignore() {
                return web -> web.ignoring()
                                .requestMatchers("/h2-console/**");
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig)
                        throws Exception {
                AuthenticationManager authManager = authConfig.getAuthenticationManager();

                JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(authManager);
                authFilter.setFilterProcessesUrl("/auth/signin");

                JwtAuthorizationFilter authzFilter = new JwtAuthorizationFilter(jwtUtil, userDetailsService,
                                securityExceptionHandler);

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(withDefaults())
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(ex -> ex.authenticationEntryPoint(securityExceptionHandler)
                                                .accessDeniedHandler(securityExceptionHandler))
                                .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
                                .authorizeHttpRequests(a -> a
                                                .requestMatchers("/ws/**").permitAll()
                                                .requestMatchers("/pub/**", "/sub/**").permitAll()
                                                .requestMatchers("/auth/signin",
                                                                "/auth/refresh",
                                                                "/member",
                                                                "/channel/search",
                                                                "/member/check/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .authenticationManager(authManager)
                                .addFilter(authFilter)
                                .addFilterBefore(authzFilter, JwtAuthenticationFilter.class);

                return http.build();
        }
}
