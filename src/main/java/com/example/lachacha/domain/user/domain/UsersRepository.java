package com.example.lachacha.domain.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsersRepository extends JpaRepository<Users, Long>
{
    boolean existsByUsername(String username);

    Users findByUsername(String username);

    @Query("SELECT COUNT(u) FROM Users u WHERE u.interests LIKE %:interest%")
    long countByInterestsContaining(@Param("interest") String interest);
}
