package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Company;
import com.example.Project_Jobhunter.domain.Job;
import com.example.Project_Jobhunter.domain.Resume;
import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.dto.response.ResResumeDTO;
import com.example.Project_Jobhunter.service.JobService;
import com.example.Project_Jobhunter.service.ResumeService;
import com.example.Project_Jobhunter.service.UserService;
import com.example.Project_Jobhunter.util.SecurityUtil;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;
    private final JobService jobService;
    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterBuilder filterBuilder;

    public ResumeController(ResumeService resumeService, UserService userService, JobService jobService,
            FilterSpecificationConverter filterSpecificationConverter, FilterBuilder filterBuilder) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.jobService = jobService;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterBuilder = filterBuilder;
    }

    @PostMapping("/resumes")
    @ApiMessage("Tạo một bản sơ yếu lý lịch mới thành công!")
    public ResponseEntity<ResResumeDTO> createResume(@RequestBody Resume resume) throws IdInvalidException {
        User user = this.userService.handleGetUserById(resume.getUser().getId());
        if (user == null) {
            throw new IdInvalidException("Người dùng không tồn tại! Vui lòng kiểm tra lại ID.");

        }
        Job job = this.jobService.handleGetJobById(resume.getJob().getId());
        if (job == null) {
            throw new IdInvalidException("Công việc không tồn tại! Vui lòng kiểm tra lại ID.");
        }

        Resume newResume = this.resumeService.handleCreateResume(resume);
        return ResponseEntity.ok(this.resumeService.convertToResumeDTO(newResume));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết một bản sơ yếu lý lịch thành công!")
    public ResponseEntity<ResResumeDTO> getResumeById(@PathVariable("id") int id) throws IdInvalidException {
        Resume resume = this.resumeService.handleGetResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Bản sơ yếu lý lịch không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(this.resumeService.convertToResumeDTO(resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("Hiển thị danh sách các bản sơ yếu lý lịch thành công!")
    public ResponseEntity<ResPaginationDTO> getListResumes(@Filter Specification<Resume> spec, Pageable pageable) {
        return ResponseEntity.ok(this.resumeService.handleGetAllResumes(spec, pageable));
    }

    @PutMapping("/resumes")
    @ApiMessage("Câp nhật thông tin bản sơ yếu lý lịch thành công!")
    public ResponseEntity<ResResumeDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {

        User user = this.userService.handleGetUserById(resume.getUser().getId());
        if (user == null) {
            throw new IdInvalidException("Người dùng không tồn tại! Vui lòng kiểm tra lại ID.");

        }
        Job job = this.jobService.handleGetJobById(resume.getJob().getId());
        if (job == null) {
            throw new IdInvalidException("Công việc không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        Resume newResume = this.resumeService.handleUpdateResume(resume);
        if (newResume == null) {
            throw new IdInvalidException("Bản sơ yếu lý lịch không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(this.resumeService.convertToResumeDTO(newResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Xóa bản sơ yếu lý lịch thành công!")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") int id) throws IdInvalidException {
        Resume resume = this.resumeService.handleGetResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Bản sơ yếu lý lịch không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        this.resumeService.handleDeleteResume(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/resumes-history/{id}")
    @ApiMessage("Hiển thị lịch sử các CV đã nộp thành công!")
    public ResponseEntity<ResPaginationDTO> getResumesHistoryByUser(@PathVariable("id") int id, Pageable pageable)
            throws IdInvalidException {
        User user = this.userService.handleGetUserById(id);
        if (user == null) {
            throw new IdInvalidException("Người dùng không tồn tại! Vui lòng kiểm tra lại ID.");

        }

        return ResponseEntity.ok(this.resumeService.handleGetResumesHistory(id, pageable));
    }

    @GetMapping("/resumes-company")
    @ApiMessage("Hiển thị các CV của công ty thành công!")
    public ResponseEntity<ResPaginationDTO> getResumesOfCompany(@Filter Specification<Resume> spec, Pageable pageable) {
        List<Integer> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            Company company = currentUser.getCompany();
            if (company != null) {
                List<Job> companyJobs = company.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(item -> item.getId()).collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter
                .convert(filterBuilder.field("job").in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok(this.resumeService.handleGetAllResumes(finalSpec, pageable));
    }

}
