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
import ru.projects.model.Employee;
import ru.projects.model.Project;
import ru.projects.model.Task;
import ru.projects.repository.ProjectRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeExportService {

    private final EmployeeService employeeService;
    private final ProjectRepository projectRepository;

    public ByteArrayInputStream generateExcelReport() throws IOException {
        List<Project> projects = projectRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Разработчики и задачи");

        // Заголовок таблицы
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Проект", "Разработчик", "Задача", "Описание", "Тип задачи", "Приоритет", "Статус"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        int rowIdx = 1;
        for (Project project : projects) {
            for (Employee employee : project.getEmployees()) {
                Set<Task> tasks = project.getTasks();
                for (Task task : tasks) {
                    if (task.getEmployee() != null && task.getEmployee().equals(employee)) {
                        Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue(project.getName());
                        row.createCell(1).setCellValue(employee.getLastName() + " " + employee.getFirstName() + " " + employee.getPatronymicName());
                        row.createCell(2).setCellValue(task.getName());
                        row.createCell(3).setCellValue(task.getDescription());
                        row.createCell(4).setCellValue(task.getTaskType().toString());
                        row.createCell(5).setCellValue(task.getPriority().toString());
                        row.createCell(6).setCellValue(task.getStatus().toString());
                    }
                }
            }
        }

        // Автоматическая подгонка ширины столбцов
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Запись в поток
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
