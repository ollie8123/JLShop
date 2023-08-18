package tw.com.jinglingshop.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.jinglingshop.model.domain.user.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

	@EntityGraph(attributePaths = "user")
    List<BankAccount> findAll();
	
	
}
