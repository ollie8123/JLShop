package tw.com.jinglingshop.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * ClassName:DeltaProcessor
 * Package:tw.com.jinglingshop.utils
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/13 下午 02:05
 * @Version 1.0
 */
//編輯器測試
public class DeltaProcessor {

    public void processDelta(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);
        JsonNode opsNode = rootNode.get("aaa").get("ops");

        for (JsonNode op : opsNode) {
            if (op.has("insert")) {
                JsonNode insertNode = op.get("insert");
                if (insertNode.has("image")) {
                    String imageData = insertNode.get("image").asText();
                    saveImage(imageData);
                }
            }
        }
    }

    private void saveImage(String imageData) throws Exception {
        String base64Image = imageData.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        Files.write(Paths.get("output.jpg"), imageBytes);
    }

    public static void main(String[] args) throws Exception {
        String jsonString = "{...}";  // 用您的 JSON 字符串替换
        new DeltaProcessor().processDelta(jsonString);
    }
}
