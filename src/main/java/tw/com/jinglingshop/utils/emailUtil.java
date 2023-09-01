package tw.com.jinglingshop.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * ClassName:emailUtil
 * Package:tw.com.jinglingshop.utils
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/30 下午 10:11
 * @Version 1.0
 */
public class emailUtil {

    public static void testEmail(List<HashMap<String,Object>> emailMsg) throws Exception{

        for (int i=0;i<emailMsg.size();i++){
            ArrayList<String> path = new ArrayList<>();
            String name=(String) emailMsg.get(i).get("name");
            String phone=(String) emailMsg.get(i).get("phone");
            String adderss=(String) emailMsg.get(i).get("addressDetail");
            Integer orderPrice=(Integer) emailMsg.get(i).get("orderPrice");
            Integer discountPrice=(Integer) emailMsg.get(i).get("discountPrice");
            String userEmail=(String) emailMsg.get(i).get("userEmail");
//            String seller=(String) emailMsg.get(i).get("seller");

            if("FamilyMart".equals(emailMsg.get(i).get("type"))){
                System.out.println("店家"+emailMsg.get(i).get("storeName"));
            }else if("Normal".equals(emailMsg.get(i).get("type"))){
                System.out.println("addressType"+emailMsg.get(i).get("addressType"));
            }
            String list="";
            List<HashMap<String,Object>> productList =(List) emailMsg.get(i).get("emailSellerProductList");
            for (int j=0;j<productList.size();j++){
                String productPhoto=(String) productList.get(j).get("productPhoto");
                path.add(productPhoto);
                String productName=(String) productList.get(j).get("productName");
                Integer productCount=(Integer) productList.get(j).get("productCount");
                Integer productPrice=(Integer) productList.get(j).get("productPrice");
                String Specification="";
                if(!"~NoSpecification".equals(productList.get(j).get("productMainName"))){
                    String productMainName =(String) productList.get(j).get("productMainName");
                    System.out.println("主規格"+productList.get(j).get("productMainName"));
                    Specification+=productMainName;
                }
                if(!"~NoSpecification".equals(productList.get(j).get("productSecondName"))){
                    String productSecondName =(String) productList.get(j).get("productSecondName");
                    System.out.println("次規格"+productSecondName);
                    Specification+="/"+productSecondName;
                }
                int to=productCount*productPrice;
                     list+=
                        "            <div class=\"list\">\n" +
                                "                <div class=\"item\">\n" +
                                "                    <table  style=\"font-size: 25PX;\">\n" +
                                "                        <tr>\n" +

                                "                             <td> <img src=\"cid:image"+j+"\"></td>\n" +
                                "                            <td>"+productName+"</td>\n" +
                                "                            <td>"+Specification+"</td>\n" +
                                "                            <td>"+productCount+"</td></td>\n" +
                                "                            <td>NT$ "+productPrice+"</td>\n" +
                                "                            <td class=\"item-total\">NT$ "+to+"</td>\n" +
                                "                        </tr>\n" +
                                "                    </table>\n" +
                                "                </div>\n" +
                                "            </div>";
            }
            orderEmail(name,phone,adderss,discountPrice,orderPrice,userEmail,list,path);
        }
//      int a=1/0;
    }

