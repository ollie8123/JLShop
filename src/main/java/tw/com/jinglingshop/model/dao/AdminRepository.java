package tw.com.jinglingshop.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.jinglingshop.model.domain.user.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    @EntityGraph(attributePaths = "user")
    List<Admin> findAll();
}
