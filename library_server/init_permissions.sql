-- 图书管理系统 - 数据库初始化脚本
-- 执行方式：mysql -u root -p book_db < init_permissions.sql

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM t_user_role;
DELETE FROM t_role_permission;
DELETE FROM t_permission;
DELETE FROM t_role;
DELETE FROM t_borrow_record;
DELETE FROM t_notice;
DELETE FROM t_book;
DELETE FROM t_user;

INSERT INTO t_role (id, role_name, description, created_time, updated_time) VALUES
(1, 'ROLE_ADMIN', '系统管理员，拥有所有权限', NOW(), NOW()),
(2, 'ROLE_USER', '普通用户，可以借阅图书', NOW(), NOW());

INSERT INTO t_permission (id, permission_name, permission_code, description, created_time, updated_time) VALUES
(1, '图书管理', 'book:manage', '管理图书信息（增删改查）', NOW(), NOW()),
(2, '借阅管理', 'borrow:manage', '管理借阅记录', NOW(), NOW()),
(3, '用户管理', 'user:manage', '管理用户信息', NOW(), NOW()),
(4, '通知管理', 'notice:manage', '发布和管理通知', NOW(), NOW()),
(5, '系统设置', 'system:manage', '管理系统角色和权限', NOW(), NOW());

INSERT INTO t_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 2);

INSERT INTO t_user (id, username, password, email, phone, status, created_time, updated_time) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@test.com', '13800138000', 1, NOW(), NOW()),
(2, 'user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user1@test.com', '13800138001', 1, NOW(), NOW());

INSERT INTO t_user_role (user_id, role_id) VALUES (1, 1), (2, 2);

INSERT INTO t_book (id, title, author, isbn, publisher, publish_date, price, stock, category, description, location, created_time, updated_time) VALUES
(1, 'Java编程思想', 'Bruce Eckel', '978-7-111-21386-9', '机械工业出版社', '2007-06-01', 89.00, 5, '科技', 'Java经典入门教程，涵盖面向对象编程核心概念', 'A区1排1层', NOW(), NOW()),
(2, 'Spring Boot实战', 'Craig Walls', '978-7-115-42874-3', '人民邮电出版社', '2016-10-01', 59.00, 3, '科技', 'Spring Boot官方推荐实战指南', 'A区1排2层', NOW(), NOW()),
(3, 'Vue.js设计与实现', '霍春阳', '978-7-115-56363-9', '人民邮电出版社', '2021-06-01', 89.00, 4, '科技', '深入解析Vue.js核心原理与实现机制', 'A区2排1层', NOW(), NOW()),
(4, '三体', '刘慈欣', '978-7-229-14235-0', '重庆出版社', '2008-01-01', 23.00, 8, '文学', '中国科幻文学里程碑之作', 'B区1排1层', NOW(), NOW()),
(5, '百年孤独', '加西亚·马尔克斯', '978-7-544-30225-3', '南海出版公司', '2011-06-01', 39.50, 6, '文学', '魔幻现实主义文学代表作', 'B区1排2层', NOW(), NOW()),
(6, '活着', '余华', '978-7-506-35403-7', '作家出版社', '2012-09-01', 20.00, 7, '文学', '讲述一个人一生的苦难与坚韧', 'B区2排1层', NOW(), NOW()),
(7, '红楼梦', '曹雪芹', '978-7-02-000220-7', '人民文学出版社', '1996-11-01', 68.00, 10, '文学', '中国古典小说四大名著之首', 'B区3排1层', NOW(), NOW()),
(8, '人类简史', '尤瓦尔·赫拉利', '978-7-508-64157-9', '中信出版社', '2014-11-01', 68.00, 5, '历史', '从十万年前到21世纪的人类历史', 'C区1排1层', NOW(), NOW()),
(9, '活着本来单纯', '丰子恺', '978-7-532-16822-5', '江苏文艺出版社', '2015-05-01', 25.00, 4, '哲学', '丰子恺先生散文随笔精选', 'C区1排2层', NOW(), NOW()),
(10, '小王子', '安托万·德·圣-埃克苏佩里', '978-7-02-004242-5', '人民文学出版社', '2003-08-01', 22.00, 12, '文学', '写给大人的童话经典', 'B区2排2层', NOW(), NOW());

INSERT INTO t_notice (id, title, content, status, author, created_time, updated_time, publish_time) VALUES
(1, '系统上线通知', '图书管理系统已正式上线，欢迎各位老师和同学使用。如有疑问请联系管理员。', 1, 'admin', NOW(), NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 初始化完成！
-- 管理员账号：admin / 123456
-- 普通用户账号：user1 / 123456
-- ============================================
