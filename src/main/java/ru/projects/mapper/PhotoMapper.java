package ru.projects.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.projects.model.Photo;
import ru.projects.model.dto.photo.PhotoDto;
import ru.projects.util.FileUtils;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 22.12.2024
 */
@Mapper(componentModel = "spring", imports = {FileUtils.class})
public abstract class PhotoMapper {

    @Mapping(target = "content", expression = "java(FileUtils.readFileAsBytes(photo.getFilePath()))")
    public abstract PhotoDto photoToPhotoDto(Photo photo);

}
