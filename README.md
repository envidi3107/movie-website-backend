# Movie Website Project

## Project Overview
The Movie Website Project is a web application built using Java Spring Boot. It provides a platform for users to explore, manage, and interact with movies and related content. The application includes features such as user authentication, film management, playlist creation, and user interactions like comments and reactions.

## Features
- **User Authentication**: Secure login and registration using JWT.
- **Film Management**: CRUD operations for films and episodes.
- **Playlists**: Users can create and manage their own playlists.
- **Comments and Reactions**: Users can comment on and react to films and episodes.
- **Notifications**: Real-time notifications for user interactions.
- **WebSocket Integration**: Real-time updates using WebSocket.
- **Cloudinary Integration**: Media storage and management.
- **Validation and Error Handling**: Robust mechanisms for input validation and error management.

## Technical Details
- **Backend**: Java Spring Boot
  - Controllers: Handle REST API endpoints for authentication, film management, playlists, etc.
  - Services: Business logic implementation.
  - Repositories: Database interactions using Spring Data JPA.
  - Entities: Data models representing the database schema.
  - Configuration: Application setup including security, WebSocket, and OpenAPI.
- **Database**: MySQL
- **Dependencies**:
  - Spring Boot Starter Data JPA
  - Spring Boot Starter Web
  - Spring Boot Starter WebSocket
  - Spring Security
  - MapStruct
  - Lombok
  - Cloudinary
  - SpringDoc OpenAPI

## Setup and Running
1. Clone the repository.
2. Configure the database connection in `application.yml`.
3. Build the project using Maven:
   ```
   ./mvnw clean install
   ```
4. Run the application:
   ```
   ./mvnw spring-boot:run
   ```
5. Access the application at `http://localhost:8080`.

## Folder Structure
- `src/main/java/com/example/MovieWebsiteProject`
  - `Controller`: REST API endpoints.
  - `Service`: Business logic.
  - `Repository`: Database interactions.
  - `Entity`: Data models.
  - `Configuration`: Application setup.
- `src/main/resources`
  - `application.yml`: Configuration file.
  - `templates`: HTML templates.
  - `static`: Static assets.

## API Endpoints

### WatchingController
- **POST /api/watching/save-watching-history**: Save film watching history.
- **GET /api/watching/save-watched-duration/{filmId}**: Save watched duration for a film.

### UserNotificationController
- **GET /api/user-notification/get-all**: Retrieve all user notifications.
- **DELETE /api/user-notification/delete**: Delete a specific user notification.
- **DELETE /api/user-notification/clear-all**: Clear all user notifications.

### UserFilmPlaylistController
- **POST /api/users/add-film-to-user-playlist**: Add a film to a user's playlist.
- **GET /api/users/playlists**: Retrieve user playlists.
- **DELETE /api/users/playlists/{playlistId}**: Delete a specific playlist.

### UserController
- **POST /users/signup**: Create a new user.
- **POST /users/update-password**: Update user password.
- **GET /users/my-info**: Retrieve user information.

### ReactionController
- **POST /api/reaction/save-reaction**: Save a reaction for a film.
- **POST /api/reaction/save-episode-reaction**: Save a reaction for an episode.
- **GET /api/reaction/get-user-reaction**: Retrieve user reactions.

### FilmController
- **GET /films/all**: Retrieve all films.

### PlaylistController
- **GET /api/playlist/get-user-playlist**: Retrieve user playlists.
- **POST /api/playlist/create-playlist**: Create a new playlist.
- **DELETE /api/playlist/delete-user-playlist**: Delete a user playlist.

### CommentController
- **POST /comment/save-comment**: Save a comment.
- **POST /comment/update-comment**: Update a user comment.

### AuthenticationController
- **POST /auth/login**: Authenticate a user.
- **POST /auth/logout**: Logout a user.

### AdminController
- **GET /admin/get-users**: Retrieve paginated list of users.
- **GET /admin/users/registrations/monthly**: Retrieve monthly user registration statistics.
