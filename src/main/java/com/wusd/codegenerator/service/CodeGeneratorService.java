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
import org.springframework.stereotype.Service;

import java.util.List;
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
//    @Autowired
//    RedisTemplate<String, CodeRule> codeRuleRedisTemplate;
//    @Autowired
//    RedisTemplate<String, Integer> integerRedis;
    private String redisCodeRuleKeyPrefix = "redisCodeRuleKey:";
    private String redisCodeRuleItemKeyPrefix = "redisCodeRuleItemKey:";
    public String generateNextAndGetCode(String name) {
        CodeRule codeRule = cache$getCodeRule(name);
        return this.generateNextAndGetCode(codeRule);
    }

    private CodeRule cache$getCodeRule(String name) {
        String redisCodeRuleKey = redisCodeRuleKeyPrefix + name;
//        if (!codeRuleRedisTemplate.hasKey(redisCodeRuleKey)) {
//            codeRuleRedisTemplate.opsForValue().set(redisCodeRuleKey, getCodeRuleByName(name));
//        }
//        CodeRule codeRule = codeRuleRedisTemplate.opsForValue().get(redisCodeRuleKey);
//        return codeRule;
        return null;
    }

    private CodeRule getCodeRuleByName(String name) {
        QueryWrapper<CodeRuleBrief> briefQueryWrapper = new QueryWrapper<CodeRuleBrief>()
                .eq("name", name);
        CodeRuleBrief codeRuleBrief = briefMapper.selectOne(briefQueryWrapper);
        QueryWrapper<CodeRuleItem> itemQueryWrapper = new QueryWrapper<CodeRuleItem>()
                .eq("codeRuleBriefId", codeRuleBrief.getId());
        List<CodeRuleItem> codeRuleItems = itemMapper.selectList(itemQueryWrapper);

        CodeRule codeRule = MyBeanUtils.convert(codeRuleBrief, CodeRule.class);
        codeRule.setItemList(codeRuleItems);
        return codeRule;
    }

    private String generateNextAndGetCode(CodeRule codeRule) {
        StringBuilder code = new StringBuilder();
        String today = DateTime.now().toString("yyyyMMdd");
        for (CodeRuleItem codeRuleItem : codeRule.getItemList()) {
            if (codeRuleItem.getRuleType().equals(1)) {// 固定字符
                code.append(codeRuleItem.getFixedChars());
            } else if (codeRuleItem.getRuleType().equals(2)) {// 日期
                code.append(today);
            } else if (codeRuleItem.getRuleType().equals(3)) {// 自增数字
                code.append(generateNumAndGet(codeRuleItem, today));
            }
        }
        return code.toString();
    }

    private String generateNumAndGet(CodeRuleItem codeRuleItem, String today) {
//        String redisCodeRuleItemKey = redisCodeRuleItemKeyPrefix + today + ":" + codeRuleItem.getId();
//        if (!integerRedis.hasKey(redisCodeRuleItemKey)) {
//            integerRedis.opsForValue().set(redisCodeRuleItemKey, codeRuleItem.getNumStart(), 1, TimeUnit.DAYS);
//        }
//        Long increment = integerRedis.opsForValue().increment(redisCodeRuleItemKey);
//        return String.format("%0" + codeRuleItem.getNumLength() + "d", increment);
        return null;
    }

    public static void main(String[] args) {
        String xx = String.format("%04d", 1L);
        System.out.println(xx);
    }

}
