package com.example.Project_Jobhunter.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Project_Jobhunter.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

}
