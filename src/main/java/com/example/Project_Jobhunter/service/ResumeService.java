package com.example.Project_Jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.Project_Jobhunter.domain.Job;
import com.example.Project_Jobhunter.domain.Resume;
import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.dto.response.ResResumeDTO;
import com.example.Project_Jobhunter.repository.ResumeRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final JobService jobService;

    public ResumeService(ResumeRepository resumeRepository, UserService userService, JobService jobService) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.jobService = jobService;
    }

    // Create a new resume
    public Resume handleCreateResume(Resume resume) {

        User user = this.userService.handleGetUserById(resume.getUser().getId());
        if (user != null) {
            resume.setUser(user);
        }
        Job job = this.jobService.handleGetJobById(resume.getJob().getId());
        if (job != null) {
            resume.setJob(job);
        }
        return this.resumeRepository.save(resume);
    }

    // Get a resume by ID
    public Resume handleGetResumeById(UUID id) {
        Optional<Resume> resumeOptional = this.resumeRepository.findById(id);
        if (resumeOptional.isPresent()) {
            return resumeOptional.get();
        } else {
            return null;
        }
    }

    // Get all resumes
    public ResPaginationDTO handleGetAllResumes(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> resumePage = this.resumeRepository.findAll(spec, pageable);

        ResPaginationDTO resPaginationDTO = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(resumePage.getTotalPages());
        meta.setTotal(resumePage.getTotalElements());

        resPaginationDTO.setMeta(meta);

        List<ResResumeDTO> resumeDTOs = resumePage.getContent().stream()
                .map(this::convertToResumeDTO)
                .toList();
        resPaginationDTO.setResult(resumeDTOs);

        return resPaginationDTO;
    }

    // Update a resume
    public Resume handleUpdateResume(Resume resume) {
        Resume currentResume = this.handleGetResumeById(resume.getId());
        if (currentResume != null) {
            User user = this.userService.handleGetUserById(resume.getUser().getId());
            if (user != null) {
                resume.setUser(user);
            }
            Job job = this.jobService.handleGetJobById(resume.getJob().getId());
            if (job != null) {
                resume.setJob(job);
            }

            currentResume.setEmail(resume.getEmail());
            currentResume.setUrl(resume.getUrl());
            currentResume.setStatus(resume.getStatus());
            currentResume.setUser(resume.getUser());
            currentResume.setJob(resume.getJob());

            this.resumeRepository.save(currentResume);
        }
        return currentResume;
    }

    // Delete a resume
    public void handleDeleteResume(UUID id) {
        this.resumeRepository.deleteById(id);
    }

    // Convert a resume to resumeDTO
    public ResResumeDTO convertToResumeDTO(Resume resume) {
        ResResumeDTO resumeDTO = new ResResumeDTO();
        ResResumeDTO.UserResume userResume = new ResResumeDTO.UserResume();
        ResResumeDTO.JobResume jobResume = new ResResumeDTO.JobResume();

        if (resume.getUser() != null) {
            userResume.setId(resume.getUser().getId());
            userResume.setName(resume.getUser().getName());
            resumeDTO.setUserResume(userResume);
        }

        if (resume.getJob() != null) {
            jobResume.setId(resume.getJob().getId());
            jobResume.setName(resume.getJob().getName());
            resumeDTO.setJobResume(jobResume);
        }

        resumeDTO.setId(resume.getId());
        resumeDTO.setEmail(resume.getEmail());
        resumeDTO.setUrl(resume.getUrl());
        resumeDTO.setStatus(resume.getStatus());
        resumeDTO.setCreatedAt(resume.getCreatedAt());
        resumeDTO.setUpdatedAt(resume.getUpdatedAt());
        return resumeDTO;
    }
}
