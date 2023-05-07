package com.example.springbootstudybackend.entity;

import lombok.Data;

/**
 * 实体需要与数据库的数据字段及其数据类型相对应
 */
@Data
public class Account {
    int id;
    String email;
    String username;
    String password;
}
