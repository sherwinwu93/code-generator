package com.wusd.codegenerator.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Wusd
 * @date 2025/6/24
 * @description
 */
@TableName("code_rule_item")
@Data
public class CodeRuleItem {
    @TableId
    private Long id;
    private Long codeRuleBriefId;
    private Integer ruleType;
    private String fixedChars;
    private Integer numLength;
    private Integer numStart;
    private Integer position;
}