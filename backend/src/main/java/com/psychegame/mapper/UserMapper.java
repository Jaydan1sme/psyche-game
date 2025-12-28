package com.psychegame.mapper;

import com.psychegame.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author Psyche Game Team
 * @since 2025-12-28
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User selectById(@Param("id") Long id);
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User selectByEmail(@Param("email") String email);
    
    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<User> selectAll();
    
    /**
     * 插入用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);
    
    /**
     * 更新用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int update(User user);
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新用户最后登录时间
     *
     * @param id 用户ID
     * @return 影响行数
     */
    int updateLastLoginTime(@Param("id") Long id);
}
