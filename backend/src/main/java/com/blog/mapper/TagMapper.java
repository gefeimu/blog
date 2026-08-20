package com.blog.mapper;

import com.blog.dto.TagVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {

    List<TagVO> selectAllWithCount();
}
