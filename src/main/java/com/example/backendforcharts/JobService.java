package com.example.backendforcharts;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SectionRepository sectionRepository;
    private final GeologicalClassRepository geologicalClassRepository;
    private final ExcelFileParser excelFileParser;

    public JobService(JobRepository jobRepository, SectionRepository sectionRepository, GeologicalClassRepository geologicalClassRepository, ExcelFileParser excelFileParser) {
        this.jobRepository = jobRepository;
        this.sectionRepository = sectionRepository;
        this.geologicalClassRepository = geologicalClassRepository;
        this.excelFileParser = excelFileParser;
    }

    public Job processFileImport(MultipartFile file) {
        Job job = new Job();
        job.setStatus("IN_PROGRESS");
        System.out.println("Processing file input: " + file.getOriginalFilename());
        job.setStartedAt(new Date());
        job = jobRepository.save(job);
        processFile(job, file);

        return job;
    }

    @Async
    public void processFile(Job job, MultipartFile file) {
        try {
            List<Section> sections = excelFileParser.parse(file);

            for (Section section : sections) {
                // Set Section to GeologicalClasses first
                section.getGeologicalClasses().forEach(geologicalClass ->
                        geologicalClass.setSection(section));
                // Then, save Section which includes GeologicalClasses on cascade
                sectionRepository.save(section);
            }

            job.setStatus("DONE");
            job.setResult("File Imported Successfully");
            job.setEndedAt(new Date());
            jobRepository.save(job);
        } catch (Exception ex) {
            job.setStatus("ERROR");
            job.setResult(ex.getMessage());
            job.setEndedAt(new Date());
            jobRepository.save(job);
            ex.printStackTrace();
        }
    }

    public String getJobStatus(Long jobId) {
        return (String) jobRepository.findById(jobId).map(Job::getStatus).orElse("Job not found");
    }

    public Job initiateFileExport() {
        Job job = new Job();
        job.setStatus("IN_PROGRESS");
        job.setStartedAt(new Date());
        // Save the job to the database
        job = jobRepository.save(job);

        exportFile(job);

        return job;
    }

    public byte[] getExportedFile(Long jobId) {
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if(jobOptional.isPresent()){
            Job job = jobOptional.get();
            if("DONE".equals(job.getStatus())){
                try {
                    File exportedFile = new File(job.getFileName());
                    return Files.readAllBytes(exportedFile.toPath());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read exported file", e);
                }
            }
        }
        throw new RuntimeException("Export is still in process or job not found");
    }
    @Async
    public void exportFile(Job job) {
        try {
            // Fetch all the Sections from the database
            List<Section> sections = sectionRepository.findAll();

            // Create an XLS file using ExcelFileParser
            Workbook workbook = excelFileParser.createWorkbook(sections);

            // Save the workbook to an XLS file
            String exportedFileName = "sections_export_" + job.getId() + ".xlsx"; // Create a unique file name
            try (FileOutputStream fileOut = new FileOutputStream(exportedFileName)) {
                workbook.write(fileOut);
            }

            job.setStatus("DONE");
            job.setEndedAt(new Date());
            job.setResult("File Exported Successfully");
            job.setFileName(exportedFileName); // Save exported file's name to the job

            // Update the job status in the database
            jobRepository.save(job);
        } catch (Exception ex) {
            job.setStatus("ERROR");
            job.setResult(ex.getMessage());
            job.setEndedAt(new Date());
            job.setFileName(null); // No file was saved, set fileName to null

            // Update the job status in the database
            jobRepository.save(job);

            // Log or rethrow the exception, according to your requirements
        }
    }
}