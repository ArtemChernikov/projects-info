package ru.projects.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.projects.model.Employee;
import ru.projects.security.AuthenticatedEmployee;
import ru.projects.view.employees.AdminEmployeesView;
import ru.projects.view.employees.CreateEmployeeView;
import ru.projects.view.employees.EmployeesView;
import ru.projects.view.projects.CreateProjectView;
import ru.projects.view.projects.ProjectsView;
import ru.projects.view.tasks.CreateTaskView;
import ru.projects.view.tasks.TasksView;

import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    private AuthenticatedEmployee authenticatedEmployee;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedEmployee authenticatedEmployee, AccessAnnotationChecker accessChecker) {
        this.authenticatedEmployee = authenticatedEmployee;
        this.accessChecker = accessChecker;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("My App");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        if (accessChecker.hasAccess(AdminEmployeesView.class)) {
            nav.addItem(new SideNavItem("Employees", EmployeesView.class, LineAwesomeIcon.FILTER_SOLID.create()));
            nav.addItem(new SideNavItem("Create Employee", CreateEmployeeView.class, LineAwesomeIcon.USER.create()));
            nav.addItem(new SideNavItem("Admin Employees", AdminEmployeesView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
            nav.addItem(new SideNavItem("Create Project", CreateProjectView.class, LineAwesomeIcon.PROJECT_DIAGRAM_SOLID.create()));
            nav.addItem(new SideNavItem("Projects", ProjectsView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
            nav.addItem(new SideNavItem("Create Task", CreateTaskView.class, LineAwesomeIcon.PROJECT_DIAGRAM_SOLID.create()));
            nav.addItem(new SideNavItem("Tasks", TasksView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        }
        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<Employee> maybeEmployee = authenticatedEmployee.get();
        if (maybeEmployee.isPresent()) {
            Employee employee = maybeEmployee.get();

            Avatar avatar = new Avatar(employee.getFirstName());
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(employee.getFirstName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedEmployee.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
