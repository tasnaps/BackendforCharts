package com.example.backendforcharts;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/geoclasses")
public class GeologicalClassController {

    private final GeologicalClassRepository geologicalClassRepository;

    public GeologicalClassController(GeologicalClassRepository geologicalClassRepository) {
        this.geologicalClassRepository = geologicalClassRepository;
    }

    @GetMapping
    public List<GeologicalClass> getAllGeologicalClasses() {
        return geologicalClassRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeologicalClass createGeologicalClass(@RequestBody GeologicalClass geologicalClass) {
        return geologicalClassRepository.save(geologicalClass);
    }

    @GetMapping("/{id}")
    public GeologicalClass getGeologicalClass(@PathVariable Long id) {
        return geologicalClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GeologicalClass not found with id " + id));
    }

    @PutMapping("/{id}")
    public GeologicalClass updateGeologicalClass(@RequestBody GeologicalClass updatedGeologicalClass, @PathVariable Long id) {
        return geologicalClassRepository.findById(id)
                .map(geologicalClass -> {
                    geologicalClass.setName(updatedGeologicalClass.getName());
                    geologicalClass.setCode(updatedGeologicalClass.getCode());
                    return geologicalClassRepository.save(geologicalClass);
                })
                .orElseThrow(() -> new ResourceNotFoundException("GeologicalClass not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteGeologicalClass(@PathVariable Long id) {
        if (!geologicalClassRepository.existsById(id)) {
            throw new ResourceNotFoundException("GeologicalClass not found with id " + id);
        }
        geologicalClassRepository.deleteById(id);
    }
}