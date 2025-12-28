package com.psychegame.service.impl;

import com.psychegame.entity.User;
import com.psychegame.mapper.UserMapper;
import com.psychegame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 *
 * @author Psyche Game Team
 * @since 2025-12-28
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    // 密码编码器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
    
    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }
    
    @Override
    public User register(User user) {
        // 验证用户名是否已存在
        if (getUserByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 验证邮箱是否已存在
        if (getUserByEmail(user.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 设置默认值
        if (user.getNickname() == null) {
            user.setNickname(user.getUsername());
        }
        
        if (user.getAvatar() == null) {
            user.setAvatar("/default-avatar.png");
        }
        
        user.setRole("user");
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // 加密密码
        user.setPassword(encodePassword(user.getPassword()));
        
        // 插入用户
        userMapper.insert(user);
        
        return user;
    }
    
    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码
        if (!verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证用户状态
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 更新最后登录时间
        updateLastLoginTime(user.getId());
        
        return user;
    }
    
    @Override
    public User updateUser(User user) {
        // 查询原用户
        User originalUser = getUserById(user.getId());
        if (originalUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证用户名是否已被其他用户使用
        User existingUser = getUserByUsername(user.getUsername());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 验证邮箱是否已被其他用户使用
        existingUser = getUserByEmail(user.getEmail());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 更新用户信息
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
        
        return getUserById(user.getId());
    }
    
    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean updateLastLoginTime(Long id) {
        return userMapper.updateLastLoginTime(id) > 0;
    }
    
    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
