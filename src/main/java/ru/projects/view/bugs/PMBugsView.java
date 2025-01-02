package ru.projects.view.bugs;

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
import ru.projects.model.dto.bug.BugViewDto;
import ru.projects.service.BugService;
import ru.projects.service.EmployeeService;
import ru.projects.view.MainLayout;

@PageTitle("Bugs")
@Route(value = "pm-bugs", layout = MainLayout.class)
@RolesAllowed(value = {"ROLE_PM"})
@Menu(order = 10, icon = "line-awesome/svg/bug-solid.svg")
public class PMBugsView extends Div  {

    private final Grid<BugViewDto> grid = new Grid<>(BugViewDto.class, false);

    private final BugService bugService;

    private Employee authenticatedEmployee;

    public PMBugsView(BugService bugService, EmployeeService employeeService) {
        this.bugService = bugService;
        authenticatedEmployee = employeeService.getCurrentEmployee();
        addClassNames("employee-bugs-view");
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
        grid.addColumn("project").setAutoWidth(true);
        grid.addColumn("priority").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);

        grid.addColumn(BugDescriptionDetails.createToggleDetailsRenderer(grid));
        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(BugDescriptionDetails.createBugDetailsRenderer());

        refreshGrid();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }

    private void refreshGrid() {
        grid.setItems(query -> bugService.getAllByProjects(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                authenticatedEmployee.getProjects()
        ).stream());
        grid.getDataProvider().refreshAll();
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }
}