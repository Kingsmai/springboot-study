package com.example.springbootstudybackend.mapper;

import com.example.springbootstudybackend.entity.UserAccount;
import com.example.springbootstudybackend.entity.auth.Account;
import org.apache.ibatis.annotations.Insert;
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

    @Select("SELECT * FROM account WHERE username = #{text} OR email = #{text}")
    UserAccount findUserAccountByUsernameOrEmail(String text);

    @Select("SELECT * FROM account WHERE email = #{email}")
    Account findAccountByEmail(String email);

    @Insert("INSERT INTO account (username, password, email) VALUES (#{username}, #{password}, #{email})")
    int createAccount(String username, String password, String email);

    @Update("UPDATE account SET password = #{password} WHERE email = #{email}")
    int resetPasswordByEmail(String password, String email);
}
