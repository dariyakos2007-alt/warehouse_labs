package com.example.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EnableAsync
public class WarehouseApplication {

    public static void main(String[] args) {
        configureRenderDatabaseUrl();
        SpringApplication.run(WarehouseApplication.class, args);
    }

    private static void configureRenderDatabaseUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl == null || databaseUrl.isBlank() || System.getenv("SPRING_DATASOURCE_URL") != null) {
            return;
        }

        URI uri = URI.create(databaseUrl);
        String[] userInfo = uri.getRawUserInfo() == null ? new String[0] : uri.getRawUserInfo().split(":", 2);
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath();

        if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
            jdbcUrl += "?" + uri.getQuery();
        }

        System.setProperty("spring.datasource.url", jdbcUrl);

        if (userInfo.length > 0) {
            System.setProperty("spring.datasource.username", decode(userInfo[0]));
        }

        if (userInfo.length > 1) {
            System.setProperty("spring.datasource.password", decode(userInfo[1]));
        }
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

}
