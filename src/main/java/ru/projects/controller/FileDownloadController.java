package ru.projects.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.projects.service.EmployeeExportService;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("api")
public class FileDownloadController {

    private final EmployeeExportService exportService;

    @Autowired
    public FileDownloadController(EmployeeExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/download/employees")
    public ResponseEntity<InputStreamResource> downloadEmployeesXml() throws Exception {
        ByteArrayInputStream excelStream = exportService.generateExcelReport();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=developers_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excelStream));
    }
}
