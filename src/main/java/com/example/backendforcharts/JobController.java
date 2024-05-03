package com.example.backendforcharts;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/import")
    public ResponseEntity<Long> importFile(@RequestParam("file") MultipartFile file) {
        Job job = jobService.processFileImport(file);
        return new ResponseEntity<>(job.getId(), HttpStatus.OK);
    }

    @GetMapping("/import/{id}")
    public ResponseEntity<String> importJobStatus(@PathVariable Long id) {
        String status = jobService.getJobStatus(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/export")
    public ResponseEntity<Long> exportFile() {
        Job job = jobService.initiateFileExport();
        return new ResponseEntity<>(job.getId(), HttpStatus.OK);
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<String> exportJobStatus(@PathVariable Long id) {
        String status = jobService.getJobStatus(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/export/{id}/file")
    public ResponseEntity<byte[]> getFileExport(@PathVariable Long id) {
        byte[] fileData = jobService.getExportedFile(id);
        if(fileData == null) {
            throw new RuntimeException("Export is still in process");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename("output_filename.csv").build());
        return new ResponseEntity<>(fileData, HttpStatus.OK);
    }
}