package com.blog.mapper;

import com.blog.dto.TagVO;
import com.blog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {

    List<TagVO> selectAllWithCount();

    Tag selectById(Long id);

    int insert(Tag tag);

    int update(Tag tag);

    int deleteById(Long id);

    /** 该标签下已发布的文章数（删除校验用） */
    long countByTagId(Long tagId);
}
