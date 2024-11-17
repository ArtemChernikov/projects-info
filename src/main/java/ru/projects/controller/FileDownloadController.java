package ru.projects.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.projects.service.TasksExportService;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("api")
public class FileDownloadController {

    private final TasksExportService exportService;

    @Autowired
    public FileDownloadController(TasksExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/download/employees")
    public ResponseEntity<InputStreamResource> downloadEmployeesXml() {
        ByteArrayInputStream excelStream = exportService.generateExcelReport();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }
}
