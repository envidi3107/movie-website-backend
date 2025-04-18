package com.example.IdentityService.Repository;

import com.example.IdentityService.Entity.SystemFilm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemFilmRepository extends JpaRepository<SystemFilm, String> {

}
