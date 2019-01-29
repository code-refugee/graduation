package com.yuhangTao.mapper;


import com.yuhangTao.pojo.SearchRecords;
import com.yuhangTao.utils.MyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
    List<String> queryHot();
}