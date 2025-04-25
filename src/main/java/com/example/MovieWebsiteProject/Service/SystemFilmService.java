package com.example.MovieWebsiteProject.Service;


import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.SystemFilmRepository;
import com.example.MovieWebsiteProject.dto.response.SystemFilmDetailResponse;
import com.example.MovieWebsiteProject.dto.response.SystemFilmSummaryResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemFilmService {
    SystemFilmRepository systemFilmRepository;

    public List<SystemFilmSummaryResponse> getAllSystemFilmSummary(int page) {
        if (page < 0)
            throw new AppException(ErrorCode.INVALID_PAGE_NUMBER);

        List<Map<String, Object>> results = systemFilmRepository.getAllSystemFilmSummary();
        Map<String, SystemFilmSummaryResponse> systemFilmMap = new LinkedHashMap<>();

        for (Map<String, Object> row : results) {
            String systemFilmId = row.get("system_film_id").toString();
            if (systemFilmMap.get(systemFilmId) == null) {
                SystemFilmSummaryResponse systemFilmSummaryResponse = SystemFilmSummaryResponse.builder()
                        .systemFilmId((String) row.get("system_film_id"))
                        .title((String) row.get("title"))
                        .releaseDate(((Timestamp) row.get("release_date")).toLocalDateTime())
                        .backdropPath((String) row.get("backdrop_path"))
                        .posterPath((String) row.get("poster_path"))
                        .genres(new HashSet<>())
                        .build();
                systemFilmMap.put(systemFilmId, systemFilmSummaryResponse);
            }
            systemFilmMap.get(systemFilmId).getGenres().add(row.get("genre_name").toString());
        }
        List<SystemFilmSummaryResponse> systemFilmSummaryResponseList = new ArrayList<>(systemFilmMap.values());
        List<SystemFilmSummaryResponse> response = new ArrayList<>();
        int size = 5;
        int offset = page * size;
        int limit = page * size + size;
        for (int i = offset; i < limit; i++) {
            if (i < systemFilmSummaryResponseList.size()) {
                response.add(systemFilmSummaryResponseList.get(i));
            } else break;
        }
        return response;
    }

    public SystemFilmDetailResponse getSystemFilmDetail(String filmId) {
        List<Map<String, Object>> results = systemFilmRepository.getSystemFilmDetail(filmId);
        Map<String, Object> firstRow = results.getFirst();
        SystemFilmDetailResponse film = SystemFilmDetailResponse.builder()
                .systemFilmId(filmId)
                .title((String) firstRow.get("title"))
                .releaseDate(((Timestamp) firstRow.get("release_date")).toLocalDateTime())
                .backdropPath((String) firstRow.get("backdrop_path"))
                .posterPath((String) firstRow.get("poster_path"))
                .genres(new HashSet<>())

                .adult(Boolean.TRUE.equals(firstRow.get("adult")))
                .videoPath((String) firstRow.get("video_path"))
                .overview((String) firstRow.get("overview"))
                .createdAt((Timestamp) firstRow.get("created_at"))
                .updatedAt((Timestamp) firstRow.get("updated_at"))
                .watchedDuration((Long) firstRow.get("watched_duration"))
                .numberOfViews((Long) firstRow.get("number_of_views"))
                .numberOfLikes((Long) firstRow.get("number_of_likes"))
                .numberOfDislikes((Long) firstRow.get("number_of_dislikes"))
                .numberOfComments((Long) firstRow.get("number_of_comments"))
                .build();
        for (Map<String, Object> row : results) {
            String genreName = (String) row.get("genre_name");
            if (genreName != null) {
                film.getGenres().add(genreName);
            }
        }

        return film;
    }
}
