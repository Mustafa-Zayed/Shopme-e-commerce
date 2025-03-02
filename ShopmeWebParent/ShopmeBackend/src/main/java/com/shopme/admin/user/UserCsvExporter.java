package com.shopme.admin.user;

import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserCsvExporter {
    public void export(List<User> users, HttpServletResponse response) throws IOException {
        // Set response headers for CSV file download
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormat.format(new Date());
        String fileName = "users_" + timestamp + ".csv";

        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        // Create CSV workbook
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        String[] nameMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};

        // Write CSV header
        csvBeanWriter.writeHeader(csvHeader);

        // Write CSV data rows
        for (User user : users) {
            csvBeanWriter.write(user, nameMapping);
        }
        csvBeanWriter.close();
    }
}
