package com.example.Project_Jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.Project_Jobhunter.domain.Job;
import com.example.Project_Jobhunter.domain.Skill;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {

    boolean existsByName(String name);

    List<Job> findBySkillsIn(List<Skill> skills);
}
