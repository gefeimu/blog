package com.blog.controller;

import com.blog.entity.Tag;
import com.blog.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TagController {

    @Autowired
    private TagMapper tagMapper;

    @GetMapping("/api/tags")
    public List<Tag> list() {
        return tagMapper.selectAll();
    }
}
