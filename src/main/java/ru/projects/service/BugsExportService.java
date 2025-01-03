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
import ru.projects.model.dto.bug.BugViewDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BugsExportService {

    private final BugService bugService;

    public ByteArrayInputStream generateBugReportByProjectIds(List<Long> projectIds) {
        List<BugViewDto> bugs = bugService.getAllByProjectIds(projectIds);
        return generateExcelReport(bugs);
    }

    private ByteArrayInputStream generateExcelReport(List<BugViewDto> bugs) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Bugs");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Project", "Name", "Description", "Priority", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            int rowIdx = 1;
            for (BugViewDto bug : bugs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(bug.getProject());
                row.createCell(1).setCellValue(bug.getName());
                row.createCell(2).setCellValue(bug.getDescription());
                row.createCell(3).setCellValue(bug.getPriority());
                row.createCell(4).setCellValue(bug.getStatus());
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
