package com.example.lachacha.domain.user.domain;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long>
{
    boolean existsByUsername(String username);

    Users findByUsername(String username);

    @Query("SELECT COUNT(u) FROM Users u WHERE u.interestJobCategory LIKE %:jobCategory% and u.interestJobValue Like %:jobValue%")
    long countByInterestsContaining(@Param("jobCategory") String jobCategory, @Param("jobValue") String jobValue);

    long countByJobCategory(String jobCategory);

    List<Users> findByIsParticipateTrue();
}
