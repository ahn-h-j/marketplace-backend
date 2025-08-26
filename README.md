# Clonestagram

인스타그램 클론 프로젝트입니다.

사용자는 게시물을 작성 및 공유하고 다른 사용자를 팔로우하거나 "좋아요"와 댓글로 소통할 수 있습니다. 또한 해시태그와 사용자 검색을 통해 원하는 콘텐츠를 탐색하는 기능을 구현했습니다.

---

## ✏️ 기술 스택

### Tech
<img src="https://img.shields.io/badge/java-FC4C02?style=for-the-badge&logo=java&logoColor=white"><img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white"><img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white"><img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">

### Tool
<img src="https://img.shields.io/badge/Github-000000?style=for-the-badge&logo=Github&logoColor=white"/><img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white"/>

---

## 🖥️ 프로젝트 산출물

### ERD

![image](https://github.com/user-attachments/assets/1e7463a7-5eaf-4de7-8714-a9c1cf321eb6)


# API 명세서

## Auth (인증)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 회원가입 | `POST` | `/join` |
| 로그아웃 | `POST` | `/logout` |
| 토큰 재발행 | `POST` | `/reissue` |

## User & Profile (사용자 및 프로필)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 내 프로필 조회 | `GET` | `/user/me` |
| 유저 ID로 검색 | `GET` | `/user/id` |
| 특정 유저 프로필 조회 | `GET` | `/{userId}/profile` |
| 프로필 수정 | `PUT` | `/{userId}/profile` |
| 회원 탈퇴 (프로필 삭제) | `DELETE` | `/{userId}/profile` |

## Post (게시물 - 이미지/비디오)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 이미지 업로드 | `POST` | `/image` |
| 이미지 수정 | `PUT` | `/image/{postSeq}` |
| 이미지 삭제 | `DELETE` | `/image/{postSeq}` |
| 비디오 업로드 | `POST` | `/video` |
| 비디오 수정 | `PUT` | `/video/{postSeq}` |
| 비디오 삭제 | `DELETE` | `/video/{postSeq}` |
| 특정 유저 게시물 조회 | `GET` | `/feeds/user` |

## Feed (피드)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 내 피드 조회 | `GET` | `/feeds` |
| 전체 피드 조회 | `GET` | `/feeds/all` |
| 팔로우 피드 조회 | `GET` | `/feeds/follow` |
| 본 피드 삭제 | `DELETE` | `/feeds/seen` |

## Search (검색)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 유저 검색 | `GET` | `/search/users` |
| 팔로잉 검색 | `GET` | `/search/following` |
| 팔로워 검색 | `GET` | `/search/follower` |
| 해시태그 검색 | `GET` | `/search/tag` |
| 해시태그 추천 | `GET` | `/search/tag/suggestions` |
| 유저 추천 | `GET` | `/search/user/suggestions` |

## Follow (팔로우)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 팔로워 목록 조회 | `GET` | `/follow/{userId}/profile/followers` |
| 팔로잉 목록 조회 | `GET` | `/follow/{userId}/profile/following` |
| 팔로우/언팔로우 토글 | `POST` | `/follow/{follower}/profile/{followed}` |

## Like (좋아요)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 좋아요 개수 조회 | `GET` | `/feeds/{postId}/likes` |
| 좋아요 토글 | `POST` | `/feeds/{postId}/likes` |
| 좋아요 여부 확인 | `GET` | `/posts/{postId}/liked` |

## Comment (댓글)

| 기능 | HTTP 메소드 | URL |
|------|-------------|-----|
| 댓글 작성 | `POST` | `/comments` |
| 특정 댓글 조회 | `GET` | `/comments/{id}` |
| 특정 게시물 댓글 목록 조회 | `GET` | `/comments/post/{postId}` |
| 댓글 삭제 | `DELETE` | `/comments/{commentId}` |
