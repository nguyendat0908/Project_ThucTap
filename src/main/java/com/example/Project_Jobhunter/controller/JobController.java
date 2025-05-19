package com.example.Project_Jobhunter.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Job;
import com.example.Project_Jobhunter.dto.response.ResJobDTO;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.service.CompanyService;
import com.example.Project_Jobhunter.service.JobService;
import com.example.Project_Jobhunter.service.SkillService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;
    private final SkillService skillService;
    private final CompanyService companyService;

    public JobController(JobService jobService, SkillService skillService, CompanyService companyService) {
        this.jobService = jobService;
        this.skillService = skillService;
        this.companyService = companyService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a new job")
    public ResponseEntity<ResJobDTO> createJob(@RequestBody @Valid Job job)
            throws IdInvalidException, IllegalArgumentException {
        this.checkExist(job);
        if (job.getEndDate() != null && job.getStartDate() != null) {
            if (job.getEndDate().isBefore(job.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date!");
            }

        }
        Job newJob = this.jobService.handleCreateJob(job);
        return ResponseEntity.ok(this.jobService.convertToResJobDTO(newJob));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get job by id")
    public ResponseEntity<ResJobDTO> getJobById(@PathVariable("id") UUID id) throws IdInvalidException {
        Job job = this.jobService.handleGetJobById(id);
        if (job == null) {
            throw new IdInvalidException("ID no exist! Please check your ID.");
        }
        return ResponseEntity.ok(this.jobService.convertToResJobDTO(job));
    }

    @GetMapping("/jobs")
    @ApiMessage("Get list job")
    public ResponseEntity<ResPaginationDTO> getListJobs(@Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok(this.jobService.handleGetAllJobs(spec, pageable));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update job")
    public ResponseEntity<ResJobDTO> updateJob(@RequestBody Job job) throws IdInvalidException {
        this.checkExist(job);
        if (job.getEndDate() != null && job.getStartDate() != null) {
            if (job.getEndDate().isBefore(job.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date!");
            }

        }
        Job newJob = this.jobService.handleUpdateJob(job);
        if (newJob == null) {
            throw new IdInvalidException("ID no exist! Please check your ID.");
        }
        return ResponseEntity.ok(this.jobService.convertToResJobDTO(newJob));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") UUID id) throws IdInvalidException {
        Job job = this.jobService.handleGetJobById(id);
        if (job == null) {
            throw new IdInvalidException("ID no exist! Please check your ID.");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok(null);
    }

    public String checkExist(Job job) throws IdInvalidException {

        boolean isCheckNameExist = this.jobService.handleCheckExistByName(job.getName());
        if (isCheckNameExist) {
            throw new IdInvalidException("Name existed! Please chose another email.");
        }
        List<UUID> listIdSkills = job.getSkills().stream()
                .map(item -> item.getId()).collect(Collectors.toList());
        for (UUID id : listIdSkills) {
            boolean isCheckSkillExist = this.skillService.handleCheckExistById(id);
            if (!isCheckSkillExist) {
                throw new IdInvalidException("Skill not exist. Please check again!");
            }
        }
        boolean isCheckCompanyExist = this.companyService.handleCheckExistById(job.getCompany().getId());
        if (!isCheckCompanyExist) {
            throw new IdInvalidException("Company not exist. Please check again!");
        }

        return "Valid success!";
    }
}
