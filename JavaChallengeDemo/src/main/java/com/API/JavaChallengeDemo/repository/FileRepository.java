package com.API.JavaChallengeDemo.repository;

import com.API.JavaChallengeDemo.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//crud operations
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByFileName(String fileName);
}
