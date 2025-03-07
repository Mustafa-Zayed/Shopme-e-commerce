package com.shopme.admin.user;

import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class UserCsvExporter {
    public void export(List<User> users, HttpServletResponse response) throws IOException {
        // Set response headers for CSV file
        AbstractExporter.setResponseHeader(response, "text/csv", ".csv");

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
