package com.example.springbootstudybackend.mapper;

import com.example.springbootstudybackend.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM account WHERE username = #{text} OR email = #{text}")
    Account findAccountByUsernameOrEmail(String text);
}
