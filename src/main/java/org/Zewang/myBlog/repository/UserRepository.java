package org.Zewang.myBlog.repository;

import org.Zewang.myBlog.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @description: 用户 Repository
 * @author "Zewang"
 * @version 1.0
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
