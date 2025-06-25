package com.wusd.codegenerator.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author Wusd
 * @date 2025/6/24
 * @description
 */
@Data
@TableName("code_rule_brief")
public class CodeRuleBrief {
    @TableId
    private Long id;
    private String name;
    private Integer currentValue;
    private Timestamp updateTime;
}
