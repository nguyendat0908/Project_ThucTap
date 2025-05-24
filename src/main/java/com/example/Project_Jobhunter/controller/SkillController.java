package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Skill;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.service.SkillService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // Creteate a new skill
    @PostMapping("/skills")
    @ApiMessage("Tạo một kỹ năng mới thành công!")
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) throws IdInvalidException {

        boolean isCheckSkillExistName = this.skillService.handleCheckSkillExistsByName(skill.getName());
        if (isCheckSkillExistName) {
            throw new IdInvalidException("Tên kỹ năng đã tồn tại! Vui lòng chọn tên khác.");
        }
        Skill newSkill = this.skillService.handleCreateSkill(skill);
        return ResponseEntity.ok(newSkill);
    }

    // Get a skill by ID
    @GetMapping("/skills/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết một kỹ năng thành công!")
    public ResponseEntity<Skill> getSkillById(@PathVariable("id") UUID id) throws IdInvalidException {
        Skill skill = this.skillService.handleGetSkillById(id);
        if (skill == null) {
            throw new IdInvalidException("Kỹ năng không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(skill);
    }

    // Get all skills
    @GetMapping("/skills")
    @ApiMessage("Hiển thị danh sách kỹ năng thành công!")
    public ResponseEntity<ResPaginationDTO> getListSkills(@Filter Specification<Skill> spec, Pageable pageable) {
        ResPaginationDTO resPaginationDTO = this.skillService.handleGetListSkill(spec, pageable);
        return ResponseEntity.ok(resPaginationDTO);
    }

    // Update a skill
    @PutMapping("/skills")
    @ApiMessage("Cập nhật kỹ năng thành công!")
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skill) throws IdInvalidException {
        Skill newSkill = this.skillService.handleUpdateSkill(skill);
        if (newSkill == null) {
            throw new IdInvalidException("Kỹ năng không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(newSkill);
    }

    // Delete a skill
    @DeleteMapping("/skills/{id}")
    @ApiMessage("Xóa kỹ năng thành công!")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") UUID id) throws IdInvalidException {
        Skill skill = this.skillService.handleGetSkillById(id);
        if (skill == null) {
            throw new IdInvalidException("Kỹ năng không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().build();
    }

}
