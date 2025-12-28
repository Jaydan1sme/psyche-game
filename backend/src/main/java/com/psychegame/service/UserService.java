package com.psychegame.service;

import com.psychegame.entity.User;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author Psyche Game Team
 * @since 2025-12-28
 */
public interface UserService {
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);
    
    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册成功的用户
     */
    User register(User user);
    
    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户和JWT令牌
     */
    User login(String username, String password);
    
    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新后的用户
     */
    User updateUser(User user);
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    boolean deleteUser(Long id);
    
    /**
     * 更新用户最后登录时间
     *
     * @param id 用户ID
     * @return 更新结果
     */
    boolean updateLastLoginTime(Long id);
    
    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 验证结果
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);
    
    /**
     * 加密密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    String encodePassword(String password);
}
