package com.example.imagicspringaws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ClickService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOperations;

    private static final String KEY = "leaderboard";

    public ClickService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public void initUserIfAbsent(String username) {
        Double score = zSetOperations.score(KEY, username);
        if (score == null) {
            zSetOperations.add(KEY, username, 0.0);
        }
    }

    public void incrementClick(String username) {
        zSetOperations.incrementScore(KEY, username, 1);
    }

    public int getClicks(String username) {
        Double score = zSetOperations.score(KEY, username);
        return score != null ? score.intValue() : 0;
    }

    public List<Map.Entry<String, Integer>> getTopUsers(int limit) {
        Set<ZSetOperations.TypedTuple<String>> set = zSetOperations.reverseRangeWithScores(KEY, 0, limit - 1);
        if (set == null) return List.of();

        return set.stream()
                .map(e -> Map.entry(e.getValue(), e.getScore().intValue()))
                .toList();
    }
}
