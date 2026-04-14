-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS PBL6 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE PBL6;

-- 创建活动表
CREATE TABLE IF NOT EXISTS sys_activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_name VARCHAR(255) NOT NULL,
    activity_desc TEXT,
    cover_url VARCHAR(255),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status INT DEFAULT 0,
    hot_status INT DEFAULT 0,
    creator VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建通知表
CREATE TABLE IF NOT EXISTS sys_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    readerstatus INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_type INT DEFAULT 0,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    gender INT DEFAULT 0,
    phone VARCHAR(20),
    email VARCHAR(100),
    school_id BIGINT,
    college VARCHAR(100),
    major VARCHAR(100),
    grade VARCHAR(20),
    class_name VARCHAR(50),
    avatar VARCHAR(255),
    status INT DEFAULT 1,
    is_deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_time DATETIME,
    last_login_ip VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入示例数据到活动表
INSERT INTO sys_activity (activity_name, activity_desc, cover_url, start_time, end_time, status, hot_status, creator) VALUES
('春季运动会', '学校春季运动会，包含跑步、跳远、跳高、投掷等项目', 'https://example.com/sport.jpg', '2026-04-10 09:00:00', '2026-04-12 17:00:00', 1, 1, 'admin'),
('校园科技节', '展示学生科技创新成果，包含机器人、编程、3D打印等项目', 'https://example.com/tech.jpg', '2026-04-15 10:00:00', '2026-04-18 16:00:00', 1, 0, 'admin'),
('文艺汇演', '学生文艺表演，包含歌唱、舞蹈、戏剧等节目', 'https://example.com/art.jpg', '2026-04-20 19:00:00', '2026-04-20 21:30:00', 0, 0, 'admin');

-- 插入示例数据到通知表
INSERT INTO sys_notice (title, content, readerstatus, status) VALUES
('系统更新通知', '系统将于2026年4月5日凌晨进行维护更新，届时系统将暂时无法访问，请提前做好准备。', 0, 1),
('活动报名提醒', '春季运动会报名将于2026年4月1日截止，请有意参加的同学尽快报名。', 0, 1),
('安全提示', '近期校园内出现多起物品丢失事件，请同学们注意保管好个人财物。', 0, 1);

-- 插入示例数据到用户表
INSERT INTO sys_user (user_type, username, password, real_name, gender, phone, email, school_id, college, major, grade, class_name, avatar, status, is_deleted) VALUES
(0, 'admin', '123456', '管理员', 1, '13800138000', 'admin@example.com', 1, '信息学院', '计算机科学与技术', '2023级', '计科1班', 'https://example.com/avatar1.jpg', 1, 0),
(1, 'student1', '123456', '张三', 1, '13900139001', 'student1@example.com', 1, '信息学院', '计算机科学与技术', '2023级', '计科1班', 'https://example.com/avatar2.jpg', 1, 0),
(1, 'student2', '123456', '李四', 0, '13900139002', 'student2@example.com', 1, '信息学院', '软件工程', '2023级', '软工2班', 'https://example.com/avatar3.jpg', 1, 0);
