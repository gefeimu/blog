package com.blog.service;

import com.blog.common.BizException;
import com.blog.dto.TagVO;
import com.blog.entity.Tag;
import com.blog.mapper.TagMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Tag create(Tag tag) {
        if (tag.getName() == null || tag.getName().isBlank()) {
            throw BizException.badRequest("标签名不能为空");
        }
        tagMapper.insert(tag);
        return tagMapper.selectById(tag.getId());
    }

    @Transactional
    public Tag update(Long id, Tag tag) {
        if (tagMapper.selectById(id) == null) {
            throw BizException.notFound("标签不存在");
        }
        if (tag.getName() == null || tag.getName().isBlank()) {
            throw BizException.badRequest("标签名不能为空");
        }
        tag.setId(id);
        tagMapper.update(tag);
        return tagMapper.selectById(id);
    }

    @Transactional
    public void delete(Long id) {
        if (tagMapper.selectById(id) == null) {
            throw BizException.notFound("标签不存在");
        }
        // 已发布文章仍引用该标签时禁止删除（草稿引用不阻塞，与列表 count 口径一致）
        if (tagMapper.countByTagId(id) > 0) {
            throw BizException.conflict("该标签下还有已发布文章，不能删除");
        }
        tagMapper.deleteById(id);
    }
}
