package com.example.backendforcharts;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelFileParser {

    public List<Section> parse(MultipartFile file) {
        List<Section> sections = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // we only read the first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Assuming each row represents a Section and geological classes
                // Assuming the columns are arranged in order: name, Class1 name, Class1 code, Class2 name, Class2 code, ...
                String sectionName = row.getCell(0).getStringCellValue();

                // Create a new Section object
                Section section = new Section();
                section.setName(sectionName);

                // Iterate over the Geological Classes
                for (int i = 1; i < row.getLastCellNum(); i += 2) {
                    String className = row.getCell(i).getStringCellValue();
                    String classCode = row.getCell(i + 1).getStringCellValue();

                    // Create a new GeologicalClass object
                    GeologicalClass geologicalClass = new GeologicalClass();
                    geologicalClass.setName(className);
                    geologicalClass.setCode(classCode);

                    // Add the GeologicalClass to the Section
                    section.getGeologicalClasses().add(geologicalClass);
                }

                // Add the Section to the list
                sections.add(section);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions according to your needs
        }

        return sections;
    }

    public Workbook createWorkbook(List<Section> sections) {
        Workbook workbook = new XSSFWorkbook(); // Create new workbook
        Sheet sheet = workbook.createSheet(); // Create new sheet

        int rowNum = 0;

        // Writing headers(row 0)
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Section name");

        // Assuming that each Section has the same number of GeologicalClasses
        int numGeoClasses = sections.get(0).getGeologicalClasses().size();
        for (int i = 0; i < numGeoClasses; i++) {
            headerRow.createCell(i * 2 + 1).setCellValue("Class" + (i + 1) + " name");
            headerRow.createCell(i * 2 + 2).setCellValue("Class" + (i + 1) + " code");
        }

        // Writing the Sections
        for (Section section : sections) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(section.getName());

            int geoClassNum = 1;
            for (GeologicalClass geologicalClass : section.getGeologicalClasses()) {
                row.createCell(geoClassNum * 2 - 1).setCellValue(geologicalClass.getName());
                row.createCell(geoClassNum * 2).setCellValue(geologicalClass.getCode());
                geoClassNum++;
            }
        }

        return workbook;
    }
}