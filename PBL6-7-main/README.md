# 这是第五次提交 4.1
## 项目前端请可访问https://github.com/Lalum-ljy/PBL6-vue （有更新：第三次提交 4.1）
### 关于本次提交
- 新增图片上传接口
  - 现在新增活动会调用POST/api/activity和POST/api/updown/img两个接口实现创建活动时的图片上传
  - 图片上传路径是src/mian/resources/static/img
  - (这个功能竟然写了一天才完全写好)
-**新增mcp板块（重大更新）**
 - 引入mcp大模型功能实现了指令化了增加活动和查询活动操作
 - 正常对话和具体操作时两个业务逻辑板块（ModelEngineService和SmartAssistantService）
 - 两个业务逻辑使用同一个接口POST/api/chat
 - 实现逻辑是先利用大模型的注意力机制将口语化对话转译为固定指令，在分析指令并操作



# 这是第四次提交 3.28
## 项目前端请可访问https://github.com/Lalum-ljy/PBL6-vue （有更新：第二次提交 3.38）
### 关于本次提交
- 新增sys_notice表
  - 该表的5个接口
   - 增
   - 删（按id）
   - 改
   - 查（按id）
   - 获取所有公告
- 该表表结构
```sql
CREATE TABLE `sys_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '通知标题',
  `readerstatus` tinyint NOT NULL DEFAULT 0 COMMENT '通知接收范围：0-所有人 1-普通用户 2-医生',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '通知状态：0-未读 1-已读 2-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '通知创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_notice_readerstatus`(`readerstatus` ASC) USING BTREE,
  INDEX `idx_sys_notice_reader_status`(`readerstatus` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统通知表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
 ````





# 这是第三次提交 3.26
## 项目选题是校园活动发布平台
### 关于本次提交
- **增加了前端部分（vue）**
  - 考虑到项目解耦性，前端仓库与本仓库分开，前端具体可访问https://github.com/Lalum-ljy/PBL6-vue
- 后端更新了2个接口
  - 根据标题和内容搜索（替换了原本的根据内容搜索）
  - 根据是否为热门活动筛选
- 修改了sys_activity表的结构
  - 增加了hot_status字段
  - 让该表的主键（id）可以复用
  - 修改后的数据库查询语句
```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_activity
-- ----------------------------
DROP TABLE IF EXISTS `sys_activity`;
CREATE TABLE `sys_activity`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '活动ID（主键）',
  `activity_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动名称',
  `activity_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '活动描述',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动封面图片URL地址',
  `start_time` datetime NOT NULL COMMENT '活动开始时间',
  `end_time` datetime NOT NULL COMMENT '活动结束时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '活动状态：0-未开始 1-进行中 2-已结束 3-已取消',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `hot_status` tinyint NOT NULL DEFAULT 0 COMMENT '活动热门状态：0-非热门 1-热门',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_activity_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
 ````
 - 彻底跑通了消息队列，现在消息队列经过测试，完全可用


# 这是第二次提交 3.24
## 项目选题是校园活动发布平台
### 关于本次提交
**本次提交包括：**
- 增加了3个接口
  - 根据标题搜索
  - 根据内容搜索
  - 根据发起人搜索（两表联合）
  - 以上接口均支持模糊搜索，均通过本地测试

# **校园活动发布平台测试报告（第一次提交 3.23）**
### 一、测试概述
本次测试针对校园活动发布平台第一次提交的内容展开，主要验证后端基础架构、依赖集成、数据库设计及核心接口的可用性，为后续开发和优化提供依据。

### 二、测试做了什么
- 1. 后端架构与依赖验证
  - 检查项目后端基本架构的完整性，确认架构可正常启动。
  - 验证核心依赖（MyBatis、Redis、RabbitMQ、Swagger）的引入情况，确认依赖配置无明显冲突。
- 2. 数据库表设计验证
  - 检查 sys_user（用户表）和 sys_activity（活动表）的表结构，确认字段定义、索引设置、注释说明与设计文档一致。
  - 验证表的字符集（utf8mb4）、引擎（InnoDB）等配置符合要求。
- 3. 核心接口功能测试
  - 针对 8 个接口进行基础功能验证：
  - 用户模块：
    - 登录接口：验证账号密码校验逻辑的基础可用性。
    - 注册接口：确认哈希加密工具类的集成，验证密码加密存储的基础流程。
  - 活动模块：
    - 验证活动的增删改查（CRUD）四个接口的基础功能。
    - 测试按时间筛选、按状态（0 - 未开始、1 - 进行中、2 - 已结束、3 - 已取消）筛选接口的基础逻辑。
- 4. Swagger 文档尝试访问
    - 尝试访问 http://localhost:8080/swagger-ui/index.html 查看接口 API 文档，但遇到访问问题（详见 “遇到的困难” 部分）。
### 三、遇到了哪些困难
- 1. Redis 功能未正式调用
  - README 中提到 “Redis 当前未正式调用，本应存入的 token 暂时使用 try catch 包裹”，导致无法测试 Redis 缓存 token 的实际效果，如 token 的存储、读取、过期等逻辑。
- 2. RabbitMQ 功能未经测试
  - RabbitMQ 用于活动表的状态切换（自动开始 / 结束任务），但 README 明确说明该功能 “未经测试”，无法验证消息队列是否能正常触发活动状态的自动更新。
- 3. Swagger 文档访问受限
  - 尝试访问 Swagger 地址时，系统提示 “URL 拼写可能存在错误，请检查”，且该地址为本地地址（localhost），无法在非本机环境下查看接口文档，影响接口测试的效率和便利性。
- 4. 接口测试覆盖不足
  - 仅验证了接口的基础功能，未覆盖异常场景（如参数错误、数据重复、并发请求等），无法全面评估接口的稳定性和容错性。
### 四、下一步怎么办
- 1. 完善 Redis 功能并测试
  - 移除 token 存储逻辑中的 try catch 包裹，正式集成 Redis。
  - 测试 Redis 的 token 存储、读取、过期删除等功能，确保缓存机制正常工作。
- 2. 测试 RabbitMQ 活动状态切换
  - 搭建 RabbitMQ 测试环境，配置消息队列和定时任务。
  - 验证活动状态是否能根据时间自动切换（如未开始→进行中→已结束），确保功能符合预期。
- 3. 解决 Swagger 文档访问问题
  - 检查 Swagger 配置，修正 URL 拼写或访问路径问题。
  - 若需在非本机环境测试，可配置 Swagger 的外部访问地址，或提供本地测试的详细指南。
- 4. 补充接口测试覆盖
  - 设计异常场景测试用例（如参数为空、数据格式错误、重复提交等），验证接口的容错性。
  - 进行并发请求测试，评估接口在高并发下的性能和稳定性。
- 5. 推进前后端联调准备
  - 根据 README 建议，前端使用 axios 统一管理接口，下一步可准备接口文档（修正 Swagger 后），为前后端联调做准备。


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
 - 接口使用swagger统一管理，访问http://localhost:8080/swagger-ui/index.html （本机）查看接口api
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
