package com.market.marketplacebackend.product.service;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.common.service.ImageService;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.dto.ProductCreateRequestDto;
import com.market.marketplacebackend.product.dto.ProductUpdateRequestDto;
import com.market.marketplacebackend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final ImageService imageService;

    @Transactional
    public Product createProduct(Long accountId, ProductCreateRequestDto productCreateRequestDto, MultipartFile image) throws IOException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        verifyIsSeller(account);
        String imageUrl = imageService.uploadImage(image);
        Product product = productCreateRequestDto.toEntity(account, imageUrl);

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long accountId, Long productId, ProductUpdateRequestDto productUpdateRequestDto, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        checkProductOwnership(product, accountId);
        String imageUrl = "";
        if(image != null){
            imageService.deleteImage(product.getImageUrl());
            imageUrl = imageService.uploadImage(image);
        }
        product.updateIfChanged(productUpdateRequestDto, imageUrl);

        return product;
    }

    @Transactional
    public void deleteProduct(Long accountId, Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        checkProductOwnership(product, accountId);

        imageService.deleteImage(product.getImageUrl());
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Page<Product> findAllProducts(String categoryInput, Pageable pageable) {
        if (categoryInput == null || categoryInput.isBlank()) {
            return productRepository.findAllFetchJoin(pageable);
        } else{
            Category category = Category.fromName(categoryInput)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            return productRepository.findAllByCategory(category, pageable);
        }
    }

    @Transactional(readOnly = true)
    public Product findDetailProducts(Long productId) {
        return productRepository.findByIdWithAccount(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private void checkProductOwnership(Product product, Long accountId) {
        if (!Objects.equals(product.getAccount().getId(), accountId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_PRODUCT);
        }
    }

    private void verifyIsSeller(Account account) {
        if (account.getAccountRole() != AccountRole.SELLER) {
            throw new BusinessException(ErrorCode.FORBIDDEN_NOT_SELLER);
        }
    }
}
