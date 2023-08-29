package tw.com.jinglingshop.controller;


import java.io.File;
import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tw.com.jinglingshop.utils.Result;


@RestController
@RequestMapping(path = "/test")
public class PhotoUploadTestController {
    
    @PostMapping(path="/photoUpload")
    public Result photoUpload(@RequestParam("productPageImgs") MultipartFile[] files) {
        //上傳路徑，這個路徑可以再自己改寫，之後從property裡面引入應該更好
        String uploadPath = "C:/uploads/";

        //檢查路徑是否存在並創造路徑
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            for (MultipartFile file : files) {
                //產生單一檔案的路徑
                String filePath = uploadPath + file.getOriginalFilename();
                //儲存檔案
                file.transferTo(new File(filePath));
                System.out.println("File written successfully: " + filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success("Test Upload Success");
    }
    
}
