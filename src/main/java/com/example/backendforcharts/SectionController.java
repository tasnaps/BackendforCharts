package com.example.backendforcharts;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/sections")
public class SectionController {

    private final SectionRepository sectionRepository;

    public SectionController(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @GetMapping
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Section createSection(@RequestBody Section section) {
        return sectionRepository.save(section);
    }

    @PostMapping("/sections/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                //Workbook for inputstream
                HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());

                // Get the first sheet
                HSSFSheet sheet = workbook.getSheetAt(0);

                // Iterate through rows
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    // For each row, iterate through each columns
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        // TODO: Save/Update Entity in database
                    }
                }

                workbook.close();
            } catch (Exception e) {
                //todo
            }
        }
    }

    @GetMapping("/{id}")
    public Section getSection(@PathVariable Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id " + id));
    }
    @GetMapping("/by-code")
    public List<Section> getSectionsByGeologicalClassCode(@RequestParam("code") String code) {
        return this.sectionRepository.findAllByGeologicalClassCode(code);
    }

    @PutMapping("/{id}")
    public Section updateSection(@RequestBody Section updatedSection, @PathVariable Long id) {
        return sectionRepository.findById(id)
                .map(section -> {
                    section.setName(updatedSection.getName());
                    section.setGeologicalClasses(updatedSection.getGeologicalClasses());
                    return sectionRepository.save(section);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteSection(@PathVariable Long id) {
        if (!sectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Section not found with id " + id);
        }
        sectionRepository.deleteById(id);
    }
}