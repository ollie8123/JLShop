package tw.com.jinglingshop.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tw.com.jinglingshop.model.dao.CreditcardRepository;
import tw.com.jinglingshop.model.dao.UserRepository;
import tw.com.jinglingshop.model.domain.user.Creditcard;
import tw.com.jinglingshop.model.domain.user.User;
import tw.com.jinglingshop.utils.Result;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private CreditcardRepository creditcardRepository;

	private final Path photoPath = Paths.get("src/image/userPhoto");



	public Result addUser(User user) {
		// 加密密碼
		String encodePwd = encoder.encode(user.getPassword());
		user.setPassword(encodePwd);

		// 隨機產生6位數帳號
		String randAccount;
		Optional<User>  existsAccount ;

		while (true) {
			Integer randNum = (int) (Math.random() * (999999999) + 1);
			randAccount = String.format("%09d", randNum);


			existsAccount = userRepository.findByAccount(randAccount);

			if (existsAccount.isEmpty()) {
				String account = "~" + randAccount;
				user.setAccount(account);
				user.setIsEnable(true);
				break;
			}
		}

		User save = userRepository.save(user);
		return Result.success("succcess", save);

	}

	public Result checkLogin(String email, String password) {

		Optional<User>  existsEmail = userRepository.findByEmail(email);

		if (existsEmail.isPresent()) {
			User user =  existsEmail.get();
			if (encoder.matches(password, user.getPassword() )){
				return Result.success("succcess", user);
			}

		}
		return Result.error("登入失敗");
	}

	public Result addPhoto(MultipartFile file, String email) {

		String path = Paths.get("src//image//userPhoto").toAbsolutePath().toString();

		// 處理檔案為空的情況
		// 可以拋出異常或返回錯誤訊息
		if (file.isEmpty()) {
			return Result.error("未上傳資料");
		}

		try {

			Optional<User>  existsEmail = userRepository.findByEmail(email);
			
			if (existsEmail.isEmpty()) {
				return Result.error("查無帳號");
			}
			
			User userAccount = existsEmail.get();

			String oldPhotoPath = userAccount.getPhotoPath();

			// 產生唯一的檔案名稱，避免重複
			String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

			// 確認存放照片的目錄是否存在，如果不存在，則創建該目錄
			File uploadDir = new File(path);
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}

			// 設定照片的完整路徑
			Path filePath = Paths.get(path, uniqueFileName);

			// 將檔案存儲到本地端
			Files.copy(file.getInputStream(), filePath);

			// 刪除舊檔案
			if (oldPhotoPath != null) {
				File oldFile = new File(path, oldPhotoPath);
				oldFile.delete();
			}

			userAccount.setPhotoPath(uniqueFileName);

			// 執行更新操作並返回更新後的用戶物件
			
			User save = userRepository.save(userAccount);
			return Result.success("succcess", save);

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("錯誤" + e.getMessage());
			return Result.error("上傳失敗");
		}

	}
	
	public Result updateUserInformation(User user,String email) {

		Optional<User>  existsEmail = userRepository.findByEmail(email);

		if (existsEmail.isPresent()) {
			
			User userAccount = existsEmail.get();

			
			userAccount.setAccount(user.getAccount());
			userAccount.setName(user.getName());
	        userAccount.setPhone(user.getPhone());
	        userAccount.setBirth(user.getBirth());
	        
	    	User save = userRepository.save(userAccount);
			return Result.success("succcess", save);
			
		}
		return Result.error("更新失敗");
	
	}
	
	public Boolean findByEmail(String email) {
	
		Optional<User> existsEamil = userRepository.findByEmail(email);
	
		if (existsEamil.isEmpty()) {
		
			return false;
		}else {
		
			return  true;
		}

	}
	
	public User getUserByEmail(String email) {
		Optional<User> existsEamil = userRepository.findByEmail(email);
		
		if (existsEamil.isEmpty()) {
			return null;
		}else {
			User user = existsEamil.get();
			return user;
			
		}
		
	}


	public Creditcard addCreditcard(Creditcard creditcard) {

		if(creditcard != null) {

			return creditcardRepository.save(creditcard);

		}

		return null;

	}

public List<Creditcard> findUserCreditcard(String email) {

	Optional<User> existsEamil = userRepository.findByEmail(email);

	if(existsEamil.isPresent()) {
		User user = existsEamil.get();

		List<Creditcard> creditcardInfoList = creditcardRepository.findAllByUserId(user.getId());

		 return creditcardInfoList;
	}
	 return new ArrayList<>();

	}

public String deleteCreditcard(Integer id) {

	Optional<Creditcard> existsId = creditcardRepository.findById(id);

	if(existsId.isPresent()) {
		Creditcard creditcard = existsId.get();

		creditcardRepository.delete(creditcard);

		 return "success";
	}
	 return "fail";

	}


public String loadImageAsResource(String email) {
	try {
        Optional<User> existsEmail = userRepository.findByEmail(email);


        if (!existsEmail.isPresent()) {
            return null;
        }

        User userData = existsEmail.get();
        String filename = userData.getPhotoPath();

        if (filename == null) {
            return null; // 如果filename為null，直接返回
        }


        Path file = photoPath.resolve(filename);
        byte[] fileBytes = Files.readAllBytes(file);
        return Base64.getEncoder().encodeToString(fileBytes);
    } catch (IOException e) {
        throw new RuntimeException("Failed to read file: " + e.getMessage(), e);
    }

 }



}
