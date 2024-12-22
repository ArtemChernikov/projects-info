package ru.projects.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.projects.model.Photo;
import ru.projects.model.User;
import ru.projects.repository.UserRepository;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 22.12.2024
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void addPhotoToUser(User user, Photo photo) {
        user.setPhoto(photo);
        userRepository.save(user);
    }

    public void removePhotoByPhotoId(Long photoId) {
        User user = userRepository
                .findByPhoto_photoId(photoId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPhoto(null);
        userRepository.save(user);
    }

}
