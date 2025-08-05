package com.market.marketplacebackend.product;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.dto.ProductCreateRequestDto;
import com.market.marketplacebackend.product.dto.ProductUpdateRequestDto;
import com.market.marketplacebackend.product.repository.ProductRepository;
import com.market.marketplacebackend.product.service.ProductService;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 등록 성공(서비스)")
    void createProduct_Success_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.SELLER)
                .build();
        ProductCreateRequestDto productCreateRequestDto = ProductCreateRequestDto.builder()
                .name("test Product")
                .price(10000)
                .description("맛있는 사과입니다")
                .stock(50)
                .category(Category.FOOD)
                .build();
        Product product = productCreateRequestDto.toEntity(account);

        when(accountRepository.findById(eq(1L))).thenReturn(Optional.of(account));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Product result = productService.createProduct(account.getId(), productCreateRequestDto);

        // then
        verify(accountRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
        assertThat(result.getName()).isEqualTo(productCreateRequestDto.getName());
        assertThat(result.getPrice()).isEqualTo(productCreateRequestDto.getPrice());
        assertThat(result.getDescription()).isEqualTo(productCreateRequestDto.getDescription());
        assertThat(result.getStock()).isEqualTo(productCreateRequestDto.getStock());
        assertThat(result.getCategory()).isEqualTo(productCreateRequestDto.getCategory());
    }

    @Test
    @DisplayName("상품 등록 실패(서비스) - ACCOUNT_NOT_FOUND")
    void createProduct_AccountNotFound_Fail() {
            // given
            ProductCreateRequestDto productCreateRequestDto = ProductCreateRequestDto.builder()
                    .name("test Product")
                    .price(10000)
                    .description("맛있는 사과입니다")
                    .stock(50)
                    .category(Category.FOOD)
                    .build();
            when(accountRepository.findById(eq(1L))).thenReturn(Optional.empty());
            // when
            BusinessException exception = assertThrows(BusinessException.class, () -> productService.createProduct(1L, productCreateRequestDto));

            // then
            verify(accountRepository).findById(1L);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("상품 등록 실패(서비스) - FORBIDDEN_NOT_SELLER")
    void createProduct_ForbiddenNotSeller_Fail() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.CUSTOMER)
                .build();
        ProductCreateRequestDto productCreateRequestDto = ProductCreateRequestDto.builder()
                .name("test Product")
                .price(10000)
                .description("맛있는 사과입니다")
                .stock(50)
                .category(Category.FOOD)
                .build();
        when(accountRepository.findById(eq(1L))).thenReturn(Optional.of(account));
        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> productService.createProduct(1L, productCreateRequestDto));

        // then
        verify(accountRepository).findById(1L);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_NOT_SELLER);
    }

    @Test
    @DisplayName("상품 수정 성공(서비스)")
    void updateProduct_Success_Service() {
        // given
        Account account = Account.builder()
                .id(1L)
                .name("test")
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
        Product originalProduct = Product.builder()
                .id(1L)
                .name("old Product")
                .price(10000)
                .description("옛날 상품입니다")
                .stock(50)
                .category(Category.BOOKS)
                .account(account)
                .build();

        when(productRepository.findById(eq(1L))).thenReturn(Optional.of(originalProduct));

        // when
        Product result = productService.updateProduct(account.getId(), originalProduct.getId(), productUpdateRequestDto);

        // then
        verify(productRepository).findById(1L);
        assertThat(result.getName()).isEqualTo(productUpdateRequestDto.getName());
        assertThat(result.getPrice()).isEqualTo(productUpdateRequestDto.getPrice());
        assertThat(result.getDescription()).isEqualTo(productUpdateRequestDto.getDescription());
        assertThat(result.getStock()).isEqualTo(productUpdateRequestDto.getStock());
        assertThat(result.getCategory()).isEqualTo(productUpdateRequestDto.getCategory());
    }

    @Test
    @DisplayName("상품 수정 실패(서비스) - PRODUCT_NOT_FOUND")
    void updateProduct_ProductNotFound_Fail() {
        // given
        ProductUpdateRequestDto productUpdateRequestDto = ProductUpdateRequestDto.builder()
                .name("new Product")
                .price(20000)
                .description("신 상품입니다")
                .stock(100)
                .category(Category.FASHION)
                .build();
        when(productRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> productService.updateProduct(1L, 1L, productUpdateRequestDto));

        // then
        verify(productRepository).findById(1L);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("상품 수정 실패(서비스) - FORBIDDEN_PRODUCT")
    void updateProduct_ForbiddenProduct_Fail() {
        // given
        Account ownerAccount = Account.builder().id(2L).build();
        Product otherOwnersProduct = Product.builder().id(1L).account(ownerAccount).build();

        when(productRepository.findById(eq(1L))).thenReturn(Optional.of(otherOwnersProduct));

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.updateProduct(1L, 1L, new ProductUpdateRequestDto()));

        // then
        verify(productRepository).findById(1L);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_PRODUCT);
    }

    @Test
    @DisplayName("상품 삭제 성공(서비스)")
    void deleteProduct_Success_Service() {
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
        when(productRepository.findById(eq(1L))).thenReturn(Optional.of(product));
        // when
        productService.deleteProduct(1L, 1L);
        // then
        verify(productRepository).findById(1L);
        verify(productRepository).delete(any(Product.class));
    }

    @Test
    @DisplayName("상품 삭제 실패(서비스) - PRODUCT_NOT_FOUND")
    void deleteProduct_ProductNotFound_Fail() {
        // given
        when(productRepository.findById(eq(1L))).thenReturn(Optional.empty());
        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> productService.deleteProduct(1L, 1L));
        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("상품 삭제 실패(서비스) - FORBIDDEN_PRODUCT")
    void deleteProduct_ForbiddenProduct_Fail() {
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
        when(productRepository.findById(eq(1L))).thenReturn(Optional.of(product));
        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> productService.deleteProduct(2L, 1L));
        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_PRODUCT);
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("전체 상품 조회 성공(서비스)")
    void findAllProducts_Success_Service() {
        //given
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

        when(productRepository.findAllFetchJoin(pageable)).thenReturn(result);
        // when
        Page<Product> products = productService.findAllProducts(null, pageable);
        // then
        verify(productRepository).findAllFetchJoin(pageable);
        assertThat(products.getContent().get(0).getName()).isEqualTo(product.getName());
        assertThat(products.getContent().get(0).getPrice()).isEqualTo(product.getPrice());
        assertThat(products.getContent().get(0).getDescription()).isEqualTo(product.getDescription());
        assertThat(products.getContent().get(0).getStock()).isEqualTo(product.getStock());
        assertThat(products.getContent().get(0).getCategory().getCategoryName()).isEqualTo(product.getCategory().getCategoryName());
    }

    @Test
    @DisplayName("특정 상품 조회 성공(서비스)")
    void findDetailProduct_Success_Service() {
        //given
        Product product = Product.builder()
                .id(1L)
                .name("test 상품")
                .price(1000)
                .description("test 상품 입니다")
                .stock(100)
                .category(Category.FASHION)
                .account(Account.builder().id(1L).build())
                .build();

        when(productRepository.findByIdWithAccount(eq(1L))).thenReturn(Optional.of(product));
        // when
        Product result = productService.findDetailProducts(1L);
        // then
        verify(productRepository).findByIdWithAccount(1L);
        assertThat(result.getName()).isEqualTo(product.getName());
        assertThat(result.getPrice()).isEqualTo(product.getPrice());
        assertThat(result.getDescription()).isEqualTo(product.getDescription());
        assertThat(result.getStock()).isEqualTo(product.getStock());
        assertThat(result.getCategory().getCategoryName()).isEqualTo(product.getCategory().getCategoryName());
    }

    @Test
    @DisplayName("특정 상품 조회 실패(서비스) - PRODUCT_NOT_FOUND")
    void findDetailProduct_ProductNotFound_Fail() {
        //given
        when(productRepository.findByIdWithAccount(eq(1L))).thenReturn(Optional.empty());
        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> productService.findDetailProducts(1L));
        // then
        verify(productRepository).findByIdWithAccount(1L);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND );
    }

    @Test
    @DisplayName("카테고리 상품 조회 성공(서비스)")
    void findProductsByCategory_Success_Service() {
        //given
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

        when(productRepository.findAllByCategory(Category.FASHION, pageable)).thenReturn(result);
        // when
        Page<Product> products = productService.findAllProducts("FASHION", pageable);
        // then
        verify(productRepository).findAllByCategory(Category.FASHION, pageable);
        assertThat(products.getContent().get(0).getName()).isEqualTo(product.getName());
        assertThat(products.getContent().get(0).getPrice()).isEqualTo(product.getPrice());
        assertThat(products.getContent().get(0).getDescription()).isEqualTo(product.getDescription());
        assertThat(products.getContent().get(0).getStock()).isEqualTo(product.getStock());
        assertThat(products.getContent().get(0).getCategory().getCategoryName()).isEqualTo(product.getCategory().getCategoryName());
    }

    @Test
    @DisplayName("카테고리 상품 조회 실패(서비스) - CATEGORY_NOT_FOUND")
    void findProductsByCategory_CategoryNotFound_Fail() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> productService.findAllProducts("WRONG_CATEGORY", pageable));
        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND );
    }
}