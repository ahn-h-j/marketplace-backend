package com.market.marketplacebackend.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.SignUpDto;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.product.controller.ProductController;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.dto.ProductCreateRequestDto;
import com.market.marketplacebackend.product.dto.ProductUpdateRequestDto;
import com.market.marketplacebackend.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }


    @Test
    @DisplayName("상품 등록 성공(컨트롤러)")
    void createProduct_Success_Controller() throws Exception {
        // given
        SignUpDto signUpDto = SignUpDto.builder()
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        Account account = signUpDto.toEntity();

        ProductCreateRequestDto productCreateRequestDto = ProductCreateRequestDto.builder()
                .name("test Product")
                .price(10000)
                .description("맛있는 사과입니다")
                .stock(50)
                .category(Category.FOOD)
                .build();
        Product product = productCreateRequestDto.toEntity(account);
        when(productService.createProduct(eq(1L), any(ProductCreateRequestDto.class))).thenReturn(product);

        // when & then
        mockMvc.perform(post("/product")
                        .param("accountId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateRequestDto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("상품 등록 완료"))
                        .andExpect(jsonPath("$.code").value("OK"));

        verify(productService, times(1)).createProduct(eq(1L), any(productCreateRequestDto.getClass()));
    }

    @Test
    @DisplayName("상품 등록 실패(컨트롤러) - Valid")
    void createProduct_Failure_Controller() throws Exception {
        // given
        ProductCreateRequestDto productCreateRequestDto = ProductCreateRequestDto.builder()
                .price(10000)
                .description("맛있는 사과입니다")
                .stock(50)
                .category(Category.FOOD)
                .build();

        // when & then
        mockMvc.perform(post("/product")
                        .param("accountId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 수정 성공(컨트롤러)")
    void updateProduct_Success_Controller() throws Exception {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();

        ProductUpdateRequestDto productUpdateRequestDto = ProductUpdateRequestDto.builder()
                .name("new Product")
                .price(20000)
                .description("신 상품입니다")
                .stock(100)
                .category(Category.FASHION)
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .name(productUpdateRequestDto.getName())
                .price(productUpdateRequestDto.getPrice())
                .description(productUpdateRequestDto.getDescription())
                .stock(productUpdateRequestDto.getStock())
                .category(productUpdateRequestDto.getCategory())
                .account(account)
                .build();

        when(productService.updateProduct(eq(1L),eq(1L), any(ProductUpdateRequestDto.class)))
                .thenReturn(updatedProduct);

        // when & then
        mockMvc.perform(patch("/product/{productId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateRequestDto))
                        .param("accountId", "1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("상품 수정 완료"))
                        .andExpect(jsonPath("$.code").value("OK"))
                        .andExpect(jsonPath("$.data.name").value(productUpdateRequestDto.getName()))
                        .andExpect(jsonPath("$.data.price").value(productUpdateRequestDto.getPrice()))
                        .andExpect(jsonPath("$.data.description").value(productUpdateRequestDto.getDescription()))
                        .andExpect(jsonPath("$.data.stock").value(productUpdateRequestDto.getStock()))
                        .andExpect(jsonPath("$.data.category").value(productUpdateRequestDto.getCategory().name()));

        verify(productService, times(1)).updateProduct(eq(1L), eq(1L), any(productUpdateRequestDto.getClass()));

    }

    @Test
    @DisplayName("상품 삭제 성공(컨트롤러)")
    void deleteProduct_Success_Controller() throws Exception {
        // given
        doNothing().when(productService).deleteProduct(eq(1L), eq(1L));

        // when & then
        mockMvc.perform(delete("/product/{productId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("accountId", "1"))
                .andExpect(status().isNoContent());
        verify(productService, times(1)).deleteProduct(eq(1L), eq(1L));

    }

    @Test
    @DisplayName("상품 전체 조회 성공(컨트롤러)")
    void findAllProducts_Success_Controller() throws Exception {
        // given
        Product product = Product.builder()
                .id(1L)
                .name("test 상품")
                .price(1000)
                .description("test 상품 입니다")
                .stock(100)
                .category(Category.FASHION)
                .account(Account.builder().id(1L).build())
                .build();

        List<Product> productList = List.of(product);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> result = new PageImpl<>(productList, pageable, 1);

        when(productService.findAllProducts(isNull(), any(Pageable.class)))
                .thenReturn(result);
        // when & then
        mockMvc.perform(get("/product")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 전체 조회 완료"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.content[0].name").value(product.getName()))
                .andExpect(jsonPath("$.data.content[0].price").value(product.getPrice()))
                .andExpect(jsonPath("$.data.content[0].category").value(product.getCategory().name()))
                .andExpect(jsonPath("$.data.content[0].sellerName").value(product.getAccount().getName()));
    }

    @Test
    @DisplayName("상품 카테고리 조회 성공(컨트롤러)")
    void findProductsByCategory_Success_Controller() throws Exception {
        // given
        Product product = Product.builder()
                .id(1L)
                .name("test 상품")
                .price(1000)
                .description("test 상품 입니다")
                .stock(100)
                .category(Category.FASHION)
                .account(Account.builder().id(1L).build())
                .build();

        List<Product> productList = List.of(product);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> result = new PageImpl<>(productList, pageable, 1);

        when(productService.findAllProducts(eq("FASHION"), any(Pageable.class)))
                .thenReturn(result);
        // when & then
        mockMvc.perform(get("/product")
                        .param("category", "FASHION")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 전체 조회 완료"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.content[0].name").value(product.getName()))
                .andExpect(jsonPath("$.data.content[0].price").value(product.getPrice()))
                .andExpect(jsonPath("$.data.content[0].category").value(product.getCategory().name()))
                .andExpect(jsonPath("$.data.content[0].sellerName").value(product.getAccount().getName()));


    }

    @Test
    @DisplayName("상품 특정 상품 조회 성공(컨트롤러)")
    void findDetailProduct_Success_Controller() throws Exception {
        // given
        Product product = Product.builder()
                .id(1L)
                .name("test 상품")
                .price(1000)
                .description("test 상품 입니다")
                .stock(100)
                .category(Category.FASHION)
                .account(Account.builder().id(1L).build())
                .build();

        when(productService.findDetailProducts(eq(1L)))
                .thenReturn(product);
        // when & then
        mockMvc.perform(get("/product/{productId}",1)
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 상세 조회 완료"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.name").value(product.getName()))
                .andExpect(jsonPath("$.data.price").value(product.getPrice()))
                .andExpect(jsonPath("$.data.category").value(product.getCategory().name()))
                .andExpect(jsonPath("$.data.sellerName").value(product.getAccount().getName()));
    }
}
