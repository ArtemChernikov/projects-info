package ru.projects.view.home;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.projects.model.Employee;
import ru.projects.model.Project;
import ru.projects.service.BackupService;
import ru.projects.service.EmployeeService;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 10.11.2024
 */
@PageTitle("Home page")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/home-solid.svg")
@RolesAllowed(value = {"ROLE_ADMIN", "ROLE_USER", "ROLE_PM", "ROLE_DEV", "ROLE_TEST"})
public class HomeView extends VerticalLayout {

    private final BackupService backupService;

    private Employee authenticatedEmployee;

    public HomeView(BackupService backupService, EmployeeService employeeService) {
        this.backupService = backupService;
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        H2 header = new H2("Welcome to Home page");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph("This is a place where you can work 🤗"));

        if (isUserInRole("ROLE_ADMIN")) {
            Button backupDatabaseButton = createButton("Create backup database", this::createBackupDatabase);
            Button restoreDatabaseButton = createButton("Restore database", this::restoreDatabase);
            Button downloadButton = createButton("Download tasks report",
                    () -> UI.getCurrent().getPage().open("/api/report/all-tasks"));

            add(backupDatabaseButton, restoreDatabaseButton, downloadButton);
        }
        if (isUserInRole("ROLE_PM")) {
            authenticatedEmployee = employeeService.getCurrentEmployee();
            String projectIds = authenticatedEmployee.getProjects().stream()
                    .map(Project::getProjectId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            Button tasksReportButton = createButton("Download tasks report",
                    () -> UI.getCurrent().getPage().open("/api/report/all-tasks-by-projects?projectIds=" + projectIds));
            Button activeTasksReportButton = createButton("Download active tasks report",
                    () -> UI.getCurrent().getPage().open("/api/report/active-tasks-by-projects?projectIds=" + projectIds));
            Button finishedTasksReportButton = createButton("Download finished tasks report",
                    () -> UI.getCurrent().getPage().open("/api/report/finished-tasks-by-projects?projectIds=" + projectIds));
            Button bugsReportButton = createButton("Download bugs report",
                    () -> UI.getCurrent().getPage().open("/api/report/bugs-by-projects?projectIds=" + projectIds));

            add(tasksReportButton, activeTasksReportButton, finishedTasksReportButton, bugsReportButton);
        }

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private Button createButton(String label, Runnable action) {
        Button button = new Button(label);
        button.addClickListener(event -> action.run());
        return button;
    }

    private boolean isUserInRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    private void restoreDatabase() {
        try {
            backupService.restoreBackup();
            Notification.show("Database restored successfully!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (IOException e) {
            Notification.show("Error during database restore: " + e.getMessage(), 3000,
                    Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void createBackupDatabase() {
        try {
            backupService.createBackup();
            Notification.show("Database backup successfully!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (IOException e) {
            Notification.show("Error during database backup: " + e.getMessage(), 3000,
                    Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

}