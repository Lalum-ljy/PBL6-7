-- 创建活动表
CREATE TABLE IF NOT EXISTS sys_activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_name VARCHAR(255) NOT NULL,
    activity_desc TEXT,
    cover_url VARCHAR(500),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status INT DEFAULT 0,
    hot_status INT DEFAULT 0,
    creator VARCHAR(50) NOT NULL,
    create_time DATETIME,
    update_time DATETIME
);

-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(500),
    create_time DATETIME
);

-- 创建通知表
CREATE TABLE IF NOT EXISTS sys_notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    create_time DATETIME
);