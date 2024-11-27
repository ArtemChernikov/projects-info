package ru.projects.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 19.11.2024
 */
@Service
@Slf4j
public class BackupService {

    @Scheduled(cron = "* * 8 * * ?")
    public void createBackup() throws IOException {
        log.info("Service Create backup");
        ProcessBuilder processBuilder = new ProcessBuilder(
                "pg_dump",
                "-U", "postgres",
                "-h", "localhost",
                "-p", "5432",
                "projects-info"
        );

        processBuilder.redirectOutput(new File("./backup.sql"));
        processBuilder.redirectErrorStream(true);
        processBuilder.environment().put("PGPASSWORD", "2236");

        Process process = processBuilder.start();
        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Backup created successfully at " + "./db/backup.sql");
            } else {
                System.err.println("Backup failed with exit code " + exitCode);
            }
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Backup process was interrupted", e);
        }
    }

    public void restoreBackup() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "psql",
                "-U", "postgres",
                "-h", "localhost",
                "-p", "5432",
                "projects-info"
        );

        processBuilder.redirectInput(new File("./backup.sql"));
        processBuilder.redirectErrorStream(true);
        processBuilder.environment().put("PGPASSWORD", "2236");

        Process process = processBuilder.start();
        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Database restored successfully from " + "./backup");
            } else {
                System.err.println("Restore failed with exit code " + exitCode);
            }
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Restore process was interrupted", e);
        }
    }
}


