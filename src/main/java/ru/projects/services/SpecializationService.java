package ru.projects.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.projects.model.Specialization;
import ru.projects.model.dto.SpecializationDto;
import ru.projects.repository.SpecializationRepository;

import java.util.List;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public List<SpecializationDto> getAllSpecializationsDto() {
        return specializationRepository.findAll().stream()
                .map(specialization -> new SpecializationDto(specialization.getSpecializationName()))
                .toList();
    }

    public List<String> getAllSpecializationsNames() {
        return specializationRepository.findAll().stream()
                .map(Specialization::getSpecializationName)
                .toList();
    }

}