    public static void orderEmail(String name,String phone,String adderss,Integer discountPrice,Integer orderPrice,String userEmail,String list,ArrayList path) throws Exception {
        final String sender = "nmsl880120@gmail.com";
        final String urpass = "jvvfbxsvhrtbjpsv";
        Properties set = new Properties();
        set.put("mail.smtp.starttls.enable", "true");
        set.put("mail.smtp.auth", "true");
        set.put("mail.smtp.host", "smtp.gmail.com");
        set.put("mail.smtp.port", "587");
        Session session = Session.getInstance(set,new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, urpass);
            }});

        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
            transport.connect();
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(userEmail));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            //標題
            email.setSubject("");
            Multipart multipart = new MimeMultipart();

            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Document</title>\n" +
                    "    <link rel=\"stylesheet\" href=\"css/stylesheet.css\">\n" +
                    "  <style>\n" +
                    "        /* http://meyerweb.com/eric/tools/css/reset/ \n" +
                    "   v2.0 | 20110126\n" +
                    "   License: none (public domain)\n" +
                    "*/\n" +
                    "\n" +
                    "html, body, div, span, applet, object, iframe,\n" +
                    "h1, h2, h3, h4, h5, h6, p, blockquote, pre,\n" +
                    "a, abbr, acronym, address, big, cite, code,\n" +
                    "del, dfn, em, img, ins, kbd, q, s, samp,\n" +
                    "small, strike, strong, sub, sup, tt, var,\n" +
                    "b, u, i, center,\n" +
                    "dl, dt, dd, ol, ul, li,\n" +
                    "fieldset, form, label, legend,\n" +
                    "table, caption, tbody, tfoot, thead, tr, th, td,\n" +
                    "article, aside, canvas, details, embed, \n" +
                    "figure, figcaption, footer, header, hgroup, \n" +
                    "menu, nav, output, ruby, section, summary,\n" +
                    "time, mark, audio, video {\n" +
                    "\tmargin: 0;\n" +
                    "\tpadding: 0;\n" +
                    "\tborder: 0;\n" +
                    "\tfont-size: 100%;\n" +
                    "\tfont: inherit;\n" +
                    "\tvertical-align: baseline;\n" +
                    "}\n" +
                    "/* HTML5 display-role reset for older browsers */\n" +
                    "article, aside, details, figcaption, figure, \n" +
                    "footer, header, hgroup, menu, nav, section {\n" +
                    "\tdisplay: block;\n" +
                    "}\n" +
                    "body {\n" +
                    "\tline-height: 1;\n" +
                    "}\n" +
                    "ol, ul {\n" +
                    "\tlist-style: none;\n" +
                    "}\n" +
                    "blockquote, q {\n" +
                    "\tquotes: none;\n" +
                    "}\n" +
                    "blockquote:before, blockquote:after,\n" +
                    "q:before, q:after {\n" +
                    "\tcontent: '';\n" +
                    "\tcontent: none;\n" +
                    "}\n" +
                    "table {\n" +
                    "\tborder-collapse: collapse;\n" +
                    "\tborder-spacing: 0;\n" +
                    "}\n" +
                    "\n" +
                    ".wrap{\n" +
                    "            width: 70%;\n" +
                    "            margin: 0 auto;\n" +
                    "        }\n" +
                    "\n" +
                    "      \n" +
                    "        .top,\n" +
                    "        .bottom {\n" +
                    "            margin-bottom: 20px;\n" +
                    "            border: #e7eeea solid;\n" +
                    "        }\n" +
                    "\n" +
                    "        .top h3,\n" +
                    "        .bottom h3 {\n" +
                    "            padding: 28px;\n" +
                    "            font-size: 23px;\n" +
                    "            font-weight: bold;\n" +
                    "            color: #829886;\n" +
                    "            background-color: #e7eeea;\n" +
                    "        }\n" +
                    "\n" +
                    "        .price,\n" +
                    "        .total {\n" +
                    "            padding: 15px;\n" +
                    "            display: flex;\n" +
                    "            justify-content: space-between;\n" +
                    "            color: #829886;\n" +
                    "        }\n" +
                    "\n" +
                    "        .total span {\n" +
                    "            font-size: 20px;\n" +
                    "            font-weight: bold;\n" +
                    "        }\n" +
                    "\n" +
                    "        .list {\n" +
                    "            padding: 15px;\n" +
                    "            border-bottom: 1px solid #e7eeea;\n" +
                    "        }\n" +
                    "\n" +
                    "        .item {\n" +
                    "            color: #829886;\n" +
                    "            width: 100%;\n" +
                    "        }\n" +
                    "\n" +
                    "        .item img {\n" +
                    "            width: 150px;\n" +
                    "            height: auto;\n" +
                    "            margin-right: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .item table {\n" +
                    "            width: 100%;\n" +
                    "            border-collapse: collapse;\n" +
                    "        }\n" +
                    "\n" +
                    "        .item td {\n" +
                    "            padding: 10px;\n" +
                    "            text-align: center;\n" +
                    "            vertical-align: middle;\n" +
                    "        }\n" +
                    "\n" +
                    "        .item-total {\n" +
                    "            font-size: 25px;\n" +
                    "            font-weight: bold;\n" +
                    "        }\n" +
                    "  </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"wrap\">\n" +
                    "\n" +
                    "\n" +
                    "         <div class=\"top\" style=\"font-size: 25PX;\">\n" +
                    "            <h3>訂購者資訊</h3></h3>\n" +
                    "            <div class=\"price\">\n" +
                    "                <div style=\"width: 20%;\">姓名:"+name+"</div>\n" +
                    "                <div style=\"width: 30%;\">電話:"+phone+"</div>\n" +
                    "                <div style=\"width: 50%;\">收件地址:"+adderss+"</div>\n" +
                    "            </div>\n" +
                    "        </div>  \n" +
                    "\n" +
                    "        <div class=\"bottom\">\n" +
                    "            <h3>購物清單</h3>\n" +
                    list+
                    "\n" +
                    "\n" +
                    "\n" +
                    "        </div>\n" +
                    "\n" +
                    "\n" +
                    "     \n" +
                    "        <div class=\"top\"  style=\"font-size: 25PX;\">\n" +
                    "            <h3>訂單摘要</h3>\n" +
                    "            <div class=\"price\">\n" +
                    "                <span>優惠券</span>\n" +
                    "                <span> "+(discountPrice==0?"未使用":"NT$"+discountPrice)+"</span>\n" +
                    "            </div>\n" +
                    "            <div class=\"total\" >\n" +
                    "                <span style=\"font-size: 30PX;\">總計</span>\n" +
                    "                <span style=\"font-size: 30PX;\">NT$ "+orderPrice+"</span>\n" +
                    "            </div>\n" +
                    "        </div>  \n" +
                    "     \n" +
                    "\n" +
                    "    </div>\n" +
                    "    \n" +
                    "</body>\n" +
                    "</html>\n";

            messageBodyPart.setContent(htmlContent, "text/html; charset=UTF-8");
            multipart.addBodyPart(messageBodyPart);

            for (int i=0;i<path.size();i++){
                String image = getImage((String) path.get(i));
                // 圖片部分
                BodyPart messageBodyPart2 = new MimeBodyPart();
                DataSource source = new FileDataSource(image);
                messageBodyPart2.setDataHandler(new DataHandler(source));
                messageBodyPart2.setHeader("Content-ID", "<image"+i+">");
                multipart.addBodyPart(messageBodyPart2);
            }


            email.setContent(multipart);
            transport.sendMessage(email, email.getAllRecipients());
        }catch (MessagingException e) {
            throw new RuntimeException(e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                }
            }
            System.out.println("end");
        }
    }
    public static String getImage(String urlStr) {
        File file = new File(urlStr);
        if (file.exists()) {
            return urlStr;
        } else {
            return "C:\\testphoto\\999.png";
        }
    }











