package tw.com.jinglingshop.utils;

import java.io.*;
import java.util.Base64;

/**
 * ClassName:photoUtile
 * Package:tw.com.jinglingshop.utils
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/30 上午 04:14
 * @Version 1.0
 */
public class photoUtil {

//    public static void main(String[] args) {
//        String base64ByPath = getBase64ByPath("C:\\Users\\chiu3\\OneDrive\\桌面\\新增資料夾\\D3CADA5D-B168-465D-9DF5-15E436A086BC.jpg");
//        System.out.println(base64ByPath);
//    }


    public static String getBase64ByPath(String Path){
        String base64String= "data:image/*;base64,";
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(Path).exists()?new File(Path):new File("C:\\testphoto\\999.png")));
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
        byte[] bytes = new byte[1024];
        int len;
        while ((len=bufferedInputStream.read(bytes))!=-1){
            byteArrayOutputStream.write(bytes,0,len);
        }
        byte[] allBytes = byteArrayOutputStream.toByteArray();
            base64String +=Base64.getEncoder().encodeToString(allBytes);
        return  base64String;
        } catch (IOException e) {
            return null;
        }

    }
}
