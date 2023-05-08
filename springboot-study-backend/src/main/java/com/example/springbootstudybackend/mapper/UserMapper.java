package com.example.springbootstudybackend.mapper;

import com.example.springbootstudybackend.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Mapper 接口定义了数据库操作方法
 */
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM account WHERE username = #{text} OR email = #{text}")
    Account findAccountByUsernameOrEmail(String text);

    @Update("INSERT INTO account (username, password, email) VALUES (#{username}, #{password}, #{email})")
    int createAccount(String username, String password, String email);
}
