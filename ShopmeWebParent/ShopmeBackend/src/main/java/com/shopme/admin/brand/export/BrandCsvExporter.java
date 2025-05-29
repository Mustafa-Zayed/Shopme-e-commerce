package com.shopme.admin.brand.export;

import com.shopme.admin.utils.AbstractExporter;
import com.shopme.common.entity.Brand;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class BrandCsvExporter {
    public void export(List<Brand> brands, HttpServletResponse response) throws IOException {
        // Set response headers for CSV file
        AbstractExporter.setResponseHeader(response, "text/csv",
                ".csv", "brands_");

        // Create CSV workbook
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Brand ID", "Name"};
        String[] nameMapping = {"id", "name"};

        // Write CSV header
        csvBeanWriter.writeHeader(csvHeader);

        // Write CSV data rows
        for (Brand brand : brands) {
            csvBeanWriter.write(brand, nameMapping);
        }
        csvBeanWriter.close();
    }
}
