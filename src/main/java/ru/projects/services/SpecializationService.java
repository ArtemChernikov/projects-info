package ru.projects.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.projects.model.Specialization;
import ru.projects.model.dto.SpecializationDto;
import ru.projects.model.enums.EnumSpecialization;
import ru.projects.model.enums.TaskType;
import ru.projects.repository.SpecializationRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public Specialization getSpecializationByName(String name) {
        return specializationRepository.findBySpecializationName(name)
                .orElseThrow(() -> new RuntimeException("Specialization not found"));
    }

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

    public List<String> getEnumSpecializationsByTaskType(TaskType taskType) {
        return switch (taskType) {
            case DEVELOPMENT -> List.of(EnumSpecialization.BACKEND_DEVELOPER.getSpecializationName(),
                    EnumSpecialization.FRONTEND_DEVELOPER.getSpecializationName(),
                    EnumSpecialization.FULLSTACK_DEVELOPER.getSpecializationName());
            case TESTING -> List.of(EnumSpecialization.QA_ENGINEER.getSpecializationName(),
                    EnumSpecialization.AQA_ENGINEER.getSpecializationName());
            case DEV_OPS -> List.of(EnumSpecialization.DEV_OPS.getSpecializationName());
            case DATA_SCIENCE -> List.of(EnumSpecialization.DATA_SCIENTIST.getSpecializationName());
            case DATA_ANALYSIS -> List.of(EnumSpecialization.DATA_ANALYST.getSpecializationName());
        };
    }

}
