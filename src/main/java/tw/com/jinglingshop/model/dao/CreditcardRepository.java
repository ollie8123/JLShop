package tw.com.jinglingshop.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.jinglingshop.model.domain.user.Creditcard;

public interface CreditcardRepository extends JpaRepository<Creditcard, Integer> {
	
	@EntityGraph(attributePaths = "user")
    List<Creditcard> findAll();
	
	List<Creditcard> findAllByUserId(Integer userId);
}
