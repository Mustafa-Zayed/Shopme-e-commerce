package com.shopme.admin.category.export;

import com.shopme.admin.utils.AbstractExporter;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CategoryCsvExporter {
    public void export(List<Category> categories, HttpServletResponse response) throws IOException {
        // Set response headers for CSV file
        AbstractExporter.setResponseHeader(response, "text/csv",
                ".csv", "categories_");

        // Create CSV workbook
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Category ID", "Name"};
        String[] nameMapping = {"id", "name"};

        // Write CSV header
        csvBeanWriter.writeHeader(csvHeader);

        // Write CSV data rows
        for (Category category : categories) {
            category.setName(category.getName().replace("--", "  "));
            csvBeanWriter.write(category, nameMapping);
        }
        csvBeanWriter.close();
    }
}
