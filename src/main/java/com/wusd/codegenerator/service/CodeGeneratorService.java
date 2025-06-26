package com.wusd.codegenerator.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wusd.codegenerator.entity.CodeRule;
import com.wusd.codegenerator.entity.dto.CodeRuleBrief;
import com.wusd.codegenerator.entity.dto.CodeRuleItem;
import com.wusd.codegenerator.mapper.CodeRuleBriefMapper;
import com.wusd.codegenerator.mapper.CodeRuleItemMapper;
import com.wusd.codegenerator.utils.MyBeanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Wusd
 * @date 2025/6/24
 * @description
 */
@Service
public class CodeGeneratorService {
    @Autowired
    CodeRuleBriefMapper briefMapper;
    @Autowired
    CodeRuleItemMapper itemMapper;
    @Autowired
    RedisTemplate<String, CodeRule> codeRuleRedisTemplate;
    @Autowired
    RedisTemplate<String, Integer> integerRedis;
    @Autowired
    RedisTemplate<String, String> lockRedis;
    private String redisCodeRuleKeyPrefix = "redisCodeRuleKey:";
    private String redisCodeRuleItemKeyPrefix = "redisCodeRuleItemKey:";
    private String redisLockPrefix = "redisLock:";

    public String generateNextAndGetCode(String name) {
        CodeRule codeRule = cache$getCodeRule(name);
        return this.generateNextAndGetCode(codeRule);
    }

    private CodeRule cache$getCodeRule(String name) {
        String redisCodeRuleKey = redisCodeRuleKeyPrefix + name;
        String clientId = UUID.randomUUID().toString();
        String redisLockKey = redisLockPrefix + redisCodeRuleKey;

        while (true) {
            try {
                Boolean locked = lockRedis.opsForValue().setIfAbsent(redisLockKey, clientId);
                if (!locked) {
                    TimeUnit.MILLISECONDS.sleep(50);
                    continue;
                }
                if (!codeRuleRedisTemplate.hasKey(redisCodeRuleKey)) {
                    codeRuleRedisTemplate.opsForValue().set(redisCodeRuleKey, getCodeRuleByName(name));
                }
                break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (clientId.equals(lockRedis.opsForValue().get(redisLockKey))) {
                    lockRedis.delete(redisLockKey);
                }
                // 这个具备原子性
//                String deleteLockScript = "if redis.call('get', KEYS[1]) == ARGV[1] " +
//                        "then redis.call('del', KEYS[1]) " +
//                        "else return 0 end";
//                lockRedis.execute(new DefaultRedisScript<>(deleteLockScript), Arrays.asList(redisLockKey), clientId);
            }
        }

        CodeRule codeRule = codeRuleRedisTemplate.opsForValue().get(redisCodeRuleKey);
        return codeRule;
    }

    private CodeRule getCodeRuleByName(String name) {
        QueryWrapper<CodeRuleBrief> briefQueryWrapper = new QueryWrapper<CodeRuleBrief>()
                .eq("name", name);
        CodeRuleBrief codeRuleBrief = briefMapper.selectOne(briefQueryWrapper);
        QueryWrapper<CodeRuleItem> itemQueryWrapper = new QueryWrapper<CodeRuleItem>()
                .eq("code_rule_brief_id", codeRuleBrief.getId())
                .orderBy(true, true, "position");
        List<CodeRuleItem> codeRuleItems = itemMapper.selectList(itemQueryWrapper);

        CodeRule codeRule = MyBeanUtils.convert(codeRuleBrief, CodeRule.class);
        codeRule.setItemList(codeRuleItems);
        return codeRule;
    }

    private String generateNextAndGetCode(CodeRule codeRule) {
        StringBuilder code = new StringBuilder();
        String today = DateTime.now().toString("yyyyMMdd");
        boolean containsDate = codeRule.getItemList().stream().filter(o -> o.getRuleType().equals(2)).findAny().isPresent();
        for (CodeRuleItem codeRuleItem : codeRule.getItemList()) {
            if (codeRuleItem.getRuleType().equals(1)) {// 固定字符
                code.append(codeRuleItem.getFixedChars());
            } else if (codeRuleItem.getRuleType().equals(2)) {// 日期
                code.append(today);
            } else if (codeRuleItem.getRuleType().equals(3)) {// 自增数字
                code.append(generateNumAndGet(codeRuleItem, today, containsDate));
            }
        }
        return code.toString();
    }

    private String generateNumAndGet(CodeRuleItem codeRuleItem, String today, boolean containsDate) {
        if (!containsDate) today = "";
        String redisCodeRuleItemKey = redisCodeRuleItemKeyPrefix + today + ":" + codeRuleItem.getId();
        String lockRedisKey = redisLockPrefix + redisCodeRuleItemKey;
        String clientId = UUID.randomUUID().toString();
        Integer numCurrent;

        while (true) {
            try {
                Boolean locked = lockRedis.opsForValue().setIfAbsent(lockRedisKey, clientId);
                if (!locked) {
                    TimeUnit.MILLISECONDS.sleep(100);
                    continue;
                }
                if (!integerRedis.hasKey(redisCodeRuleItemKey)) {
                    if (containsDate) {
                        integerRedis.opsForValue().set(redisCodeRuleItemKey, codeRuleItem.getNumStart(), 1, TimeUnit.DAYS);
                    } else {
                        integerRedis.opsForValue().set(redisCodeRuleItemKey, codeRuleItem.getNumStart());
                    }
                }
                numCurrent = integerRedis.opsForValue().get(redisCodeRuleItemKey);
                integerRedis.opsForValue().increment(redisCodeRuleItemKey);
                break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (clientId.equals(lockRedis.opsForValue().get(lockRedisKey))) {
                    lockRedis.delete(lockRedisKey);
                }
                // 这个具备原子性
//                String deleteLockScript = "if redis.call('get', KEYS[1]) == ARGV[1] " +
//                        "then redis.call('del', KEYS[1]) " +
//                        "else return 0 end";
//                lockRedis.execute(new DefaultRedisScript<>(deleteLockScript), Arrays.asList(lockRedisKey), clientId);
            }
        }
        return String.format("%0" + codeRuleItem.getNumLength() + "d", numCurrent);
    }

    public static void main(String[] args) {
        String xx = String.format("%04d", 1L);
        System.out.println(xx);
    }

}
