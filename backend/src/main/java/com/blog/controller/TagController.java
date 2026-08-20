package com.blog.controller;

import com.blog.dto.TagVO;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/api/tags")
    public List<TagVO> list() {
        return tagService.list();
    }

    @PostMapping("/api/tags")
    public Tag create(@RequestBody Tag tag) {
        return tagService.create(tag);
    }

    @PutMapping("/api/tags/{id}")
    public Tag update(@PathVariable Long id, @RequestBody Tag tag) {
        return tagService.update(id, tag);
    }

    @DeleteMapping("/api/tags/{id}")
    public void delete(@PathVariable Long id) {
        tagService.delete(id);
    }
}
