# MovieWebsiteProject

Mô tả: Một ứng dụng web quản lý phim (catalogue, phản hồi người dùng, playlist, xem phim) được xây dựng bằng Java \- Spring Boot \- Maven.

## Tính năng chính
- Quản lý phim: hệ thống phim và dữ liệu TMDB (một\-một với `Film`)
- Thống kê: lượt xem, lượt like/dislike, số bình luận
- Playlist người dùng: lưu phim vào playlist cá nhân
- Phản hồi: like/dislike, comment
- Lịch sử xem (watching) và thống kê liên quan

## Kiến trúc & Entities chính
- `Film` — thực thể trung tâm (tham chiếu tới `TmdbFilm`, `SystemFilm`)
- `TmdbFilm` — dữ liệu từ TMDB
- `SystemFilm` — dữ liệu do hệ thống quản lý
- `UserFilmPlaylist` — quan hệ phim ↔ playlist người dùng
- `Reaction` — like/dislike của người dùng
- `Comment` — bình luận
- `Watching` — lịch sử lượt xem

Ứng dụng theo mô hình REST API, sử dụng JPA/Hibernate để ánh xạ CRUD với cơ sở dữ liệu.

## Công nghệ
- Java 17+ (hoặc tương thích)
- Spring Boot
- Spring Data JPA
- Maven
- Cơ sở dữ liệu: MySQL / PostgreSQL (cấu hình trong `application.properties` hoặc `application.yml`)
- Lombok (để giảm boilerplate)

## Yêu cầu trước khi chạy
- JDK 17 hoặc mới hơn
- Maven 3.6+
- Một database server (MySQL)
- Thiết lập cấu hình kết nối DB trong `src/main/resources/application.properties`:
    - `spring.datasource.url`
    - `spring.datasource.username`
    - `spring.datasource.password`
    - `spring.jpa.hibernate.ddl-auto` (hoặc dùng migration tool như Flyway)

## Cài đặt & chạy
1. Cập nhật `application.properties` với thông tin DB.
2. Build project:
    - `mvn clean package`
3. Chạy:
    - `mvn spring-boot:run`
    - hoặc chạy jar: `java -jar target/MovieWebsiteProject-0.0.1-SNAPSHOT.jar`

## API (mô tả nhanh)
- `GET /api/films` — lấy danh sách phim
- `GET /api/films/{id}` — lấy chi tiết phim
- `POST /api/films` — tạo phim (tuỳ quyền)
- `PUT /api/films/{id}` — cập nhật phim
- `DELETE /api/films/{id}` — xoá phim
- `POST /api/films/{id}/reactions` — tạo reaction
- `POST /api/films/{id}/comments` — thêm bình luận
- `POST /api/users/{userId}/playlists` — quản lý playlist
- `GET /api/watching` — quản lý lịch sử xem phim

## Kiểm thử
- Chạy unit/integration tests:
    - `mvn test`

## Góp phần & phát triển
- Fork repository, tạo branch tính năng, mở pull request
- Viết test cho tính năng mới
- Tuân thủ coding style hiện có và kiểm tra bằng linter nếu có

## License
- Chọn license phù hợp (ví dụ MIT/Apache 2.0). Thêm file `LICENSE` nếu cần.
