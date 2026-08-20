package com.blog.mapper;

import com.blog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {

    List<Tag> selectAll();
}
