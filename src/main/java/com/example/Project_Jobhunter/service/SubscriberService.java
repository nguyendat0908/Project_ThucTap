package com.example.Project_Jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Project_Jobhunter.domain.Job;
import com.example.Project_Jobhunter.domain.Skill;
import com.example.Project_Jobhunter.domain.Subscriber;
import com.example.Project_Jobhunter.repository.JobRepository;
import com.example.Project_Jobhunter.repository.SkillRepository;
import com.example.Project_Jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final EmailService emailService;
    private final JobRepository jobRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            EmailService emailService, JobRepository jobRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.emailService = emailService;
        this.jobRepository = jobRepository;
    }

    public Subscriber handleCreateSubscriber(Subscriber subscriber) {
        if (subscriber.getSkills() != null) {
            List<Integer> arrSkillIds = subscriber.getSkills().stream().map(item -> item.getId()).toList();
            List<Skill> skills = this.skillRepository.findByIdIn(arrSkillIds);
            subscriber.setSkills(skills);
        }

        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber handleUpdateSubscriber(Subscriber subscriberDB, Subscriber subscriberRequest) {
        if (subscriberRequest.getSkills() != null) {
            List<Integer> arrSkillIds = subscriberRequest.getSkills().stream().map(item -> item.getId()).toList();
            List<Skill> skills = this.skillRepository.findByIdIn(arrSkillIds);
            subscriberDB.setSkills(skills);
        }

        return this.subscriberRepository.save(subscriberDB);
    }

    public boolean handleCheckExistByEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber handleGetSuById(int id) {
        Optional<Subscriber> subscriberOptional = this.subscriberRepository.findById(id);
        if (subscriberOptional.isPresent()) {
            return subscriberOptional.get();
        }

        return null;
    }

    public void handleSendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        // List<ResEmailJob> arr = listJobs.stream().map(
                        // job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailJobs(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                listJobs);
                    }
                }
            }
        }
    }

}
