package ru.projects.views.projects;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import ru.projects.model.dto.EmployeeShortDto;
import ru.projects.model.dto.ProjectFullDto;

import java.util.List;
import java.util.Set;
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
            EmployeeShortDto projectManager = projectFullDto.getProjectManager();
            Set<EmployeeShortDto> backendDevelopers = projectFullDto.getBackendDevelopers();
            Set<EmployeeShortDto> frontendDevelopers = projectFullDto.getFrontendDevelopers();
            EmployeeShortDto fullstackDeveloper = projectFullDto.getFullstackDeveloper();
            EmployeeShortDto qaEngineer = projectFullDto.getQaEngineer();
            EmployeeShortDto aqaEngineer = projectFullDto.getAqaEngineer();
            EmployeeShortDto devOps = projectFullDto.getDevOps();
            EmployeeShortDto dataScientist = projectFullDto.getDataScientist();
            EmployeeShortDto dataAnalyst = projectFullDto.getDataAnalyst();

            if (projectManager != null) {
                projectManagerField.setValue(projectManager.getName());
            }
            if (backendDevelopers != null) {
                StringBuilder stringBuilder = new StringBuilder();
                backendDevelopers.forEach(employeeShortDto -> {
                    stringBuilder.append(employeeShortDto.getName());
                    stringBuilder.append(", ");
                });
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                backendDeveloperField.setValue(stringBuilder.toString());
            }
            if (frontendDevelopers != null) {
                StringBuilder stringBuilder = new StringBuilder();
                frontendDevelopers.forEach(employeeShortDto -> {
                    stringBuilder.append(employeeShortDto.getName());
                    stringBuilder.append(", ");
                });
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                frontendDeveloperField.setValue(stringBuilder.toString());
            }
            if (fullstackDeveloper != null) {
                fullstackDeveloperField.setValue(fullstackDeveloper.getName());
            }
            if (qaEngineer != null) {
                qaEngineerField.setValue(qaEngineer.getName());
            }
            if (aqaEngineer != null) {
                aqaEngineerField.setValue(aqaEngineer.getName());
            }
            if (devOps != null) {
                devOpsField.setValue(devOps.getName());
            }
            if (dataScientist != null) {
                dataScientistField.setValue(dataScientist.getName());
            }
            if (dataAnalyst != null) {
                dataAnalystField.setValue(dataAnalyst.getName());
            }
        }
    }
}
