package com.example.Project_Jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project_Jobhunter.domain.Subscriber;

public interface SubscriberRepository extends JpaRepository<Subscriber, Integer> {

    boolean existsByEmail(String email);
}
