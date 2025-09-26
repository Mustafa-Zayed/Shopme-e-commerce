package com.shopme.admin.product.export;

import com.shopme.admin.utils.AbstractExporter;
import com.shopme.common.entity.Product;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class ProductCsvExporter {
    public void export(List<Product> products, HttpServletResponse response) throws IOException {
        // Set response headers for CSV file
        AbstractExporter.setResponseHeader(response, "text/csv",
                ".csv", "products_");

        // Create CSV workbook
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Product ID", "Product Name", "Brand", "Category"};
        String[] nameMapping = {"id", "name", "brand", "category"};

        // Write CSV header
        csvBeanWriter.writeHeader(csvHeader);

        // Write CSV data rows
        for (Product product : products) {
            csvBeanWriter.write(product, nameMapping);
        }
        csvBeanWriter.close();
    }
}
