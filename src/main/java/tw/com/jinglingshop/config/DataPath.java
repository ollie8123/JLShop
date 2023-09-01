package tw.com.jinglingshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataPath {

    @Value("${jingling-shop.data.base-path}")
    private String basePath;

    @Value("${jingling-shop.data.page-imgs-folder-name}")
    private String pageImgsFolderName;

    @Value("${jingling-shop.data.specification-imgs-folder-name}")
    private String specificationImgsFolderName;

    public String getPageImgsPath(Integer pageId) {
        return basePath + pageId + "/" + pageImgsFolderName + "/";
    }

    public String getSpecImgsPath(Integer pageId) {
        return basePath + pageId + "/" + specificationImgsFolderName + "/";
    }
}
