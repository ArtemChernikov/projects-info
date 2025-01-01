package ru.projects.view.projects;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import ru.projects.model.Employee;
import ru.projects.model.dto.project.ProjectFullDto;
import ru.projects.service.EmployeeService;
import ru.projects.service.ProjectService;
import ru.projects.view.MainLayout;

@PageTitle("My projects")
@Route(value = "employee-projects", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_USER", "ROLE_DEV", "ROLE_TEST"})
@Menu(order = 9, icon = "line-awesome/svg/project-diagram-solid.svg")
public class EmployeeProjectsView extends Div  {

    private final Grid<ProjectFullDto> grid = new Grid<>(ProjectFullDto.class, false);

    private Employee authenticatedEmployee;

    private final ProjectService projectService;

    public EmployeeProjectsView(ProjectService projectService, EmployeeService employeeService) {
        this.projectService = projectService;
        authenticatedEmployee = employeeService.getCurrentEmployee();
        addClassNames("employee-projects-view");
        createUI();
    }

    private void createUI() {
        SplitLayout splitLayout = new SplitLayout();
        createGridLayout(splitLayout);
        add(splitLayout);
        configureGrid();
    }

    private void configureGrid() {
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("startDate").setAutoWidth(true);
        grid.addColumn("endDate").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);

        grid.addColumn(ProjectEmployeeDetails.createToggleDetailsRenderer(grid));
        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(ProjectEmployeeDetails.createProjectDetailsRenderer());

        grid.setItems(query -> projectService.getAllByEmployeeId(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                        authenticatedEmployee.getEmployeeId())
                .stream());

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }
}