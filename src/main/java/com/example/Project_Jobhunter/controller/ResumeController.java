package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Job;
import com.example.Project_Jobhunter.domain.Resume;
import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.dto.response.ResResumeDTO;
import com.example.Project_Jobhunter.service.JobService;
import com.example.Project_Jobhunter.service.ResumeService;
import com.example.Project_Jobhunter.service.UserService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;
    private final JobService jobService;

    public ResumeController(ResumeService resumeService, UserService userService, JobService jobService) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.jobService = jobService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a new resume")
    public ResponseEntity<ResResumeDTO> createResume(@RequestBody Resume resume) throws IdInvalidException {
        User user = this.userService.handleGetUserById(resume.getUser().getId());
        if (user == null) {
            throw new IdInvalidException("User not found! Please check the ID again.");

        }
        Job job = this.jobService.handleGetJobById(resume.getJob().getId());
        if (job == null) {
            throw new IdInvalidException("Job not found! Please check the ID again.");
        }

        Resume newResume = this.resumeService.handleCreateResume(resume);
        return ResponseEntity.ok(this.resumeService.convertToResumeDTO(newResume));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get a resume by ID")
    public ResponseEntity<ResResumeDTO> getResumeById(@PathVariable("id") UUID id) throws IdInvalidException {
        Resume resume = this.resumeService.handleGetResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume not found! Please check the ID again.");
        }
        return ResponseEntity.ok(this.resumeService.convertToResumeDTO(resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("Get list resumes")
    public ResponseEntity<ResPaginationDTO> getListResumes(@Filter Specification<Resume> spec, Pageable pageable) {
        return ResponseEntity.ok(this.resumeService.handleGetAllResumes(spec, pageable));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResResumeDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {

        User user = this.userService.handleGetUserById(resume.getUser().getId());
        if (user == null) {
            throw new IdInvalidException("User not found! Please check the ID again.");

        }
        Job job = this.jobService.handleGetJobById(resume.getJob().getId());
        if (job == null) {
            throw new IdInvalidException("Job not found! Please check the ID again.");
        }
        Resume newResume = this.resumeService.handleUpdateResume(resume);
        if (newResume == null) {
            throw new IdInvalidException("Resume not found! Please check the ID again.");
        }
        return ResponseEntity.ok(this.resumeService.convertToResumeDTO(newResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume by ID")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") UUID id) throws IdInvalidException {
        Resume resume = this.resumeService.handleGetResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume not found! Please check the ID again.");
        }
        this.resumeService.handleDeleteResume(id);
        return ResponseEntity.ok(null);
    }

}
