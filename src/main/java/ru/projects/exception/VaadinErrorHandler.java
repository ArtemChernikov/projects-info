package ru.projects.exception;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.DefaultErrorHandler;
import com.vaadin.flow.server.ErrorEvent;

public class VaadinErrorHandler extends DefaultErrorHandler {

    @Override
    public void error(ErrorEvent event) {
        Throwable throwable = event.getThrowable();
        // Логирование ошибки
        throwable.printStackTrace();

        // Вывод уведомления пользователю
        Notification.show("An unexpected error occurred: " + throwable.getMessage(), 5000,
                Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
