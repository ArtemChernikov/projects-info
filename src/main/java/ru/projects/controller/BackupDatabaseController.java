package ru.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/api/database")
@Slf4j
public class BackupDatabaseController {

    private final BackupService backupService;

    @PostMapping("/backup")
    public void backup() throws IOException {
        log.info("Backup started");
        backupService.createBackup();
    }

    @PostMapping("/restore")
    public void restore() throws IOException {
        log.info("Restore started");
        backupService.restoreBackup();
    }
}
