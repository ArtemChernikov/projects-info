package ru.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.projects.model.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
