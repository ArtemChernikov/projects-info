package ru.projects.view.employees;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import ru.projects.model.dto.EmployeeDto;
import ru.projects.service.EmployeeService;
import ru.projects.service.SpecializationService;
import ru.projects.view.MainLayout;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.10.2024
 */
@PageTitle("Employees")
@Route(value = "employees", layout = MainLayout.class)
@Uses(Icon.class)
@RolesAllowed(value = {"ROLE_USER"})
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
public class EmployeesView extends Div {

    private Grid<EmployeeDto> grid;

    private EmployeeFilter employeeFilter;
    private final EmployeeService employeeService;

    public EmployeesView(EmployeeService employeeService, SpecializationService specializationService) {
        this.employeeService = employeeService;
        setSizeFull();
        addClassNames("employees-view");

        this.employeeFilter = new EmployeeFilter(specializationService, this::refreshGrid);
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), employeeFilter, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private HorizontalLayout createMobileFilters() {
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (employeeFilter.getClassNames().contains("visible")) {
                employeeFilter.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                employeeFilter.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }


    private Component createGrid() {
        grid = new Grid<>(EmployeeDto.class, false);
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("specialization").setAutoWidth(true);

        grid.setItems(query -> employeeService.getAllByFilter(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                employeeFilter).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();

    }

}
