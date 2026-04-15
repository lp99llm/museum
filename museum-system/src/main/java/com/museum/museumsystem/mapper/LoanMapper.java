package com.museum.museumsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.museum.museumsystem.entity.Loan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanMapper extends BaseMapper<Loan> {
}