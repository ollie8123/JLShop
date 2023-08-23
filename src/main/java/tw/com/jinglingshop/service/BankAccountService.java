package tw.com.jinglingshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.BankAccountRepository;
import tw.com.jinglingshop.model.dao.UserRepository;
import tw.com.jinglingshop.model.domain.user.BankAccount;

@Service
public class BankAccountService {
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
	public BankAccount addBankAccount(BankAccount bank) {
		
		
		if (bank != null) {

			return bankAccountRepository.save(bank);

		}

		return null;
		
		
	
		
		
	}

}
