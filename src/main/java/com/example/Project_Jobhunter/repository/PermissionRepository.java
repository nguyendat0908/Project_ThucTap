package com.example.Project_Jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.Project_Jobhunter.domain.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Integer>,JpaSpecificationExecutor<Permission>
{

    boolean existsByName(String name);

    List<Permission> findByIdIn(List<Integer> id);
}
