# 这是第一次提交 3.23
## 项目选题是校园活动发布平台
### 关于本次提交
**本次提交包括：**
- 完成了项目后端基本架构
- 完成项目后端基本依赖引入
  - mybatis
  - Redis
  - rabbitMQ
  - swagger
- SQL中两表
  - sys_user
  - sys_activity 
- 8个接口
  - sys_user
    - 登录接口
    - 注册接口（设计哈希加密工具类）
  - sys_activity
     - CRUD四个
     - 按时间筛选
     - 按状态（0：未开始 1：进行中 2：已结束 3：已取消）筛选

**须知**
 - 接口使用swagger统一管理，访问http://localhost:8080/swagger-ui/index.html 查看接口api
 - Redis当前未正式调用，本应存入的token暂时使用try catch包裹
 - rabbitMQ用在活动表的状态切换，自动开始/结束任务，**未经测试**
 - **前端建议使用axios统一管理接口**

**表设计**
- 用户表
```sql
CREATE TABLE `sys_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户唯一主键ID',
  `user_type` TINYINT NOT NULL COMMENT '用户类型：1-学生 2-教师 3-管理员 4-社团负责人',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号（学号/工号/自定义）',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储，如BCrypt哈希）',
  `real_name` VARCHAR(20) NOT NULL COMMENT '真实姓名',
  `gender` TINYINT NULL DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
  `phone` VARCHAR(11) NULL COMMENT '手机号（用于验证码登录/通知）',
  `email` VARCHAR(50) NULL COMMENT '邮箱（用于找回密码/通知）',
  `school_id` BIGINT UNSIGNED NULL COMMENT '所属学校ID（关联学校表）',
  `college` VARCHAR(50) NULL COMMENT '所属学院（如：计算机学院）',
  `major` VARCHAR(50) NULL COMMENT '所属专业（如：软件工程）',
  `grade` VARCHAR(10) NULL COMMENT '年级（如：2024级）',
  `class_name` VARCHAR(20) NULL COMMENT '班级（如：软工2401）',
  `avatar` VARCHAR(255) NULL COMMENT '头像URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：0-禁用 1-正常 2-未激活',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` DATETIME NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) NULL COMMENT '最后登录IP',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`), -- 登录账号唯一
  KEY `idx_user_type` (`user_type`), -- 按用户类型检索
  KEY `idx_school_id` (`school_id`) -- 按学校检索
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园活动平台用户表';
 ````
- **活动表**
```sql
CREATE TABLE IF NOT EXISTS activity (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '活动ID（主键）',
    activity_name VARCHAR(100) NOT NULL COMMENT '活动名称',
    activity_desc TEXT COMMENT '活动描述',
    start_time DATETIME NOT NULL COMMENT '活动开始时间',
    end_time DATETIME NOT NULL COMMENT '活动结束时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '活动状态：0-未开始 1-进行中 2-已结束 3-已取消',
    creator VARCHAR(50) NOT NULL COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    -- 索引优化：加快时间/状态相关查询
    INDEX idx_activity_time (start_time, end_time),
    INDEX idx_activity_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';
 ````
