package com.market.marketplacebackend.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.dto.ProductCreateRequestDto;
import com.market.marketplacebackend.product.dto.ProductUpdateRequestDto;
import com.market.marketplacebackend.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jwt.secret=ksf92jf12jf23jdfh4skdlf2398rjskfjweofjr9203sldf9230jsdf023r"
})
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    final long NON_EXISTENT_ACCOUNT_ID = 999L;
    final long NON_EXISTENT_PRODUCT_ID = 999L;

    @Test
    @DisplayName("상품 등록 통합 성공 테스트")
    void createProduct_Integration_Success() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        ProductCreateRequestDto productCreateRequestDto = ProductCreateRequestDto.builder()
                .name("test")
                .price(10000)
                .description("test description")
                .stock(100)
                .category(Category.FASHION)
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/product")
                        .param("accountId", String.valueOf(seller.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 등록 완료"))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Integer productIdInt = JsonPath.parse(responseBody).read("$.data.id");
        Long productId = productIdInt.longValue();
        Optional<Product> product = productRepository.findById(productId);
        assertThat(product).isPresent();
        assertThat(product.get().getName()).isEqualTo(productCreateRequestDto.getName());
        assertThat(product.get().getPrice()).isEqualTo(productCreateRequestDto.getPrice());
        assertThat(product.get().getStock()).isEqualTo(productCreateRequestDto.getStock());
        assertThat(product.get().getCategory()).isEqualTo(productCreateRequestDto.getCategory());

    }

    @Test
    @DisplayName("상품 등록 통합 실패 테스트 - 존재하지 않는 계정")
    void createProduct_Integration_AccountNotFound_Fail() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        ProductCreateRequestDto productCreateRequestDto = ProductCreateRequestDto.builder()
                .name("test")
                .price(10000)
                .description("test description")
                .stock(100)
                .category(Category.FASHION)
                .build();

        mockMvc.perform(post("/product")
                        .param("accountId", String.valueOf(NON_EXISTENT_ACCOUNT_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateRequestDto)))
                .andExpect(jsonPath("$.code").value(ErrorCode.ACCOUNT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("존재하지 않는 계정입니다."))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    @DisplayName("상품 등록 통합 실패 테스트 - CUSTOMER 권한")
    void createProduct_Integration_ForbiddenNotSeller_Fail() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.BUYER)
                .build();
        accountRepository.save(seller);
        ProductCreateRequestDto productCreateRequestDto = ProductCreateRequestDto.builder()
                .name("test")
                .price(10000)
                .description("test description")
                .stock(100)
                .category(Category.FASHION)
                .build();

        mockMvc.perform(post("/product")
                        .param("accountId", String.valueOf(seller.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateRequestDto)))
                .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN_NOT_SELLER.getCode()))
                .andExpect(jsonPath("$.message").value("셀러 권한이 없는 계정입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("상품 수정 통합 성공 테스트")
    void updateProduct_Integration_Success() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        Product product = Product.builder()
                .name("old product")
                .price(1000)
                .description("old product description")
                .stock(100)
                .category(Category.FASHION)
                .account(seller)
                .build();
        productRepository.save(product);
        ProductUpdateRequestDto productUpdateRequestDto = ProductUpdateRequestDto.builder()
                .name("new product")
                .price(2000)
                .description("new product description")
                .stock(200)
                .category(Category.SPORTS)
                .build();

        mockMvc.perform(patch("/product/{productId}",product.getId())
                        .param("accountId", String.valueOf(seller.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 수정 완료"))
                .andExpect(jsonPath("$.data.name").value(productUpdateRequestDto.getName()))
                .andExpect(jsonPath("$.data.price").value(productUpdateRequestDto.getPrice()))
                .andExpect(jsonPath("$.data.description").value(productUpdateRequestDto.getDescription()))
                .andExpect(jsonPath("$.data.stock").value(productUpdateRequestDto.getStock()))
                .andExpect(jsonPath("$.data.category").value(productUpdateRequestDto.getCategory().name()));

        Optional<Product> productResult = productRepository.findById(product.getId());
        assertThat(productResult).isPresent();
        assertThat(productResult.get().getName()).isEqualTo("new product");
        assertThat(productResult.get().getPrice()).isEqualTo(2000);
        assertThat(productResult.get().getDescription()).isEqualTo("new product description");
        assertThat(productResult.get().getStock()).isEqualTo(200);
        assertThat(productResult.get().getCategory().getCategoryName()).isEqualTo(Category.SPORTS.getCategoryName());
    }

    @Test
    @DisplayName("상품 수정 통합 실패 테스트 - 다른 사람 상품")
    void updateProduct_Integration_ForbiddenProduct_Fail() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        Account otherSeller = Account.builder()
                .name("다른테스트판매자")
                .email("other_seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(otherSeller);
        Product product = Product.builder()
                .name("old product")
                .price(1000)
                .description("old product description")
                .stock(100)
                .category(Category.FASHION)
                .account(seller)
                .build();
        productRepository.save(product);
        ProductUpdateRequestDto productUpdateRequestDto = ProductUpdateRequestDto.builder()
                .name("new product")
                .price(2000)
                .description("new product description")
                .stock(200)
                .category(Category.SPORTS)
                .build();

        mockMvc.perform(patch("/product/{productId}",product.getId())
                        .param("accountId", String.valueOf(otherSeller.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateRequestDto)))
                .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN_PRODUCT.getCode()))
                .andExpect(jsonPath("$.message").value("해당 상품에 대한 권한이 없습니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("상품 수정 통합 실패 테스트 - 존재하지 않는 제품")
    void updateProduct_Integration_ProductNotFound_Fail() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        ProductUpdateRequestDto productUpdateRequestDto = ProductUpdateRequestDto.builder()
                .name("new product")
                .price(2000)
                .description("new product description")
                .stock(200)
                .category(Category.SPORTS)
                .build();

        mockMvc.perform(patch("/product/{productId}",NON_EXISTENT_PRODUCT_ID)
                        .param("accountId", String.valueOf(seller.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateRequestDto)))
                .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("상품 삭제 통합 성공 테스트")
    void deleteProduct_Integration_Success() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        Product product = Product.builder()
                .name("old product")
                .price(1000)
                .description("old product description")
                .stock(100)
                .category(Category.FASHION)
                .account(seller)
                .build();
        productRepository.save(product);

        mockMvc.perform(delete("/product/{productId}",product.getId())
                        .param("accountId", String.valueOf(seller.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<Product> productResult = productRepository.findById(product.getId());
        assertThat(productResult).isNotPresent();
    }

    @Test
    @DisplayName("상품 삭제 통합 실패 테스트 - 다른 사람 상품")
    void deleteProduct_Integration_ForbiddenProduct_Fail() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        Account otherSeller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(otherSeller);
        Product product = Product.builder()
                .name("old product")
                .price(1000)
                .description("old product description")
                .stock(100)
                .category(Category.FASHION)
                .account(seller)
                .build();
        productRepository.save(product);

        mockMvc.perform(delete("/product/{productId}",product.getId())
                        .param("accountId", String.valueOf(otherSeller.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN_PRODUCT.getCode()))
                .andExpect(jsonPath("$.message").value("해당 상품에 대한 권한이 없습니다."))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    @DisplayName("상품 삭제 통합 실패 테스트 - 존재하지 않는 상품")
    void deleteProduct_Integration_ProductNotFound_Fail() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);

        mockMvc.perform(delete("/product/{productId}",NON_EXISTENT_PRODUCT_ID)
                        .param("accountId", String.valueOf(seller.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }
    @Test
    @DisplayName("상품 조회 통합 성공 테스트 - 전체")
    void findAllProducts_Integration_Success() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        Product product = Product.builder()
                .name("old product")
                .price(1000)
                .description("old product description")
                .stock(100)
                .category(Category.FASHION)
                .account(seller)
                .build();
        productRepository.save(product);
        Optional<Product> productResult = productRepository.findById(product.getId());

        mockMvc.perform(get("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 전체 조회 완료"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.content[0].name").value(productResult.get().getName()))
                .andExpect(jsonPath("$.data.content[0].price").value(productResult.get().getPrice()))
                .andExpect(jsonPath("$.data.content[0].category").value(productResult.get().getCategory().name()))
                .andExpect(jsonPath("$.data.content[0].sellerName").value(productResult.get().getAccount().getName()));
    }
    @Test
    @DisplayName("상품 조회 통합 성공 테스트 - 카테고리")
    void findCategoryProducts_Integration_Success() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        Product product = Product.builder()
                .name("old product")
                .price(1000)
                .description("old product description")
                .stock(100)
                .category(Category.FASHION)
                .account(seller)
                .build();
        productRepository.save(product);
        Optional<Product> productResult = productRepository.findById(product.getId());

        mockMvc.perform(get("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "2")
                        .param("category", "FASHION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 전체 조회 완료"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.content[0].name").value(productResult.get().getName()))
                .andExpect(jsonPath("$.data.content[0].price").value(productResult.get().getPrice()))
                .andExpect(jsonPath("$.data.content[0].category").value(productResult.get().getCategory().name()))
                .andExpect(jsonPath("$.data.content[0].sellerName").value(productResult.get().getAccount().getName()));
    }
    @Test
    @DisplayName("상품 조회 통합 실패 테스트 - 카테고리(존재하지 않는 카테고리)")
    void findCategoryProducts_Integration_Failure() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        Product product = Product.builder()
                .name("old product")
                .price(1000)
                .description("old product description")
                .stock(100)
                .category(Category.FASHION)
                .account(seller)
                .build();
        productRepository.save(product);

        mockMvc.perform(get("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "2")
                        .param("category", "NONE"))
                .andExpect(jsonPath("$.code").value(ErrorCode.CATEGORY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("존재하지 않는 카테고리입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }
    @Test
    @DisplayName("상품 조회 통합 성공 테스트 - 상세")
    void findDetailProducts_Integration_Success() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);
        Product product = Product.builder()
                .name("old product")
                .price(1000)
                .description("old product description")
                .stock(100)
                .category(Category.FASHION)
                .account(seller)
                .build();
        productRepository.save(product);
        Optional<Product> productResult = productRepository.findById(product.getId());

        mockMvc.perform(get("/product/{productId}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 상세 조회 완료"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.name").value(productResult.get().getName()))
                .andExpect(jsonPath("$.data.price").value(productResult.get().getPrice()))
                .andExpect(jsonPath("$.data.category").value(productResult.get().getCategory().name()))
                .andExpect(jsonPath("$.data.sellerName").value(productResult.get().getAccount().getName()));
    }

    @Test
    @DisplayName("상품 조회 통합 실패 테스트 - 상세(존재하지 않는 상품)")
    void findDetailProducts_Integration_Failure() throws Exception {
        Account seller = Account.builder()
                .name("테스트판매자")
                .email("seller@test.com")
                .password("password")
                .accountRole(AccountRole.SELLER)
                .build();
        accountRepository.save(seller);

        mockMvc.perform(get("/product/{productId}", NON_EXISTENT_PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}