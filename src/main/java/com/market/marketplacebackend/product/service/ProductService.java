package com.market.marketplacebackend.product.service;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.dto.ProductCreateRequestDto;
import com.market.marketplacebackend.product.dto.ProductUpdateRequestDto;
import com.market.marketplacebackend.product.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(Long accountId, @Valid ProductCreateRequestDto productCreateRequestDto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if(!account.getAccountRole().equals(AccountRole.SELLER)){
            throw new BusinessException(ErrorCode.FORBIDDEN_NOT_SELLER);
        }

        Product product = productCreateRequestDto.toEntity(account);

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long accountId, Long productId, @Valid ProductUpdateRequestDto productUpdateRequestDto) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if(!Objects.equals(product.getAccount().getId(), accountId)){
            throw new BusinessException(ErrorCode.FORBIDDEN_PRODUCT);
        }

        product.updateIfChanged(productUpdateRequestDto);

        return product;
    }

    public void deleteProduct(Long accountId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if(!Objects.equals(product.getAccount().getId(), accountId)){
            throw new BusinessException(ErrorCode.FORBIDDEN_PRODUCT);
        }

        productRepository.delete(product);
    }

    public List<Product> findAllProducts() {

        return productRepository.findAll();
    }
}
