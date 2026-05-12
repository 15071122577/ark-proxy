package com.ark.proxy.repository.mysql;

import com.ark.proxy.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层，提供用户实体的 CRUD 及自定义查询操作
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 匹配的用户实体，若不存在则返回空的 Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱地址
     * @return 匹配的用户实体，若不存在则返回空的 Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * 判断用户名是否已存在
     *
     * @param username 用户名
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByUsername(String username);

    /**
     * 判断邮箱是否已存在
     *
     * @param email 邮箱地址
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByEmail(String email);
}
