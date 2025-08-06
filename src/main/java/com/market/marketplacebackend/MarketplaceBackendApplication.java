package com.market.marketplacebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MarketplaceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceBackendApplication.class, args);
    }

}
