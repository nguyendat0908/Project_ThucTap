package com.example.Project_Jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.Project_Jobhunter.domain.Company;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // Create a new company
    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    // Get a company by ID
    public Company handleGetCompanyById(UUID id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional.get();
        }

        return null;
    }

    // Get all companies
    public ResPaginationDTO handleGetAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResPaginationDTO resPaginationDTO = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        resPaginationDTO.setMeta(meta);

        List<Company> listCompanies = pageCompany.getContent();
        resPaginationDTO.setResult(listCompanies);

        return resPaginationDTO;

    }

    // Update a company
    public Company handleUpdateCompany(Company company) {
        Company currentCompany = this.handleGetCompanyById(company.getId());
        if (currentCompany != null) {
            currentCompany.setName(company.getName() != null ? company.getName() : currentCompany.getName());
            currentCompany
                    .setAddress(company.getAddress() != null ? company.getAddress() : currentCompany.getAddress());
            currentCompany.setLogo(company.getLogo() != null ? company.getLogo() : currentCompany.getLogo());
            currentCompany.setDescription(company.getDescription() != null ? company.getDescription()
                    : currentCompany.getDescription());
            this.companyRepository.save(currentCompany);
        }

        return currentCompany;
    }

    // Delete a company
    public void handleDeleteCompany(UUID id) {
        this.companyRepository.deleteById(id);
    }

    // Check if a company exists by name
    public boolean handleCheckCompanyByName(String name) {
        return this.companyRepository.existsByName(name);
    }

    public boolean handleCheckExistById(UUID id) {
        return this.companyRepository.existsById(id);
    }

}
