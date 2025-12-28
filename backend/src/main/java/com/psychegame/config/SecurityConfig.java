package com.psychegame.config;

import com.psychegame.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置
 *
 * @author Psyche Game Team
 * @since 2025-12-28
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 关闭CSRF保护
            .csrf(AbstractHttpConfigurer::disable)
            
            // 允许跨域请求
            .cors(AbstractHttpConfigurer::disable)
            
            // 配置授权规则
            .authorizeHttpRequests(authorize -> authorize
                // 允许所有用户访问的路径
                .requestMatchers("/api/user/register", "/api/user/login", "/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**", "/h2-console/**").permitAll()
                // 允许所有请求访问游戏和论坛资源
                .requestMatchers("/api/game/**", "/api/forum/**").permitAll()
                // 允许所有请求访问
                .anyRequest().permitAll()
            )
            
            // 添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 配置会话管理
            .sessionManagement(session -> session
                // 无状态会话
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}
