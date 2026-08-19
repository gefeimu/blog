package com.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CategoryController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/categories")
    public List<Map<String, Object>> list() {
        return jdbcTemplate.queryForList("SELECT id, name, sort FROM category ORDER BY sort");
    }
}
