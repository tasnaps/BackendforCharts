package com.example.backendforcharts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query("SELECT s FROM Section s JOIN s.geologicalClasses gc WHERE gc.code = :code")
    List<Section> findAllByGeologicalClassCode(@Param("code") String code);
}