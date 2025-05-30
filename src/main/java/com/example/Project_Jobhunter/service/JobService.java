package com.example.Project_Jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.Project_Jobhunter.domain.Company;
import com.example.Project_Jobhunter.domain.Job;
import com.example.Project_Jobhunter.domain.Skill;
import com.example.Project_Jobhunter.dto.response.ResJobDTO;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.repository.JobRepository;
import com.example.Project_Jobhunter.repository.SkillRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyService companyService;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyService companyService) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyService = companyService;
    }

    // Create a new job
    public Job handleCreateJob(Job job) {

        // Check skills
        if (job.getSkills() != null) {
            List<Integer> skillIds = job.getSkills().stream()
                    .map(skill -> skill.getId())
                    .toList();
            List<Skill> skills = this.skillRepository.findByIdIn(skillIds);
            job.setSkills(skills);
        }

        // Check company
        if (job.getCompany() != null) {
            Company company = this.companyService.handleGetCompanyById(job.getCompany().getId());
            job.setCompany(company);
        }
        return this.jobRepository.save(job);
    }

    // Get a job by ID
    public Job handleGetJobById(int id) {
        Optional<Job> jobOptional = this.jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            return jobOptional.get();
        }

        return null;
    }

    // Get all jobs
    public ResPaginationDTO handleGetAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResPaginationDTO resPaginationDTO = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        resPaginationDTO.setMeta(meta);

        List<ResJobDTO> listJobs = pageJob.getContent().stream().map(this::convertToResJobDTO).toList();
        resPaginationDTO.setResult(listJobs);

        return resPaginationDTO;
    }

    // Update a job
    public Job handleUpdateJob(Job job) {
        Job currentJob = this.handleGetJobById(job.getId());
        if (currentJob != null) {
            if (job.getSkills() != null) {
                List<Integer> listIdSkills = job.getSkills().stream().map(item -> item.getId())
                        .collect(Collectors.toList());
                List<Skill> listSkills = this.skillRepository.findByIdIn(listIdSkills);
                currentJob.setSkills(listSkills);
            }
            if (job.getCompany() != null) {
                Company company = this.companyService.handleGetCompanyById(job.getCompany().getId());
                currentJob.setCompany(company);
            }
            currentJob.setName(job.getName() != null ? job.getName() : currentJob.getName());
            currentJob
                    .setDescription(job.getDescription() != null ? job.getDescription() : currentJob.getDescription());
            currentJob.setSalary(job.getSalary() != 0 ? job.getSalary() : currentJob.getSalary());
            currentJob.setQuantity(job.getQuantity() != 0 ? job.getQuantity() : currentJob.getQuantity());
            currentJob.setLocation(job.getLocation() != null ? job.getLocation() : currentJob.getLocation());
            currentJob.setLevel(job.getLevel() != null ? job.getLevel() : currentJob.getLevel());
            currentJob.setStartDate(job.getStartDate() != null ? job.getStartDate() : currentJob.getStartDate());
            currentJob.setEndDate(job.getEndDate() != null ? job.getEndDate() : currentJob.getEndDate());
            currentJob.setActive(job.isActive() != false ? job.isActive() : currentJob.isActive());

            this.jobRepository.save(currentJob);
        }

        return currentJob;
    }

    // Delete a job
    public void handleDeleteJob(int id) {
        Job job = this.handleGetJobById(id);
        this.jobRepository.deleteById(id);
    }

    // Check if a job exists by name
    public boolean handleCheckExistByName(String name) {
        return this.jobRepository.existsByName(name);
    }

    // Convert Job to ResJobDTO
    public ResJobDTO convertToResJobDTO(Job job) {
        ResJobDTO resJobDTO = new ResJobDTO();
        ResJobDTO.CompanyJob companyJob = new ResJobDTO.CompanyJob();

        if (job.getCompany() != null) {
            companyJob.setId(job.getCompany().getId());
            companyJob.setName(job.getCompany().getName());
            resJobDTO.setCompanyJob(companyJob);
        }

        if (job.getSkills() != null) {
            List<String> skillNames = job.getSkills().stream()
                    .map(Skill::getName)
                    .toList();
            resJobDTO.setSkills(skillNames);
        }

        resJobDTO.setId(job.getId());
        resJobDTO.setName(job.getName());
        resJobDTO.setLocation(job.getLocation());
        resJobDTO.setSalary(job.getSalary());
        resJobDTO.setQuantity(job.getQuantity());
        resJobDTO.setActive(job.isActive());
        resJobDTO.setDescription(job.getDescription());
        resJobDTO.setStartDate(job.getStartDate());
        resJobDTO.setEndDate(job.getEndDate());
        resJobDTO.setLevel(job.getLevel());
        resJobDTO.setCreatedAt(job.getCreatedAt());
        resJobDTO.setUpdatedAt(job.getUpdatedAt());
        return resJobDTO;

    }
}
