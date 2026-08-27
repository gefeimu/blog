package com.blog.security;

import com.blog.common.BizException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录失败锁定：同一账号连续失败 {@link #MAX_ATTEMPTS} 次后锁定 {@link #LOCK_DURATION}，
 * 锁定期间即使密码正确也拒绝登录，防止暴力破解管理员密码。
 * <p>
 * 单实例内存实现（博客仅一个后端容器，无需 Redis）；条目数达到阈值时惰性清理过期锁定，
 * 避免 Map 无限增长。
 */
@Service
public class LoginAttemptService {

    /** 最大连续失败次数 */
    private static final int MAX_ATTEMPTS = 5;

    /** 锁定时长 */
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    /** 访问计数达到该阈值时触发一次过期清理 */
    private static final int CLEANUP_THRESHOLD = 1000;

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    private final AtomicInteger accessCount = new AtomicInteger();

    /** 检查是否已锁定，锁定中抛出 429 并提示剩余分钟数 */
    public void checkLocked(String key) {
        Attempt attempt = attempts.get(key);
        if (attempt == null || attempt.lockUntil == null) {
            return;
        }
        if (Instant.now().isBefore(attempt.lockUntil)) {
            long minutes = Duration.between(Instant.now(), attempt.lockUntil).toMinutes() + 1;
            throw BizException.tooManyRequests("失败次数过多，账号已锁定，请 " + minutes + " 分钟后重试");
        }
        attempts.remove(key);
    }

    /** 记录一次登录失败，达到阈值则触发锁定 */
    public void recordFailure(String key) {
        attempts.compute(key, (k, a) -> {
            if (a == null) {
                a = new Attempt();
            }
            a.failCount++;
            if (a.failCount >= MAX_ATTEMPTS) {
                a.lockUntil = Instant.now().plus(LOCK_DURATION);
                a.failCount = 0;
            }
            return a;
        });
        if (accessCount.incrementAndGet() % CLEANUP_THRESHOLD == 0) {
            cleanupExpired();
        }
    }

    /** 登录成功清除该账号的失败记录 */
    public void recordSuccess(String key) {
        attempts.remove(key);
    }

    /** 清理已过期的锁定条目 */
    private void cleanupExpired() {
        Instant now = Instant.now();
        attempts.entrySet().removeIf(e -> {
            Attempt attempt = e.getValue();
            return attempt.lockUntil != null && !now.isBefore(attempt.lockUntil);
        });
    }

    private static class Attempt {
        int failCount;
        Instant lockUntil;
    }
}
