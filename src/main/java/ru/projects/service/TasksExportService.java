package ru.projects.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.projects.model.dto.task.TaskFullDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TasksExportService {

    private final TaskService taskService;

    public ByteArrayInputStream generateTasksReport() {
        List<TaskFullDto> tasks = taskService.getAllByOrderProjectName();
        return generateExcelReport(tasks);
    }

    public ByteArrayInputStream generateTasksReportByProjectIds(List<Long> projectIds) {
        List<TaskFullDto> tasks = taskService.getAllByProjectIdsAndOrderProjectName(projectIds);
        return generateExcelReport(tasks);
    }

    public ByteArrayInputStream generateActiveTasksReportByProjectIds(List<Long> projectIds) {
        List<TaskFullDto> tasks = taskService.getAllActiveByProjectIdsAndOrderProjectName(projectIds);
        return generateExcelReport(tasks);
    }

    public ByteArrayInputStream generateFinishedTasksReportByProjectIds(List<Long> projectIds) {
        List<TaskFullDto> tasks = taskService.getAllFinishedByProjectIdsAndOrderProjectName(projectIds);
        return generateExcelReport(tasks);
    }

    private ByteArrayInputStream generateExcelReport(List<TaskFullDto> tasks) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tasks and employees");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Project", "Employee", "Task", "Description", "Task type", "Priority", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            int rowIdx = 1;
            for (TaskFullDto task : tasks) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(task.getProject().getName());
                row.createCell(1).setCellValue(task.getEmployee().getName());
                row.createCell(2).setCellValue(task.getName());
                row.createCell(3).setCellValue(task.getDescription());
                row.createCell(4).setCellValue(task.getTaskType());
                row.createCell(5).setCellValue(task.getPriority());
                row.createCell(6).setCellValue(task.getStatus());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
