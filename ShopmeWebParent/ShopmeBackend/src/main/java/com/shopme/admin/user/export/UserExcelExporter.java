package com.shopme.admin.user.export;

import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.util.List;

public class UserExcelExporter {
    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;

    public UserExcelExporter() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Users");
    }

    private void writeHeaderLine() {
        XSSFRow row = sheet.createRow(0);
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "ID", cellStyle);
        createCell(row, 1, "Email", cellStyle);
        createCell(row, 2, "First Name", cellStyle);
        createCell(row, 3, "Last Name", cellStyle);
        createCell(row, 4, "Roles", cellStyle);
        createCell(row, 5, "Enabled", cellStyle);
    }

    private void writeDataLines(List<User> users) {
        int rowNum = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (User user : users) {
            XSSFRow row = sheet.createRow(rowNum++);
            createCell(row, 0, user.getId(), cellStyle);
            createCell(row, 1, user.getEmail(), cellStyle);
            createCell(row, 2, user.getFirstName(), cellStyle);
            createCell(row, 3, user.getLastName(), cellStyle);
            createCell(row, 4, user.getRoles().toString(), cellStyle);
            createCell(row, 5, user.isEnabled(), cellStyle);
        }
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, XSSFCellStyle cellStyle) {
        Cell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof Integer)
            cell.setCellValue((Integer) value);
        else if (value instanceof Boolean)
            cell.setCellValue((Boolean) value);
        else
            cell.setCellValue((String) value);

        cell.setCellStyle(cellStyle);
    }

    public void export(List<User> users, HttpServletResponse response) throws IOException {
        // Set response headers for Excel file
        AbstractExporter.setResponseHeader(response, "application/octet-stream", ".xlsx");

        // Write Excel header
        writeHeaderLine();

        // Write Excel data
        writeDataLines(users);

        workbook.write(response.getOutputStream());
    }


}
