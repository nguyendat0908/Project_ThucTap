package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Subscriber;
import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.service.SkillService;
import com.example.Project_Jobhunter.service.SubscriberService;
import com.example.Project_Jobhunter.service.UserService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.IdInvalidException;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberService subscriberService;
    private final SkillService skillService;

    public SubscriberController(SubscriberService subscriberService,
            SkillService skillService) {
        this.subscriberService = subscriberService;
        this.skillService = skillService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Theo dõi thành công!")
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {
        boolean isEmailExists = this.subscriberService.handleCheckExistByEmail(subscriber.getEmail());
        if (isEmailExists) {
            throw new IdInvalidException("Email đã tồn tại! Vui lòng chọn email khác.");
        }
        List<Integer> listIdSkills = subscriber.getSkills().stream()
                .map(item -> item.getId()).collect(Collectors.toList());
        for (int id : listIdSkills) {
            boolean isCheckSkillExist = this.skillService.handleCheckExistById(id);
            if (!isCheckSkillExist) {
                throw new IdInvalidException("Kỹ năng không tồn tại. Vui lòng kiểm tra lại!");
            }
        }

        return ResponseEntity.ok(this.subscriberService.handleCreateSubscriber(subscriber));

    }

    @PutMapping("/subscribers")
    @ApiMessage("Cập nhật theo dõi kỹ năng thành công!")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriberRequest)
            throws IdInvalidException {
        Subscriber subscriberDB = this.subscriberService.handleGetSuById(subscriberRequest.getId());
        if (subscriberDB == null) {
            throw new IdInvalidException("ID không tồn tại vui lòng kiểm tra lại!");
        }
        List<Integer> listIdSkills = subscriberRequest.getSkills().stream()
                .map(item -> item.getId()).collect(Collectors.toList());
        for (int id : listIdSkills) {
            boolean isCheckSkillExist = this.skillService.handleCheckExistById(id);
            if (!isCheckSkillExist) {
                throw new IdInvalidException("Kỹ năng không tồn tại. Vui lòng kiểm tra lại!");
            }
        }

        return ResponseEntity.ok(this.subscriberService.handleUpdateSubscriber(subscriberDB, subscriberRequest));

    }

    @GetMapping("/email")
    @ApiMessage("Gửi email thành công!")
    public String sendEmail() {
        this.subscriberService.handleSendSubscribersEmailJobs();
        return "OK";
    }

}
