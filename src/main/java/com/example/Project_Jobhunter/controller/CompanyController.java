package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Company;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.service.CompanyService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;


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
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // Crete a new company
    @PostMapping("/companies")
    @ApiMessage("Tạo công ty mới thành công!")
    public ResponseEntity<Company> createCompany(@RequestBody @Valid Company company) throws IdInvalidException {
        boolean isCheckExistCompanyName = this.companyService.handleCheckCompanyByName(company.getName());
        if (isCheckExistCompanyName) {
            throw new IdInvalidException("Tên công ty đã tồn tại! Vui lòng chọn tên khác.");
        }
        return ResponseEntity.ok(this.companyService.handleCreateCompany(company));
    }

    // Get a company by ID
    @GetMapping("/companies/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết một công ty thành công!")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") int id) throws IdInvalidException {
        Company company = this.companyService.handleGetCompanyById(id);
        if (company == null) {
            throw new IdInvalidException("Không tìm thấy công ty! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(company);
    }

    // Get all companies
    @GetMapping("/companies")
    @ApiMessage("Hiển thị danh sách công ty thành công!")
    public ResponseEntity<ResPaginationDTO> getListCompanies(@Filter Specification<Company> spec, Pageable pageable) {
        return ResponseEntity.ok(this.companyService.handleGetAllCompanies(spec, pageable));
    }

    // Update a company
    @PutMapping("/companies")
    @ApiMessage("Cập nhật thông tin công ty thành công!")
    public ResponseEntity<Company> updateCompany(@RequestBody @Valid Company company) throws IdInvalidException {
        boolean isCheckExistCompanyName = this.companyService.handleCheckCompanyByName(company.getName());
        if (isCheckExistCompanyName) {
            throw new IdInvalidException("Tên công ty đã tồn tại! Vui lòng chọn tên khác.");
        }
        Company newCompany = this.companyService.handleUpdateCompany(company);
        if (newCompany == null) {
            throw new IdInvalidException("Công ty không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(newCompany);
    }

    // Delete a company
    @DeleteMapping("/companies/{id}")
    @ApiMessage("Xóa công ty thành công!")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") int id) throws IdInvalidException {
        Company company = this.companyService.handleGetCompanyById(id);
        if (company == null) {
            throw new IdInvalidException("Công ty không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok().build();
    }

}
