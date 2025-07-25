package com.market.marketplacebackend.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    ELECTRONICS("전자기기"),
    BOOKS("도서"),
    FASHION("패션의류"),
    BEAUTY("뷰티"),
    HOME_GOODS("생활용품"),
    SPORTS("스포츠/레저"),
    FOOD("식품");

    private final String categoryName;

}