package com.blog.service;

import com.blog.dto.TagVO;
import com.blog.mapper.TagMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagMapper tagMapper;

    public TagService(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    public List<TagVO> list() {
        return tagMapper.selectAllWithCount();
    }
}
