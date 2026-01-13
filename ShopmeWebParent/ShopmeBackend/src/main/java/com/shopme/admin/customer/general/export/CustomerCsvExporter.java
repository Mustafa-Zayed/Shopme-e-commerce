package com.shopme.admin.customer.general.export;

import com.shopme.admin.utility.AbstractExporter;
import com.shopme.common.entity.Customer;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CustomerCsvExporter {
    public void export(List<Customer> customers, HttpServletResponse response) throws IOException {
        // Set response headers for CSV file
        AbstractExporter.setResponseHeader(response, "text/csv",
                ".csv", "customers_");

        // Create CSV workbook
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Customer ID", "Customer Name", "Country", "E-mail", "Phone Number"};
        String[] nameMapping = {"id", "fullName", "countryName", "email", "phoneNumber"};

        // Write CSV header
        csvBeanWriter.writeHeader(csvHeader);

        // Write CSV data rows
        for (Customer customer : customers) {
            csvBeanWriter.write(customer, nameMapping);
        }
        csvBeanWriter.close();
    }
}
