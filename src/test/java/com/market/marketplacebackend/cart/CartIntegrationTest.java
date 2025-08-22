package com.market.marketplacebackend.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.cart.dto.CartItemAddRequestDto;
import com.market.marketplacebackend.cart.dto.CartItemUpdateDto;
import com.market.marketplacebackend.cart.repository.CartItemRepository;
import com.market.marketplacebackend.cart.repository.CartRepository;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static com.market.marketplacebackend.TestDataFactory.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jwt.secret=ksf92jf12jf23jdfh4skdlf2398rjskfjweofjr9203sldf9230jsdf023r"
})
@WithMockUser(roles = "BUYER")
class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("장바구니 조회 통합 성공 테스트")
    void getCart_Integration_Success() throws Exception {
        // given
        Account account = createAccount();
        accountRepository.save(account);
        Product product = createProduct( account);
        productRepository.save(product);
        Cart cart = createCart(account);
        CartItem cartItem = createCartItem(product,cart,10);
        cart.addProduct(product, 10);
        cartRepository.save(cart);
        //then
        mockMvc.perform(get("/cart/{accountId}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("카트 조회 완료"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.items[0].productId").value(product.getId()))
                .andExpect(jsonPath("$.data.items[0].productName").value(product.getName()))
                .andExpect(jsonPath("$.data.items[0].productPrice").value(product.getPrice()))
                .andExpect(jsonPath("$.data.items[0].quantity").value(cartItem.getQuantity()))
                .andExpect(jsonPath("$.data.items[0].totalPricePerItem").value(cartItem.getProduct().getPrice() * cartItem.getQuantity()))
                .andExpect(jsonPath("$.data.totalPrice").value(cartItem.getProduct().getPrice() * cartItem.getQuantity()));
    }

    @Test
    @DisplayName("장바구니 상품 추가 통합 성공 테스트")
    void addItemToCart_Integration_Success() throws Exception {
        // given
        Account account = createAccount();
        accountRepository.save(account);
        Product product = createProduct( account);
        productRepository.save(product);
        Cart cart = createCart(account);
        cartRepository.save(cart);
        CartItemAddRequestDto cartItemAddRequestDto = createCartItemAddRequestDto(product.getId(), 5);
        // then
        mockMvc.perform(post("/cart/items/{accountId}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
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
    @DisplayName("장바구니 상품 추가 통합 실패 테스트(상품 없음)")
    void addItemToCart_ProductNotFound_Integration_Fail() throws Exception {
        // given
        Account account = createAccount();
        accountRepository.save(account);
        cartRepository.save(createCart(account));

        CartItemAddRequestDto cartItemAddRequestDto = CartItemAddRequestDto.builder()
                .quantity(10)
                .build();
        // when & then
        mockMvc.perform(post("/cart/items/{accountId}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemAddRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 상품 추가 통합 실패 테스트(수량 0)")
    void addItemToCart_ZeroQuantity_Integration_Fail() throws Exception {
        // given
        Account account = createAccount();
        accountRepository.save(account);
        Product product = createProduct( account);
        productRepository.save(product);
        CartItemAddRequestDto cartItemAddRequestDto = createCartItemAddRequestDto(product.getId(), 0);

        // when & then
        mockMvc.perform(post("/cart/items/{accountId}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemAddRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 상품 수정 통합 성공 테스트")
    void updateItem_Integration_Success() throws Exception {
        // given
        Account account = createAccount();
        accountRepository.save(account);
        Product product = createProduct( account);
        productRepository.save(product);
        Cart cart = createCart(account);
        CartItem cartItem = cart.addProduct(product, 10);
        cartRepository.save(cart);
        CartItemUpdateDto cartItemUpdateDto = createCartItemUpdateDto(cartItem.getId(), 5);

        // then
        mockMvc.perform(patch("/cart/items/{accountId}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
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
    @DisplayName("장바구니 상품 수정 통합 실패 테스트(상품 없음)")
    void updateItem_ProductNotFound_Integration_Fail() throws Exception {
        // given
        Account account = createAccount();
        accountRepository.save(account);
        CartItemUpdateDto cartItemUpdateDto = CartItemUpdateDto.builder()
                .quantity(5)
                .build();
        // when & then
        mockMvc.perform(patch("/cart/items/{accountId}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 상품 삭제 통합 성공 테스트")
    void deleteItem_Integration_Success() throws Exception {
        // given
        Account account = createAccount();
        accountRepository.save(account);
        Product product = createProduct( account);
        productRepository.save(product);
        Cart cart = createCart(account);
        CartItem cartItem = cart.addProduct(product,10);
        cartRepository.save(cart);
        // then
        mockMvc.perform(delete("/cart/items/{accountId}/{cartItemId}", account.getId(),cartItem.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        Optional<CartItem> deletedItem = cartItemRepository.findById(cartItem.getId());
        assertThat(deletedItem).isEmpty();
    }

    @Test
    @DisplayName("장바구니 상품 전체 삭제 통합 성공 테스트")
    void deleteAllCartItems_Integration_Success() throws Exception {
        // given
        Account account = createAccount();
        accountRepository.save(account);
        Product product = createProduct( account);
        productRepository.save(product);
        Cart cart = createCart(account);
        CartItem cartItem = cart.addProduct(product,10);
        cartRepository.save(cart);
        // then
        mockMvc.perform(delete("/cart/items/{accountId}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        Optional<CartItem> deletedItem = cartItemRepository.findById(cartItem.getId());
        assertThat(deletedItem).isEmpty();
    }
}