package ru.projects.service;

import org.springframework.stereotype.Service;
import ru.projects.mapper.PhotoMapper;
import ru.projects.model.Photo;
import ru.projects.model.User;
import ru.projects.model.dto.photo.PhotoDto;
import ru.projects.repository.PhotoRepository;
import ru.projects.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final UserService userService;
    private final PhotoMapper photoMapper;
    private static final String UPLOAD_DIR = "src/main/resources/META-INF/resources/avatars/";

    public PhotoService(PhotoRepository photoRepository, UserService userService, PhotoMapper photoMapper) {
        this.photoRepository = photoRepository;
        this.userService = userService;
        this.photoMapper = photoMapper;
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    public void savePhoto(PhotoDto photoDto, User user) throws IOException {
        Photo oldPhoto = user.getPhoto();
        if (oldPhoto != null) {
            FileUtils.deleteFile(oldPhoto.getFilePath());
        }

        String fileName = System.currentTimeMillis() + "_" + photoDto.getFileName();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        Files.write(filePath, photoDto.getContent());

        Photo newPhoto = Photo.builder()
                .fileName(fileName)
                .filePath(filePath.toString())
                .build();
        Photo savedPhoto = photoRepository.save(newPhoto);
        userService.addPhotoToUser(user, savedPhoto);
    }


    public PhotoDto getPhotoById(Long photoId) {
        Photo photo = photoRepository.findById(photoId).orElseThrow(() -> new RuntimeException("Photo not found"));
        PhotoDto photoDto = photoMapper.photoToPhotoDto(photo);
        if (photoDto.getContent().length == 0) {
            userService.removePhotoByPhotoId(photoId);
        }
        return photoDto;
    }
}
