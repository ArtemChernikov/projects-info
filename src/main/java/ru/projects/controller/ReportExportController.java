package ru.projects.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.projects.service.BugsExportService;
import ru.projects.service.TasksExportService;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportExportController {

    private final TasksExportService tasksExportService;
    private final BugsExportService bugsExportService;

    @GetMapping("/all-tasks")
    public ResponseEntity<InputStreamResource> downloadTasks() {
        ByteArrayInputStream excelStream = tasksExportService.generateTasksReport();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }

    @GetMapping("/all-tasks-by-projects")
    public ResponseEntity<InputStreamResource> downloadTasksByProjects(@RequestParam List<Long> projectIds) {
        ByteArrayInputStream excelStream = tasksExportService.generateTasksReportByProjectIds(projectIds);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }

    @GetMapping("/active-tasks-by-projects")
    public ResponseEntity<InputStreamResource> downloadActiveTasksByProjects(@RequestParam List<Long> projectIds) {
        ByteArrayInputStream excelStream = tasksExportService.generateActiveTasksReportByProjectIds(projectIds);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=active_tasks_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }

    @GetMapping("/finished-tasks-by-projects")
    public ResponseEntity<InputStreamResource> downloadFinishedTasksByProjects(@RequestParam List<Long> projectIds) {
        ByteArrayInputStream excelStream = tasksExportService.generateFinishedTasksReportByProjectIds(projectIds);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=finished_tasks_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }

    @GetMapping("/bugs-by-projects")
    public ResponseEntity<InputStreamResource> downloadBugsByProjects(@RequestParam List<Long> projectIds) {
        ByteArrayInputStream excelStream = bugsExportService.generateBugReportByProjectIds(projectIds);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bugs_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }


}
