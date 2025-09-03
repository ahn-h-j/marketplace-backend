package com.market.marketplacebackend.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.TestDataFactory;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.config.TestConfig;
import com.market.marketplacebackend.product.controller.ProductController;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.dto.ProductCreateRequestDto;
import com.market.marketplacebackend.product.dto.ProductUpdateRequestDto;
import com.market.marketplacebackend.product.service.ProductService;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(TestConfig.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    private Account testAccount;
    private Authentication authToken;

    @BeforeEach
    void setUp() {
        testAccount = TestDataFactory.createAccount();

        PrincipalDetails principalDetails = new PrincipalDetails(testAccount);
        authToken = new UsernamePasswordAuthenticationToken(
                principalDetails, null, principalDetails.getAuthorities());
    }

    @Test
    @DisplayName("상품 등록 성공(컨트롤러)")
    void createProduct_Success_Controller() throws Exception {
        // given
        ProductCreateRequestDto createDto = new ProductCreateRequestDto(
                "test product",
                10000,
                "this is test product",
                100,
                Category.FASHION);

        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes());

        String jsonDto = objectMapper.writeValueAsString(createDto);
        MockMultipartFile requestDtoPart = new MockMultipartFile(
                "requestDto",
                "",
                "application/json",
                jsonDto.getBytes(StandardCharsets.UTF_8));

        Account account = TestDataFactory.createAccount(1L);
        PrincipalDetails principalDetails = new PrincipalDetails(account);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                principalDetails, null, principalDetails.getAuthorities());

        Product product = TestDataFactory.createProduct(1L, account);

        when(productService.createProduct(anyLong(), any(ProductCreateRequestDto.class), any(MultipartFile.class))).thenReturn(product);

        // when & then
        mockMvc.perform(multipart("/product")
                        .file(imageFile)
                        .file(requestDtoPart)
                        .with(authentication(authToken))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 등록 완료"))
                .andExpect(jsonPath("$.data.name").value(createDto.getName()))
                .andExpect(jsonPath("$.data.price").value(createDto.getPrice()));

        verify(productService, times(1)).createProduct(anyLong(), any(ProductCreateRequestDto.class), any(MultipartFile.class));
    }

    @Test
    @DisplayName("상품 등록 실패(컨트롤러) - Validation")
    void createProduct_Failure_Controller_Validation() throws Exception {
        // given
        ProductCreateRequestDto createDto = new ProductCreateRequestDto(
                null,
                10000,
                "맛있는 사과입니다",
                50,
                Category.FOOD);

        String jsonDto = objectMapper.writeValueAsString(createDto);
        MockMultipartFile requestDtoPart = new MockMultipartFile(
                "requestDto",
                "",
                "application/json",
                jsonDto.getBytes(StandardCharsets.UTF_8));

        // when & then
        mockMvc.perform(multipart("/product")
                        .file(requestDtoPart)
                        .with(authentication(authToken))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 수정 성공(컨트롤러)")
    void updateProduct_Success_Controller() throws Exception {
        // given
        Long productId = 1L;
        ProductUpdateRequestDto updateDto = new ProductUpdateRequestDto("new Product", 20000, "신 상품입니다", 100, Category.FASHION);
        Product updatedProduct = Product.builder()
                .id(productId)
                .name(updateDto.getName())
                .price(updateDto.getPrice())
                .description(updateDto.getDescription())
                .stock(updateDto.getStock())
                .category(updateDto.getCategory())
                .account(testAccount)
                .build();

        String jsonDto = objectMapper.writeValueAsString(updateDto);
        MockMultipartFile requestDtoPart = new MockMultipartFile(
                "requestDto",
                "",
                "application/json",
                jsonDto.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );
        when(productService.updateProduct(eq(testAccount.getId()), eq(productId), any(ProductUpdateRequestDto.class), any(MultipartFile.class)))
                .thenReturn(updatedProduct);

        // when & then
        mockMvc.perform(multipart(HttpMethod.PATCH,"/product/update/{productId}", productId)
                        .file(requestDtoPart)
                        .file(imagePart)
                        .with(authentication(authToken))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 수정 완료"))
                .andExpect(jsonPath("$.data.name").value(updateDto.getName()))
                .andExpect(jsonPath("$.data.price").value(updateDto.getPrice()));

        verify(productService, times(1)).updateProduct(eq(testAccount.getId()), eq(productId), any(ProductUpdateRequestDto.class),  any(MultipartFile.class));
    }

    @Test
    @DisplayName("상품 삭제 성공(컨트롤러)")
    void deleteProduct_Success_Controller() throws Exception {
        // given
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(eq(testAccount.getId()), eq(productId));

        // when & then
        mockMvc.perform(delete("/product/{productId}", productId)
                        .with(authentication(authToken))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(eq(testAccount.getId()), eq(productId));
    }

    @Test
    @DisplayName("상품 전체 조회 성공(컨트롤러)")
    void findAllProducts_Success_Controller() throws Exception {
        // given
        Product product = TestDataFactory.createProduct(testAccount);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productService.findAllProducts(isNull(), any(Pageable.class))).thenReturn(productPage);

        // when & then
        mockMvc.perform(get("/product")
                        .param("page", "0")
                        .param("size", "10")
                        .with(authentication(authToken))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 전체 조회 완료"))
                .andExpect(jsonPath("$.data.content[0].name").value(product.getName()))
                .andExpect(jsonPath("$.data.content[0].sellerName").value(testAccount.getName()));
    }

    @Test
    @DisplayName("상품 카테고리 조회 성공(컨트롤러)")
    void findProductsByCategory_Success_Controller() throws Exception {
        // given
        Product product = TestDataFactory.createProduct(testAccount);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        String category = "FASHION";

        when(productService.findAllProducts(eq(category), any(Pageable.class))).thenReturn(productPage);

        // when & then
        mockMvc.perform(get("/product")
                        .param("category", category)
                        .param("page", "0")
                        .param("size", "10")
                        .with(authentication(authToken))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].category").value(category));
    }

    @Test
    @DisplayName("상품 상세 조회 성공(컨트롤러)")
    void findDetailProduct_Success_Controller() throws Exception {
        // given
        Long productId = 1L;
        Product product = TestDataFactory.createProduct(testAccount);
        when(productService.findDetailProducts(eq(productId))).thenReturn(product);

        // when & then
        mockMvc.perform(get("/product/{productId}", productId)
                        .with(authentication(authToken))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 상세 조회 완료"))
                .andExpect(jsonPath("$.data.name").value(product.getName()))
                .andExpect(jsonPath("$.data.sellerName").value(testAccount.getName()));
    }
}