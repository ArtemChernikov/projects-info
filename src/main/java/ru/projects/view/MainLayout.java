package ru.projects.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.projects.exception.VaadinErrorHandler;
import ru.projects.model.Photo;
import ru.projects.model.User;
import ru.projects.model.dto.photo.PhotoDto;
import ru.projects.security.AuthenticatedUser;
import ru.projects.service.PhotoService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private final PhotoService photoService;

    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;

    public MainLayout(AuthenticatedUser authenticatedUser, PhotoService photoService) {
        this.authenticatedUser = authenticatedUser;
        this.photoService = photoService;
        VaadinSession.getCurrent().setErrorHandler(new VaadinErrorHandler());
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
        Span appName = new Span("Projects info");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(entry.title(), entry.path()));
            }
        });

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> optionalUser = authenticatedUser.get();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Avatar avatar = new Avatar(user.getUsername());
            Photo photo = user.getPhoto();
            if (photo != null) {
                PhotoDto photoDto = photoService.getPhotoById(photo.getPhotoId());
                StreamResource resource = new StreamResource("profile-pic",
                        () -> new ByteArrayInputStream(photoDto.getContent()));
                avatar.setImageResource(resource);
                avatar.setThemeName("xsmall");
                avatar.getElement().setAttribute("tabindex", "-1");
            }

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getUsername());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> authenticatedUser.logout());

            Upload upload = getUpload(user);
            userName.getSubMenu().addItem(upload);

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

    private Upload getUpload(User user) {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/*");
        upload.setUploadButton(new Button("Download avatar"));
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            try {
                byte[] fileBytes = buffer.getInputStream().readAllBytes();
                PhotoDto photoDto = new PhotoDto(fileName, fileBytes);

                photoService.savePhoto(photoDto, user);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return upload;
    }
}
