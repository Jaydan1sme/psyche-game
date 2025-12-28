package com.psychegame.filter;

import com.psychegame.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 *
 * @author Psyche Game Team
 * @since 2025-12-28
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwtToken = null;
        
        // 检查Authorization头格式
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (Exception e) {
                System.err.println("JWT令牌解析失败: " + e.getMessage());
            }
        }
        
        // 如果令牌有效且用户未认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 验证令牌
                if (!jwtUtil.isTokenExpired(jwtToken)) {
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            new ArrayList<>()
                    );
                    
                    // 设置认证详情
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 将认证信息设置到SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                    // 将用户ID添加到请求属性
                    Long userId = jwtUtil.extractUserId(jwtToken);
                    request.setAttribute("userId", userId);
                    request.setAttribute("username", username);
                }
            } catch (Exception e) {
                System.err.println("JWT令牌验证失败: " + e.getMessage());
            }
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
