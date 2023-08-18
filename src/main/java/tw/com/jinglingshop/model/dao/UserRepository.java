package tw.com.jinglingshop.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.jinglingshop.model.domain.user.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    
    Optional<User> findByAccount(String account);
    Optional<User> findByEmail(String email);
    
}
