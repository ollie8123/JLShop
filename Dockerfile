# 使用官方的 Java 環境來建置
FROM openjdk:17

CMD mvn package

# 添加一個服務端口
EXPOSE 9999


# 將專案 jar 檔案複製到容器中
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# 在容器啟動時運行 Java 應用
ENTRYPOINT ["java","-jar","/app.jar"]