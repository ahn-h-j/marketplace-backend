# 쇼핑몰

현재 구현중인 개인 프로젝트이며 Spring Boot 기반의 쇼핑몰 플랫폼 프로젝트입니다.
회원가입/로그인, 상품 관리, 주문 처리, 장바구니 기능 등 쇼핑몰의 핵심 비즈니스 로직을 구현한 백엔드 프로젝트입니다

---

## ✏️ 기술 스택

### Tech
<img src="https://img.shields.io/badge/java-FC4C02?style=for-the-badge&logo=java&logoColor=white"><img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white"><img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white"><img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">

### Tool
<img src="https://img.shields.io/badge/Github-000000?style=for-the-badge&logo=Github&logoColor=white"/><img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white"/>

---

## 🖥️ 프로젝트 산출물

### ERD

![image](https://github.com/user-attachments/assets/c0ebbff3-8e58-4406-bd42-9f8e6bdb1966)



# API 명세서

## 인증 (Authentication)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 회원가입 | `POST` | `/user/signup` |
| 토큰 재발행 | `POST` | `/reissue` |

## 상품 (Product)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 상품 등록 | `POST` | `/product` |
| 상품 수정 | `PATCH` | `/product/{productId}` |
| 상품 삭제 | `DELETE` | `/product/{productId}` |
| 상품 목록 조회 | `GET` | `/product` |
| 상품 상세 조회 | `GET` | `/product/{productId}` |

## 장바구니 (Cart)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 내 장바구니 조회 | `GET` | `/cart/{accountId}` |
| 장바구니에 상품 추가 | `POST` | `/cart/items/{accountId}` |
| 장바구니 상품 수량 수정 | `PATCH` | `/cart/items/{accountId}` |
| 장바구니 상품 개별 삭제 | `DELETE` | `/cart/items/{accountId}/{cartItemId}` |
| 장바구니 모든 상품 삭제 | `DELETE` | `/cart/items/{accountId}` |

## 주문 (Order)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 주문 생성 | `POST` | `/order/{accountId}` |
| 주문 상태 변경 | `PATCH` | `/order/{orderId}/{accountId}` |
| 내 주문 목록 조회 | `GET` | `/order/{accountId}` |
| 주문 상세 조회 | `GET` | `/order/{accountId}/{orderId}` |
