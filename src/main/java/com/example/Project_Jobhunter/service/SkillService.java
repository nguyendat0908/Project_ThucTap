package com.example.Project_Jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.Project_Jobhunter.domain.Skill;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.repository.SkillRepository;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    // Create a new skill
    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    // Get a skill by ID
    public Skill handleGetSkillById(int id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        if (skill.isPresent()) {
            return skill.get();
        }
        return null;
    }

    // Get all skills
    public ResPaginationDTO handleGetListSkill(Specification<Skill> spec, Pageable pageable) {

        Page<Skill> pageSkills = this.skillRepository.findAll(spec, pageable);

        ResPaginationDTO resPaginationDTO = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageSkills.getTotalPages());
        meta.setTotal(pageSkills.getTotalElements());

        resPaginationDTO.setMeta(meta);
        resPaginationDTO.setResult(pageSkills.getContent());

        return resPaginationDTO;

    }

    // Update a skill
    public Skill handleUpdateSkill(Skill skill) {
        Skill currentSkill = this.handleGetSkillById(skill.getId());
        if (currentSkill != null) {
            currentSkill.setName(skill.getName());
            this.skillRepository.save(currentSkill);
        }
        return currentSkill;
    }

    // Delete a skill
    public void handleDeleteSkill(int id) {
        this.skillRepository.deleteById(id);
    }

    // Check if a skill exists by name
    public boolean handleCheckSkillExistsByName(String name) {
        return this.skillRepository.existsByName(name);
    }

    // Check exist id
    public boolean handleCheckExistById(int id) {
        return this.skillRepository.existsById(id);
    }
}
