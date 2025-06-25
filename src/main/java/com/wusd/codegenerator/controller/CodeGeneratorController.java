package com.wusd.codegenerator.controller;

import com.wusd.codegenerator.service.CodeGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Wusd
 * @date 2025/6/24
 * @description
 */
@RequestMapping("/codeGenerator")
@RestController
public class CodeGeneratorController {
    @Autowired
    private CodeGeneratorService service;

    @GetMapping("/generateNextAndGetCode")
    public ResponseEntity generateNextAndGetCode(String name) {
        return ResponseEntity.ok().body(service.generateNextAndGetCode(name));
    }
}
