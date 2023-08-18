package tw.com.jinglingshop.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.jinglingshop.model.domain.user.Creditcard;
import tw.com.jinglingshop.model.domain.user.NormalAddress;

public interface NormalAddressRepository extends JpaRepository<NormalAddress, Integer> {
	
	@EntityGraph(attributePaths = "user")
    List<NormalAddress> findAll();
	
	List<NormalAddress> findAllByUserId(Integer userId);
	
	List<NormalAddress> findAllByUserIdOrderByIsDefaultDesc(Integer userId);
	

}
