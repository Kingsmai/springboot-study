# 前后端分离项目模板

包含基本的登录、注册、密码重置等等功能，可以二次开发编写具体场景下的应用程序。

## 功能介绍

- [x] 登录功能（支持用户名，邮箱登录） 
- [ ] 七天免密登录
  - [x] 后端 cookie 保存逻辑
  - [ ] 前端效果
- [ ] 注册用户（通过邮箱注册）
- [ ] 重置密码（通过邮箱重置密码）

## 项目设立

在 application.properties 中修改，springboot_study 为你的数据库名称。username 和 password 根据自己的情况设置。

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_study?useUnicode=true&characterEncoding=utf-8
spring.datasource.username=root
spring.datasource.password=123456
```

在 MySQL 数据库中创建一个数据库，数据库名和 application.properties 里的数据库一致

```mysql
DROP TABLE IF EXISTS account;

CREATE TABLE IF NOT EXISTS account
(
    id       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(64)  NOT NULL,
    username VARCHAR(64)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    UNIQUE KEY unique_email (email),
    UNIQUE KEY unique_username (username)
);

INSERT INTO account
VALUES (null, 'xsbugh@gmail.com', 'admin', '$2a$10$fq.J23Lcr5dyV4yFO3fqKuZRAi4wfBKa9M9cYT/1dslb9qsasb2RO');
```

打开 Security Configuration，在 `tokenRepository` 函数中将 `jdbcTokenRepository.setCreateTableOnStartup(true)` 设为
true。在项目运行一次之后设置为 false

## 线上运行须知

**重要！**上线前，需要修改 Security Configuration 的 cors 设置。