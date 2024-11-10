package ru.projects.view.projects;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import ru.projects.model.dto.employee.EmployeeShortDto;
import ru.projects.model.dto.project.ProjectFullDto;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 22.10.2024
 */
public class ProjectEmployeeDetails {

    public static Renderer<ProjectFullDto> createToggleDetailsRenderer(Grid<ProjectFullDto> grid) {
        return LitRenderer.<ProjectFullDto>of(
                        "<vaadin-button theme=\"tertiary\" @click=\"${handleClick}\">Employees</vaadin-button>")
                .withFunction("handleClick",
                        project -> grid.setDetailsVisible(project,
                                !grid.isDetailsVisible(project)));
    }

    public static ComponentRenderer<ProjectDetailsFormLayout, ProjectFullDto> createProjectDetailsRenderer() {
        return new ComponentRenderer<>(ProjectDetailsFormLayout::new,
                ProjectDetailsFormLayout::setEmployees);
    }

    private static class ProjectDetailsFormLayout extends FormLayout {
        private final TextField projectManagerField = new TextField("Project Manager");
        private final TextField backendDeveloperField = new TextField("Backend Developers");
        private final TextField frontendDeveloperField = new TextField("Frontend Developers");
        private final TextField fullstackDeveloperField = new TextField("Fullstack Developer");
        private final TextField qaEngineerField = new TextField("QA Engineer");
        private final TextField aqaEngineerField = new TextField("AQA Engineer");
        private final TextField devOpsField = new TextField("DevOps");
        private final TextField dataScientistField = new TextField("Data Scientist");
        private final TextField dataAnalystField = new TextField("Data Analyst");

        public ProjectDetailsFormLayout() {
            Stream.of(projectManagerField, fullstackDeveloperField, qaEngineerField, aqaEngineerField, devOpsField,
                            dataScientistField, dataAnalystField, backendDeveloperField, frontendDeveloperField)
                    .forEach(field -> {
                        field.setReadOnly(true);
                        add(field);
                    });

            setResponsiveSteps(new ResponsiveStep("0", 3));
            setColspan(projectManagerField, 3);
            setColspan(backendDeveloperField, 3);
            setColspan(frontendDeveloperField, 3);
            setColspan(fullstackDeveloperField, 3);
            setColspan(qaEngineerField, 3);
            setColspan(aqaEngineerField, 3);
            setColspan(devOpsField, 3);
            setColspan(dataScientistField, 3);
            setColspan(dataAnalystField, 3);
        }

        public void setEmployees(ProjectFullDto projectFullDto) {
            setEmployeeField(projectFullDto.getProjectManagers(), projectManagerField);
            setEmployeeField(projectFullDto.getBackendDevelopers(), backendDeveloperField);
            setEmployeeField(projectFullDto.getFrontendDevelopers(), frontendDeveloperField);
            setEmployeeField(projectFullDto.getFullstackDevelopers(), fullstackDeveloperField);
            setEmployeeField(projectFullDto.getQaEngineers(), qaEngineerField);
            setEmployeeField(projectFullDto.getAqaEngineers(), aqaEngineerField);
            setEmployeeField(projectFullDto.getDevOps(), devOpsField);
            setEmployeeField(projectFullDto.getDataScientists(), dataScientistField);
            setEmployeeField(projectFullDto.getDataAnalysts(), dataAnalystField);
        }

        private void setEmployeeField(Set<EmployeeShortDto> employees, TextField field) {
            if (employees != null && !employees.isEmpty()) {
                String employeeNames = employees.stream()
                        .map(EmployeeShortDto::getName)
                        .collect(Collectors.joining(", "));
                field.setValue(employeeNames);
            }
        }
    }
}
