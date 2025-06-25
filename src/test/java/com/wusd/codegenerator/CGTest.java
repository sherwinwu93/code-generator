package com.wusd.codegenerator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CGApplication.class)
public class CGTest {

    @Test
    public void test() {
        System.out.println("-----------test");
    }

    @Test
    public void testMyInsertAll() {
    }
}
