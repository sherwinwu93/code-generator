package com.wusd.codegenerator.controller;

import com.wusd.codegenerator.entity.dto.CodeRuleBrief;
import com.wusd.codegenerator.mapper.CodeRuleBriefMapper;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Wusd
 * @date 2025/6/25
 * @description
 */
@RequestMapping("/test")
@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/datasource")
    public Map datasource() {
        Map<String, Object> stringObjectMap = jdbcTemplate.queryForMap("select * from code_rule_brief");
        return stringObjectMap;
    }
    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;
    @Autowired
    private RedisTemplate<String, Integer> integerRedisTemplate;

    @GetMapping("/redis")
    public Object redis() {
        stringRedisTemplate.opsForValue().set("hello", "world");
        return stringRedisTemplate.opsForValue().get("hello");
    }

    @GetMapping("/redisOther")
    public Object redisOther() {
        integerRedisTemplate.opsForValue().set("redisOther", 1);
        return integerRedisTemplate.opsForValue().get("redisOther");
    }

    @Autowired
    private CodeRuleBriefMapper briefMapper;
    @GetMapping("/mapper")
    public Object mapper() {
        CodeRuleBrief codeRuleBrief = briefMapper.selectById(1L);
        return codeRuleBrief;
    }
}
