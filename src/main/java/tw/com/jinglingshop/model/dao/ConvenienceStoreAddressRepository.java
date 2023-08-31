package tw.com.jinglingshop.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.jinglingshop.model.domain.user.ConvenienceStoreAddress;

public interface ConvenienceStoreAddressRepository extends JpaRepository<ConvenienceStoreAddress, Integer> {

	@EntityGraph(attributePaths = "user")
    List<ConvenienceStoreAddress> findAll();
	
	List<ConvenienceStoreAddress> findAllByUserId(Integer userId);
}
