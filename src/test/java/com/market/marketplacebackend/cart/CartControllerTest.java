package com.market.marketplacebackend.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.cart.controller.CartController;
import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.cart.dto.CartItemAddRequestDto;
import com.market.marketplacebackend.cart.dto.CartItemUpdateDto;
import com.market.marketplacebackend.cart.service.CartService;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.config.TestConfig;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import(TestConfig.class)
public class CartControllerTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("장바구니 조회 성공 - 컨트롤러")
    void getCart_Success_Controller() throws Exception {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
        PrincipalDetails customUserDetails = new PrincipalDetails(account);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        Product product = Product.builder()
                .id(1L)
                .name("test product")
                .price(10000)
                .description("this is test product")
                .stock(100)
                .category(Category.FASHION)
                .account(account)
                .build();

        Cart cart = Cart.builder()
                .account(account)
                .build();

        cart.addProduct(product, 10);

        //when
        // 주입받은 cartService Mock 객체를 사용하여 동작을 정의합니다.
        when(cartService.getCart(eq(1L))).thenReturn(cart);

        //then
        mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("카트 조회 완료"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.items[0].productId").value(product.getId()))
                .andExpect(jsonPath("$.data.items[0].productName").value(product.getName()))
                .andExpect(jsonPath("$.data.items[0].productPrice").value(product.getPrice()))
                .andExpect(jsonPath("$.data.items[0].quantity").value(10))
                .andExpect(jsonPath("$.data.items[0].totalPricePerItem").value(product.getPrice() * 10))
                .andExpect(jsonPath("$.data.totalPrice").value(product.getPrice() * 10));
    }

    @Test
    @DisplayName("장바구니 상품 추가 성공 - 컨트롤러")
    void addItemToCart_Success_Controller() throws Exception {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
        PrincipalDetails customUserDetails = new PrincipalDetails(account);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        Product product = Product.builder()
                .id(1L)
                .name("test product")
                .price(10000)
                .description("this is test product")
                .stock(100)
                .category(Category.FASHION)
                .account(account)
                .build();

        Cart cart = Cart.builder()
                .account(account)
                .build();

        CartItemAddRequestDto cartItemAddRequestDto = CartItemAddRequestDto.builder()
                .productId(product.getId())
                .quantity(10)
                .build();

        CartItem cartItem = CartItem.builder()
                .product(product)
                .cart(cart)
                .quantity(cartItemAddRequestDto.getQuantity())
                .build();

        // when
        when(cartService.addItemToCart(eq(1L), any(CartItemAddRequestDto.class))).thenReturn(cartItem);

        // then
        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authToken))
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(cartItemAddRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("장바구니에 상품 추가 완료"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.productId").value(product.getId()))
                .andExpect(jsonPath("$.data.productName").value(product.getName()))
                .andExpect(jsonPath("$.data.productPrice").value(product.getPrice()))
                .andExpect(jsonPath("$.data.quantity").value(cartItemAddRequestDto.getQuantity()))
                .andExpect(jsonPath("$.data.totalPricePerItem").value(product.getPrice() * cartItemAddRequestDto.getQuantity()));
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패(상품 없음) - 컨트롤러")
    void addItemToCart_ProductNotFound_Fail_Controller() throws Exception {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
        PrincipalDetails customUserDetails = new PrincipalDetails(account);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        CartItemAddRequestDto cartItemAddRequestDto = CartItemAddRequestDto.builder()
                .quantity(10)
                .build();
        // when & then
        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authToken))
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(cartItemAddRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패(수량 0) - 컨트롤러")
    void addItemToCart_ZeroQuantity_Fail_Controller() throws Exception {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
        PrincipalDetails customUserDetails = new PrincipalDetails(account);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        CartItemAddRequestDto cartItemAddRequestDto = CartItemAddRequestDto.builder()
                .productId(1L)
                .quantity(0)
                .build();
        // when & then
        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authToken))
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(cartItemAddRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 상품 수정 성공 - 컨트롤러")
    void updateItem_Success_Controller() throws Exception {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
        PrincipalDetails customUserDetails = new PrincipalDetails(account);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        Product product = Product.builder()
                .id(1L)
                .name("test product")
                .price(10000)
                .description("this is test product")
                .stock(100)
                .category(Category.FASHION)
                .account(account)
                .build();

        Cart cart = Cart.builder()
                .account(account)
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .cart(cart)
                .quantity(10)
                .build();

        CartItemUpdateDto cartItemUpdateDto = CartItemUpdateDto.builder()
                .cartItemId(cartItem.getId())
                .quantity(9)
                .build();

       cartItem.updateQuantity(cartItemUpdateDto.getQuantity());

        // when
        when(cartService.updateItem(eq(1L), any(CartItemUpdateDto.class))).thenReturn(cartItem);

        // then
        mockMvc.perform(patch("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authToken))
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(cartItemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("장바구니에 상품 수정 완료"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.productId").value(product.getId()))
                .andExpect(jsonPath("$.data.productName").value(product.getName()))
                .andExpect(jsonPath("$.data.productPrice").value(product.getPrice()))
                .andExpect(jsonPath("$.data.quantity").value(cartItemUpdateDto.getQuantity()))
                .andExpect(jsonPath("$.data.totalPricePerItem").value(product.getPrice() * cartItemUpdateDto.getQuantity()));
    }

    @Test
    @DisplayName("장바구니 상품 수정 실패(상품 없음) - 컨트롤러")
    void updateItem_ProductNotFound_Fail_Controller() throws Exception {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
        PrincipalDetails customUserDetails = new PrincipalDetails(account);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        CartItemUpdateDto cartItemUpdateDto = CartItemUpdateDto.builder()
                .quantity(5)
                .build();
        // when & then
        mockMvc.perform(patch("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authToken))
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(cartItemUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 상품 삭제 성공 - 컨트롤러")
    void deleteItem_Success_Controller() throws Exception {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
        PrincipalDetails customUserDetails = new PrincipalDetails(account);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        // then
        mockMvc.perform(delete("/cart/items/{cartItemId}",1)
                        .with(authentication(authToken))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(cartService, times(1)).deleteItem(eq(1L), eq(1L));
    }

    @Test
    @DisplayName("장바구니 상품 전체 삭제 성공 - 컨트롤러")
    void deleteAllCartItems_Success_Controller() throws Exception {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
        PrincipalDetails customUserDetails = new PrincipalDetails(account);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        // then
        mockMvc.perform(delete("/cart/items")
                        .with(authentication(authToken))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(cartService, times(1)).deleteAllCartItems(eq(1L));

    }
}
