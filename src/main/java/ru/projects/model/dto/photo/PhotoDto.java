package ru.projects.model.dto.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 22.12.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PhotoDto {

    private String fileName;

    private byte[] content;
}
