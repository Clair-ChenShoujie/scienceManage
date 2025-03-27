package com.example.mapper;

import com.example.entity.Log;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LogMapper {

    int insert(Log log);

    void deleteById(Integer id);

    List<Log> selectAll(Log log);
}
