-- =============================================
-- Docker 入口初始化脚本（MySQL 容器启动时自动执行）
-- 合并 schema.sql + data.sql
-- =============================================

SOURCE /docker-entrypoint-initdb.d/schema.sql;
SOURCE /docker-entrypoint-initdb.d/data.sql;
