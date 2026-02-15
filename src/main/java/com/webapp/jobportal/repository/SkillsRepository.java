package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillsRepository extends JpaRepository<Skills, Integer> {
    
    @Query("SELECT DISTINCT s.name FROM Skills s ORDER BY s.name")
    List<String> findDistinctSkillNames();
    
    List<Skills> findByNameContainingIgnoreCase(String name);
}

