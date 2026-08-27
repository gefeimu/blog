package com.blog.controller;

import com.blog.common.BizException;
import com.blog.entity.AdminUser;
import com.blog.mapper.AdminUserMapper;
import com.blog.security.JwtUtil;
import com.blog.security.LoginAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdminUserMapper adminUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public AuthController(AdminUserMapper adminUserMapper,
                          BCryptPasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          LoginAttemptService loginAttemptService) {
        this.adminUserMapper = adminUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req.username() == null || req.username().isBlank()
                || req.password() == null || req.password().isBlank()) {
            throw BizException.badRequest("用户名和密码不能为空");
        }
        String key = req.username().trim().toLowerCase();
        loginAttemptService.checkLocked(key);
        AdminUser user = adminUserMapper.selectByUsername(req.username().trim());
        if (user == null || !passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            loginAttemptService.recordFailure(key);
            throw BizException.unauthorized("用户名或密码错误");
        }
        loginAttemptService.recordSuccess(key);
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "nickname", user.getNickname() == null ? user.getUsername() : user.getNickname()
        ));
    }

    public record LoginRequest(String username, String password) {
    }
}
