package com.museum.museumsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.museum.museumsystem.repository", "com.museum.museumsystem.mapper"})
public class MuseumSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MuseumSystemApplication.class, args);
    }

}
