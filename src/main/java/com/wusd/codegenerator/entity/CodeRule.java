package com.wusd.codegenerator.entity;

import com.wusd.codegenerator.entity.dto.CodeRuleItem;
import lombok.Data;

import java.util.List;

/**
 * @author Wusd
 * @date 2025/6/24
 * @description
 */
@Data
public class CodeRule extends CodeRuleItem {
    private List<CodeRuleItem> itemList;
}
