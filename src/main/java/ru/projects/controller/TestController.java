package ru.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.projects.service.BackupService;

import java.io.IOException;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.11.2024
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class TestController {

    private final BackupService backupService;

    @GetMapping("/api/backup")
    public void backup() throws IOException {
        log.info("controller backup");
        backupService.createBackup();
    }

    @GetMapping("/api/restore")
    public void restore() throws IOException {
        log.info("controller restore");
        backupService.restoreBackup();
    }
}
