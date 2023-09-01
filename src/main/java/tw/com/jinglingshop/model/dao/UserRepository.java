package tw.com.jinglingshop.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.jinglingshop.model.domain.user.User;

public interface UserRepository extends JpaRepository<User, Integer>{


    @Query("SELECT count(*) AS count FROM  NormalAddress nadd " +
            "left join ConvenienceStoreAddress cadd on cadd.user.id=nadd.user.id where nadd.user.id=:userId ")
    Integer selectUserAddressByUserEmail(@Param("userId") Integer userId);
    Optional<User> findByAccount(String account);
    Optional<User> findByEmail(String email);
    
}
