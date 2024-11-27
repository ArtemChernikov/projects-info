package ru.projects;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Theme(value = "my-app", variant = Lumo.DARK)
public class ProjectsInfoApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(ProjectsInfoApplication.class, args);
    }

}
