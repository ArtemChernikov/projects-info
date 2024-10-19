package ru.projects;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "my-app")
public class ProjectsInfoApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(ProjectsInfoApplication.class, args);
    }

}
