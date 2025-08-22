package com.market.marketplacebackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jwt.secret=ksf92jf12jf23jdfh4skdlf2398rjskfjweofjr9203sldf23r"
})
class MarketplaceBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
