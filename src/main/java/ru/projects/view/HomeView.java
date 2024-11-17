package ru.projects.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.PermitAll;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 10.11.2024
 */
@PageTitle("Home Page")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/file.svg")
@PermitAll
public class HomeView extends VerticalLayout {

    public HomeView() {
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        H2 header = new H2("Welcome to Home Page");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph("This is a place where you can work 🤗"));

        Button downloadButton = new Button("Download tasks report");
        downloadButton.addClickListener(event -> UI.getCurrent().getPage().open("/api/download/employees"));
        add(downloadButton);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}