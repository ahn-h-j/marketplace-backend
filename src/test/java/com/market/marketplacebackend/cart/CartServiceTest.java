package com.market.marketplacebackend.cart;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.cart.dto.CartItemAddRequestDto;
import com.market.marketplacebackend.cart.dto.CartItemUpdateDto;
import com.market.marketplacebackend.cart.repository.CartItemRepository;
import com.market.marketplacebackend.cart.repository.CartRepository;
import com.market.marketplacebackend.cart.service.CartService;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    private final long NON_EQUAL_ACCOUNT_ID = 999L;
    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니 생성 성공 - 서비스")
    void createCart_Success_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();
        Cart cart = Cart.builder()
                .account(account)
                .build();

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        // when
        cartService.createCartForAccount(account);
        // then
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        verify(cartRepository).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getValue();

        assertThat(savedCart.getAccount()).isNotNull();
        assertThat(savedCart.getAccount().getId()).isEqualTo(account.getId());
        assertThat(savedCart.getAccount().getName()).isEqualTo(account.getName());

    }

    @Test
    @DisplayName("장바구니 조회 성공 - 서비스")
    void getCart_Success_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();
        Cart cart = Cart.builder()
                .account(account)
                .build();

        when(cartRepository.findByAccountId(eq(1L))).thenReturn(Optional.ofNullable(cart));
        // when
        cartService.getCart(account.getId());

        // then
        verify(cartRepository).findByAccountId(1L);
    }

    @Test
    @DisplayName("장바구니 조회 실패(CART_NOT_FOUND) - 서비스")
    void getCart_CartNotFound_Fail_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();

        when(cartRepository.findByAccountId(eq(1L))).thenReturn(Optional.empty());
        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> cartService.getCart(account.getId()));

        // then
        verify(cartRepository).findByAccountId(1L);
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CART_NOT_FOUND);
    }

    @Test
    @DisplayName("장바구니 상품 추가 성공 - 서비스")
    void addNewItemToCart_Success_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();
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

        when(cartRepository.findByAccountId(eq(1L))).thenReturn(Optional.of(cart));
        when(productRepository.findById(eq(1L))).thenReturn(Optional.of(product));

        // when
        cartService.addItemToCart(account.getId(), cartItemAddRequestDto);

        // then
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("장바구니 상품 추가 성공(수량 증가) - 서비스")
    void addItemToCart_Success_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();
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

        cart.addProduct(product, 10);
        when(cartRepository.findByAccountId(eq(1L))).thenReturn(Optional.of(cart));
        when(productRepository.findById(eq(1L))).thenReturn(Optional.of(product));

        // when
        CartItem addItemToCart = cartService.addItemToCart(account.getId(), cartItemAddRequestDto);

        // then
        verify(cartRepository).findByAccountId(1L);
        verify(productRepository).findById(1L);
        assertThat(addItemToCart.getQuantity()).isEqualTo(20);
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패(CART_NOT_FOUND) - 서비스")
    void addItemToCart_CartNotFound_Fail_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();
        Product product = Product.builder()
                .id(1L)
                .name("test product")
                .price(10000)
                .description("this is test product")
                .stock(100)
                .category(Category.FASHION)
                .account(account)
                .build();
        CartItemAddRequestDto cartItemAddRequestDto = CartItemAddRequestDto.builder()
                .productId(product.getId())
                .quantity(10)
                .build();

        when(cartRepository.findByAccountId(eq(1L))).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> cartService.addItemToCart(account.getId(),cartItemAddRequestDto));

        // then
        verify(cartRepository).findByAccountId(1L);
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CART_NOT_FOUND);
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패(PRODUCT_NOT_FOUND) - 서비스")
    void addItemToCart_ProductNotFound_Fail_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();
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

        when(cartRepository.findByAccountId(eq(1L))).thenReturn(Optional.of(cart));
        when(productRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> cartService.addItemToCart(account.getId(),cartItemAddRequestDto));

        // then
        verify(cartRepository).findByAccountId(1L);
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

    }

    @Test
    @DisplayName("장바구니 상품 수정 성공 - 서비스")
    void updateItem_Success_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();

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

        CartItem originalCartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .cart(cart)
                .quantity(10)
                .build();

        CartItemUpdateDto cartItemUpdateDto = CartItemUpdateDto.builder()
                .cartItemId(originalCartItem.getId())
                .quantity(5)
                .build();

        when(cartItemRepository.findById(eq(1L))).thenReturn(Optional.of(originalCartItem));

        // when
        CartItem updatedItem = cartService.updateItem(account.getId(), cartItemUpdateDto);

        // then
        verify(cartItemRepository).findById(originalCartItem.getId());
        assertThat(updatedItem.getId()).isEqualTo(originalCartItem.getId());
        assertThat(updatedItem.getQuantity()).isEqualTo(5);

    }

    @Test
    @DisplayName("장바구니 상품 수정 실패(PRODUCT_NOT_IN_CART) - 서비스")
    void updateItem_ProductNotInCart_Fail_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();

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

        CartItem originalCartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .cart(cart)
                .quantity(10)
                .build();

        CartItemUpdateDto cartItemUpdateDto = CartItemUpdateDto.builder()
                .cartItemId(originalCartItem.getId())
                .quantity(5)
                .build();

        when(cartItemRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> cartService.updateItem(account.getId(),cartItemUpdateDto));

        // then
        verify(cartItemRepository).findById(1L);
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_IN_CART);

    }

    @Test
    @DisplayName("장바구니 상품 수정 실패(FORBIDDEN_CART) - 서비스")
    void updateItem_ForbiddenCart_Fail_Service() {
        // given
        Account account = Account.builder()
                .id(NON_EQUAL_ACCOUNT_ID)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();

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

        CartItem originalCartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .cart(cart)
                .quantity(10)
                .build();

        CartItemUpdateDto cartItemUpdateDto = CartItemUpdateDto.builder()
                .cartItemId(originalCartItem.getId())
                .quantity(5)
                .build();

        when(cartItemRepository.findById(eq(1L))).thenReturn(Optional.of(originalCartItem));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> cartService.updateItem(1L,cartItemUpdateDto));

        // then
        verify(cartItemRepository).findById(1L);
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_CART);
    }

    @Test
    @DisplayName("장바구니 상품 삭제 성공 - 서비스")
    void deleteItem_Success_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();

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

        when(cartItemRepository.findById(eq(1L))).thenReturn(Optional.of(cartItem));

        // when
        cartService.deleteItem(account.getId(), cartItem.getId());

        // then
        verify(cartItemRepository).findById(cartItem.getId());

    }

    @Test
    @DisplayName("장바구니 상품 삭제 실패(PRODUCT_NOT_IN_CART) - 서비스")
    void deleteItem_ProductNotInCart_Fail_Service() {

        when(cartItemRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> cartService.deleteItem(1L,1L));

        // then
        verify(cartItemRepository).findById(1L);
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_IN_CART);
    }

    @Test
    @DisplayName("장바구니 상품 삭제 실패(FORBIDDEN_CART) - 서비스")
    void deleteItem_ForbiddenCart_Fail_Service() {
        // given
        Account account = Account.builder()
                .id(NON_EQUAL_ACCOUNT_ID)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();

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

        CartItem originalCartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .cart(cart)
                .quantity(10)
                .build();

        when(cartItemRepository.findById(eq(1L))).thenReturn(Optional.of(originalCartItem));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> cartService.deleteItem(1L,1L));

        // then
        verify(cartItemRepository).findById(1L);
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_CART);
        assertThat(cart.getCartItems()).isEmpty();

    }

    @Test
    @DisplayName("장바구니 상품 전체 삭제 성공 - 서비스")
    void deleteAllCartItems_Success_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();

        Cart cart = Cart.builder()
                .account(account)
                .build();

        when(cartRepository.findByAccountId(eq(1L))).thenReturn(Optional.of(cart));

        // when
        cartService.deleteAllCartItems(account.getId());

        // then
        verify(cartRepository).findByAccountId(account.getId());
        assertThat(cart.getCartItems()).isEmpty();

    }

    @Test
    @DisplayName("장바구니 상품 전체 삭제 실패(CART_NOT_FOUND) - 서비스")
    void deleteAllCartItems_CartNotFound_Fail_Service() {
        // given
        when(cartRepository.findByAccountId(eq(1L))).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> cartService.deleteAllCartItems(1L));

        // then
        verify(cartRepository).findByAccountId(1L);
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CART_NOT_FOUND);

    }
}
