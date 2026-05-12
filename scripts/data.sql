-- =============================================
-- 方舟 Code Plan 中转站系统 - 初始数据脚本
-- 版本: v1.1
-- 日期: 2026-05-11
-- 作者: WangMiao
-- =============================================

USE ark_proxy;

-- =============================================
-- 初始数据
-- =============================================

-- 插入管理员用户（密码：admin123，bcrypt hash）
INSERT INTO users (user_id, username, email, password_hash, department_id, role, status, created_at, updated_at)
VALUES
    ('user_admin', 'admin', 'encrypted:admin@example.com', '$2a$12$LJ3m4ys3H2dF5x8K9n0PqeR4vW7t2y5X8z1B4e7G0J3M6P9S2V5Y', NULL, 'admin', 'active', NOW(), NOW());

-- 插入测试用户（密码：user123）
INSERT INTO users (user_id, username, email, password_hash, department_id, role, status, created_at, updated_at)
VALUES
    ('user_001', 'zhangsan', 'encrypted:zhangsan@example.com', '$2a$12$LJ3m4ys3H2dF5x8K9n0PqeR4vW7t2y5X8z1B4e7G0J3M6P9S2V5Y', 'dept_001', 'user', 'active', NOW(), NOW()),
    ('user_002', 'lisi', 'encrypted:lisi@example.com', '$2a$12$LJ3m4ys3H2dF5x8K9n0PqeR4vW7t2y5X8z1B4e7G0J3M6P9S2V5Y', 'dept_001', 'user', 'active', NOW(), NOW());

-- 插入用户配额（月度配额）
INSERT INTO user_quotas (quota_id, user_id, quota_type, total_tokens, used_tokens, total_requests, used_requests, total_cost, used_cost, reset_at, created_at, updated_at)
VALUES
    ('quota_admin', 'user_admin', 'monthly', 10000000, 0, 100000, 0, 10000.00, 0.00, DATE_ADD(CURDATE(), INTERVAL 1 MONTH), NOW(), NOW()),
    ('quota_user001', 'user_001', 'monthly', 1000000, 0, 10000, 0, 1000.00, 0.00, DATE_ADD(CURDATE(), INTERVAL 1 MONTH), NOW(), NOW()),
    ('quota_user002', 'user_002', 'monthly', 1000000, 0, 10000, 0, 1000.00, 0.00, DATE_ADD(CURDATE(), INTERVAL 1 MONTH), NOW(), NOW());

-- 插入厂商配置
INSERT INTO provider_config (provider_id, provider_name, api_base_url, auth_type, auth_config, protocol_type, priority, enabled, created_at, updated_at)
VALUES
    ('anthropic', 'Anthropic (Claude)', 'https://api.anthropic.com/v1', 'bearer_token', '{"header": "x-api-key", "prefix": ""}', 'anthropic_messages', 1, TRUE, NOW(), NOW()),
    ('openai', 'OpenAI (GPT)', 'https://api.openai.com/v1', 'bearer_token', '{"header": "Authorization", "prefix": "Bearer "}', 'openai_chat', 2, TRUE, NOW(), NOW()),
    ('deepseek', 'DeepSeek', 'https://api.deepseek.com/v1', 'bearer_token', '{"header": "Authorization", "prefix": "Bearer "}', 'openai_chat', 3, TRUE, NOW(), NOW());

-- 插入模型映射（Anthropic）
INSERT INTO model_mapping (mapping_id, provider_id, standard_model_name, vendor_model_name, context_window, max_output_tokens, input_price_per_1k, output_price_per_1k, enabled, created_at, updated_at)
VALUES
    ('map_anthropic_opus', 'anthropic', 'claude-3-opus', 'claude-3-opus-20240229', 200000, 4096, 0.015000, 0.075000, TRUE, NOW(), NOW()),
    ('map_anthropic_sonnet', 'anthropic', 'claude-3-sonnet', 'claude-3-sonnet-20240229', 200000, 4096, 0.003000, 0.015000, TRUE, NOW(), NOW()),
    ('map_anthropic_haiku', 'anthropic', 'claude-3-haiku', 'claude-3-haiku-20240307', 200000, 4096, 0.000250, 0.001250, TRUE, NOW(), NOW());

-- 插入模型映射（OpenAI）
INSERT INTO model_mapping (mapping_id, provider_id, standard_model_name, vendor_model_name, context_window, max_output_tokens, input_price_per_1k, output_price_per_1k, enabled, created_at, updated_at)
VALUES
    ('map_openai_gpt4o', 'openai', 'gpt-4o', 'gpt-4o-2024-05-13', 128000, 16384, 0.005000, 0.015000, TRUE, NOW(), NOW()),
    ('map_openai_gpt4_turbo', 'openai', 'gpt-4-turbo', 'gpt-4-turbo-2024-04-09', 128000, 4096, 0.010000, 0.030000, TRUE, NOW(), NOW()),
    ('map_openai_gpt35_turbo', 'openai', 'gpt-3.5-turbo', 'gpt-3.5-turbo-0125', 16385, 4096, 0.000500, 0.001500, TRUE, NOW(), NOW());

-- 插入模型映射（DeepSeek）
INSERT INTO model_mapping (mapping_id, provider_id, standard_model_name, vendor_model_name, context_window, max_output_tokens, input_price_per_1k, output_price_per_1k, enabled, created_at, updated_at)
VALUES
    ('map_deepseek_v2', 'deepseek', 'deepseek-v2', 'deepseek-chat', 64000, 4096, 0.000140, 0.000280, TRUE, NOW(), NOW()),
    ('map_deepseek_coder', 'deepseek', 'deepseek-coder', 'deepseek-coder', 16000, 4096, 0.000140, 0.000280, TRUE, NOW(), NOW());

-- 插入 API Key 池（示例，实际部署时替换为真实加密后的 API Key）
INSERT INTO api_keys (key_id, provider_id, api_key_encrypted, key_alias, rate_limit_rpm, rate_limit_tpm, used_rpm, used_tpm, status, last_used_at, created_at, updated_at)
VALUES
    ('key_anthropic_001', 'anthropic', 'enc:example_encrypted_key_001', 'Anthropic 主 Key', 1000, 100000, 0, 0, 'active', NULL, NOW(), NOW()),
    ('key_openai_001', 'openai', 'enc:example_encrypted_key_002', 'OpenAI 主 Key', 1000, 100000, 0, 0, 'active', NULL, NOW(), NOW()),
    ('key_deepseek_001', 'deepseek', 'enc:example_encrypted_key_003', 'DeepSeek 主 Key', 1000, 100000, 0, 0, 'active', NULL, NOW(), NOW());

-- 插入预警规则（全局默认规则）
INSERT INTO alert_rules (rule_id, rule_name, rule_type, target_type, target_id, reminder_threshold, warning_threshold, block_threshold, notification_channels, enabled, created_at, updated_at)
VALUES
    ('rule_global_quota', '全局配额预警', 'quota', 'user', NULL, 70.00, 90.00, 100.00, '["email", "wechat"]', TRUE, NOW(), NOW()),
    ('rule_global_cost', '全局费用预警', 'cost', 'user', NULL, 200.00, 500.00, NULL, '["email"]', TRUE, NOW(), NOW());

-- 显示插入结果
SELECT 'Initial data inserted successfully!' AS message;
SELECT COUNT(*) AS user_count FROM users;
SELECT COUNT(*) AS provider_count FROM provider_config;
SELECT COUNT(*) AS model_mapping_count FROM model_mapping;
SELECT COUNT(*) AS api_key_count FROM api_keys;
SELECT COUNT(*) AS alert_rule_count FROM alert_rules;