//    public static void main(String[] args) {
//
//        String name="";
//        String phone="";
//        String adderss="";
//
//
//        String list=
//                "            <div class=\"list\">\n" +
//                        "                <div class=\"item\">\n" +
//                        "                    <table  style=\"font-size: 25PX;\">\n" +
//                        "                        <tr>\n" +
//                        "                            <td><img src=\"https://i.postimg.cc/zB7qtwdD/pic.png\" alt=\"picture of cake\"></td>\n" +
//                        "                            <td>焦糖馬卡龍</td>\n" +
//                        "                            <td>規格</td>\n" +
//                        "                            <td>2</td></td>\n" +
//                        "                            <td>NT$ 900</td>\n" +
//                        "                            <td class=\"item-total\">NT$ 1800</td>\n" +
//                        "                        </tr>\n" +
//                        "                    </table>\n" +
//                        "                </div>\n" +
//                        "            </div>";
//
//
//
//
//
//
//        //Turn off Two Factor Authentication
//        //Turn off less secure app
//        final String sender = "nmsl880120@gmail.com"; // The sender email
//        final String urpass = "jvvfbxsvhrtbjpsv";
//        Properties set = new Properties();
//        //Set values to the property
//        set.put("mail.smtp.starttls.enable", "true");
//        set.put("mail.smtp.auth", "true");
//        set.put("mail.smtp.host", "smtp.gmail.com");
//        set.put("mail.smtp.port", "587");
//        Session session = Session.getInstance(set,new javax.mail.Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(sender, urpass);
//            }});
//
//        Transport transport = null;
//        try {
//            transport = session.getTransport("smtp");
//            transport.connect();
//
//            //    for (int i = 0; i < 10000; i++) {
//            Message email = new MimeMessage(session);
//            email.setFrom(new InternetAddress("chiu2121yu@gmail.com"));
//            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse("chiu212155@gmail.com"));
//            //標題
//            email.setSubject("");
//
//            BodyPart messageBodyPart = new MimeBodyPart();
//            String htmlContent = "<!DOCTYPE html>\n" +
//                    "<html lang=\"en\">\n" +
//                    "<head>\n" +
//                    "    <meta charset=\"UTF-8\">\n" +
//                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
//                    "    <title>Document</title>\n" +
//                    "    <link rel=\"stylesheet\" href=\"css/stylesheet.css\">\n" +
//                    "  <style>\n" +
//                    "        /* http://meyerweb.com/eric/tools/css/reset/ \n" +
//                    "   v2.0 | 20110126\n" +
//                    "   License: none (public domain)\n" +
//                    "*/\n" +
//                    "\n" +
//                    "html, body, div, span, applet, object, iframe,\n" +
//                    "h1, h2, h3, h4, h5, h6, p, blockquote, pre,\n" +
//                    "a, abbr, acronym, address, big, cite, code,\n" +
//                    "del, dfn, em, img, ins, kbd, q, s, samp,\n" +
//                    "small, strike, strong, sub, sup, tt, var,\n" +
//                    "b, u, i, center,\n" +
//                    "dl, dt, dd, ol, ul, li,\n" +
//                    "fieldset, form, label, legend,\n" +
//                    "table, caption, tbody, tfoot, thead, tr, th, td,\n" +
//                    "article, aside, canvas, details, embed, \n" +
//                    "figure, figcaption, footer, header, hgroup, \n" +
//                    "menu, nav, output, ruby, section, summary,\n" +
//                    "time, mark, audio, video {\n" +
//                    "\tmargin: 0;\n" +
//                    "\tpadding: 0;\n" +
//                    "\tborder: 0;\n" +
//                    "\tfont-size: 100%;\n" +
//                    "\tfont: inherit;\n" +
//                    "\tvertical-align: baseline;\n" +
//                    "}\n" +
//                    "/* HTML5 display-role reset for older browsers */\n" +
//                    "article, aside, details, figcaption, figure, \n" +
//                    "footer, header, hgroup, menu, nav, section {\n" +
//                    "\tdisplay: block;\n" +
//                    "}\n" +
//                    "body {\n" +
//                    "\tline-height: 1;\n" +
//                    "}\n" +
//                    "ol, ul {\n" +
//                    "\tlist-style: none;\n" +
//                    "}\n" +
//                    "blockquote, q {\n" +
//                    "\tquotes: none;\n" +
//                    "}\n" +
//                    "blockquote:before, blockquote:after,\n" +
//                    "q:before, q:after {\n" +
//                    "\tcontent: '';\n" +
//                    "\tcontent: none;\n" +
//                    "}\n" +
//                    "table {\n" +
//                    "\tborder-collapse: collapse;\n" +
//                    "\tborder-spacing: 0;\n" +
//                    "}\n" +
//                    "\n" +
//                    ".wrap{\n" +
//                    "            width: 70%;\n" +
//                    "            margin: 0 auto;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "      \n" +
//                    "        .top,\n" +
//                    "        .bottom {\n" +
//                    "            margin-bottom: 20px;\n" +
//                    "            border: #e7eeea solid;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .top h3,\n" +
//                    "        .bottom h3 {\n" +
//                    "            padding: 28px;\n" +
//                    "            font-size: 23px;\n" +
//                    "            font-weight: bold;\n" +
//                    "            color: #829886;\n" +
//                    "            background-color: #e7eeea;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .price,\n" +
//                    "        .total {\n" +
//                    "            padding: 15px;\n" +
//                    "            display: flex;\n" +
//                    "            justify-content: space-between;\n" +
//                    "            color: #829886;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .total span {\n" +
//                    "            font-size: 20px;\n" +
//                    "            font-weight: bold;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .list {\n" +
//                    "            padding: 15px;\n" +
//                    "            border-bottom: 1px solid #e7eeea;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .item {\n" +
//                    "            color: #829886;\n" +
//                    "            width: 100%;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .item img {\n" +
//                    "            width: 150px;\n" +
//                    "            height: auto;\n" +
//                    "            margin-right: 20px;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .item table {\n" +
//                    "            width: 100%;\n" +
//                    "            border-collapse: collapse;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .item td {\n" +
//                    "            padding: 10px;\n" +
//                    "            text-align: center;\n" +
//                    "            vertical-align: middle;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .item-total {\n" +
//                    "            font-size: 25px;\n" +
//                    "            font-weight: bold;\n" +
//                    "        }\n" +
//                    "  </style>\n" +
//                    "</head>\n" +
//                    "<body>\n" +
//                    "    <div class=\"wrap\">\n" +
//                    "\n" +
//                    "\n" +
//                    "         <div class=\"top\" style=\"font-size: 25PX;\">\n" +
//                    "            <h3>訂購者資訊</h3></h3>\n" +
//                    "            <div class=\"price\">\n" +
//                    "                <div style=\"width: 20%;\">姓名:"+name+"</div>\n" +
//                    "                <div style=\"width: 30%;\">電話:"+phone+"</div>\n" +
//                    "                <div style=\"width: 50%;\">收件地址:"+adderss+"</div>\n" +
//                    "            </div>\n" +
//                    "        </div>  \n" +
//                    "\n" +
//                    "        <div class=\"bottom\">\n" +
//                    "            <h3>購物清單</h3>\n" +
//                    "            <div class=\"list\">\n" +
//                    "                <div class=\"item\">\n" +
//                    "                    <table  style=\"font-size: 25PX;\">\n" +
//                    "                        <tr>\n" +
//                    "                            <td><img src=\"https://i.postimg.cc/zB7qtwdD/pic.png\" alt=\"picture of cake\"></td>\n" +
//                    "                            <td>焦糖馬卡龍</td>\n" +
//                    "                            <td>規格</td>\n" +
//                    "                            <td>2</td></td>\n" +
//                    "                            <td>NT$ 900</td>\n" +
//                    "                            <td class=\"item-total\">NT$ 1800</td>\n" +
//                    "                        </tr>\n" +
//                    "                    </table>\n" +
//                    "                </div>\n" +
//                    "            </div>\n" +
//                    "\n" +
//                    "            <div class=\"list\">\n" +
//                    "                <div class=\"item\">\n" +
//                    "                    <table  style=\"font-size: 25PX;\">\n" +
//                    "                        <tr>\n" +
//                    "                            <td><img src=\"https://i.postimg.cc/zB7qtwdD/pic.png\" alt=\"picture of cake\"></td>\n" +
//                    "                            <td>焦糖馬卡龍</td>\n" +
//                    "                            <td>規格</td>\n" +
//                    "                            <td>2</td></td>\n" +
//                    "                            <td>NT$ 900</td>\n" +
//                    "                            <td class=\"item-total\">NT$ 1800</td>\n" +
//                    "                        </tr>\n" +
//                    "                    </table>\n" +
//                    "                </div>\n" +
//                    "            </div>\n" +
//                    "\n" +
//                    "\n" +
//                    "\n" +
//                    "        </div>\n" +
//                    "\n" +
//                    "\n" +
//                    "     \n" +
//                    "        <div class=\"top\"  style=\"font-size: 25PX;\">\n" +
//                    "            <h3>訂單摘要</h3>\n" +
//                    "            <div class=\"price\">\n" +
//                    "                <span>小計</span>\n" +
//                    "                <span>NT$ 2,700</span>\n" +
//                    "            </div>\n" +
//                    "            <div class=\"price\">\n" +
//                    "                <span>優惠券</span>\n" +
//                    "                <span>NT$ 300</span>\n" +
//                    "            </div>\n" +
//                    "            <div class=\"total\" >\n" +
//                    "                <span style=\"font-size: 30PX;\">總計</span>\n" +
//                    "                <span style=\"font-size: 30PX;\">NT$ 3,000</span>\n" +
//                    "            </div>\n" +
//                    "        </div>  \n" +
//                    "     \n" +
//                    "\n" +
//                    "    </div>\n" +
//                    "    \n" +
//                    "</body>\n" +
//                    "</html>\n";
//            messageBodyPart.setContent(htmlContent, "text/html; charset=UTF-8");
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(messageBodyPart);
//            email.setContent(multipart);
//            transport.sendMessage(email, email.getAllRecipients());
//
//
//            //                   email.setText("");
//            //                   transport.sendMessage(email, email.getAllRecipients());
//            //                   System.out.println("2!");
//            //                  Thread.sleep(2000);
//            //                 System.out.println("1!");
//
//            //   }
//        }catch (MessagingException e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (transport != null) {
//                try {
//                    transport.close();
//                } catch (MessagingException e) {
//                    // Handle the exception if necessary.
//                }
//            }
//            System.out.println("end");
//        }
//    }

}

