package ru.projects.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.projects.service.TasksExportService;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportExportController {

    private final TasksExportService exportService;

    @Autowired
    public ReportExportController(TasksExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/all-tasks")
    public ResponseEntity<InputStreamResource> downloadTasks() {
        ByteArrayInputStream excelStream = exportService.generateTasksReport();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }

    @GetMapping("/all-tasks-by-projects")
    public ResponseEntity<InputStreamResource> downloadTasksByProjects(@RequestParam List<Long> projectIds) {
        ByteArrayInputStream excelStream = exportService.generateTasksReportByProjectIds(projectIds);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }

    @GetMapping("/active-tasks-by-projects")
    public ResponseEntity<InputStreamResource> downloadActiveTasksByProjects(@RequestParam List<Long> projectIds) {
        ByteArrayInputStream excelStream = exportService.generateActiveTasksReportByProjectIds(projectIds);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=active_tasks_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }

    @GetMapping("/finished-tasks-by-projects")
    public ResponseEntity<InputStreamResource> downloadFinishedTasksByProjects(@RequestParam List<Long> projectIds) {
        ByteArrayInputStream excelStream = exportService.generateFinishedTasksReportByProjectIds(projectIds);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=finished_tasks_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }


}
