package com.blog.controller;

import com.blog.entity.Tag;
import com.blog.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/api/tags")
    public List<Tag> list() {
        return tagService.list();
    }
}
