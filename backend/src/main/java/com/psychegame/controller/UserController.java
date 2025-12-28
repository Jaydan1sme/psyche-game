package com.psychegame.controller;

import com.psychegame.entity.User;
import com.psychegame.service.UserService;
import com.psychegame.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author Psyche Game Team
 * @since 2025-12-28
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User registeredUser = userService.register(user);
            result.put("code", 200);
            result.put("message", "注册成功");
            result.put("data", registeredUser);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "注册失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 用户登录
     *
     * @param loginData 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            
            User user = userService.login(username, password);
            
            // 生成JWT token
            String token = jwtUtil.generateAccessToken(user.getUsername(), user.getId(), null);
            
            // 准备返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", user);
            
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", data);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/info/{id}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                result.put("code", 404);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestAttribute("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                result.put("code", 404);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User updatedUser = userService.updateUser(user);
            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("data", updatedUser);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 获取用户列表
     *
     * @return 用户列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getUserList() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = userService.getAllUsers();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", users);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                result.put("code", 200);
                result.put("message", "删除成功");
            } else {
                result.put("code", 404);
                result.put("message", "用户不存在");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
