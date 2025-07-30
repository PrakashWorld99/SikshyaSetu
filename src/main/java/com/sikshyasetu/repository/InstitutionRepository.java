package com.sikshyasetu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sikshyasetu.entity.InstitutionMaster;

@Repository
public interface InstitutionRepository extends JpaRepository<InstitutionMaster, Long> {
}

