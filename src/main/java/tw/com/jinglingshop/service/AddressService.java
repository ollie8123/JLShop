package tw.com.jinglingshop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.ConvenienceStoreAddressRepository;
import tw.com.jinglingshop.model.dao.NormalAddressRepository;
import tw.com.jinglingshop.model.dao.UserRepository;
import tw.com.jinglingshop.model.domain.user.ConvenienceStoreAddress;
import tw.com.jinglingshop.model.domain.user.NormalAddress;
import tw.com.jinglingshop.model.domain.user.User;

@Service
public class AddressService {

	@Autowired
	private NormalAddressRepository addressRep;

	@Autowired
	private ConvenienceStoreAddressRepository storeAddressRep;
	
	
	@Autowired
	private UserRepository userRepository;

	public NormalAddress addAddress(NormalAddress address) {

		if (address != null) {

			return addressRep.save(address);

		}

		return null;

	}
	
	
	public List<NormalAddress> findUserAddress(String email) {
		
		Optional<User> existsEamil = userRepository.findByEmail(email);
		
		if(existsEamil.isPresent()) {
			User user = existsEamil.get();
			
			List<NormalAddress> sortedAddresses = addressRep.findAllByUserIdOrderByIsDefaultDesc(user.getId());
			 return sortedAddresses;
		}
		 return new ArrayList<>();

		}

	public String deleteAddress(Integer id) {
		
		Optional<NormalAddress> existsId = addressRep.findById(id);
		
		if(existsId.isPresent()) {
			NormalAddress address = existsId.get();
			
			addressRep.delete(address);
		
			 return "success";
		}
		 return "fail";

		}
	
	
	public String addressIsDefault(Integer id) {
		
		Optional<NormalAddress> existsId = addressRep.findById(id);
		
		if(existsId.isPresent()) {
			NormalAddress creditcard = existsId.get();
			
			creditcard.setIsDefault(true);
			
			addressRep.save(creditcard);
		
			
			 return "success";
		}
		 return "fail";

		}
	
	
       public String allIsDefault(String email) {
    	   
    	   Optional<User> existsEamil = userRepository.findByEmail(email);
   		
   		if(existsEamil.isPresent()) {
   			
   			Integer id = existsEamil.get().getId();
   			
   			List<NormalAddress> addresses = addressRep.findAllByUserId(id);
   			
   		 for (NormalAddress address : addresses) {
             address.setIsDefault(false);
             addressRep.save(address);
         }
			 return "success";
   		}
   		return "fail";

		}
		
   	public ConvenienceStoreAddress addStoreAddress(ConvenienceStoreAddress storeAddress) {

		if (storeAddress != null) {

			return storeAddressRep.save(storeAddress);

		}

		return null;

	}
   	
        public List<ConvenienceStoreAddress> findUserStoreAddress(String email) {
		
		Optional<User> existsEamil = userRepository.findByEmail(email);
		
		if(existsEamil.isPresent()) {
			User user = existsEamil.get();
			
			List<ConvenienceStoreAddress> storeAddresses = storeAddressRep.findAllByUserId(user.getId());
			 return storeAddresses;
		}
		 return new ArrayList<>();

		}
	
        
    	public String deleteStoreAddress(Integer id) {
    		
    		Optional<ConvenienceStoreAddress> existsId = storeAddressRep.findById(id);
    		
    		if(existsId.isPresent()) {
    			ConvenienceStoreAddress storeAddress = existsId.get();
    			
    			storeAddressRep.delete(storeAddress);
    		
    			 return "success";
    		}
    		 return "fail";

    		}
	

}
