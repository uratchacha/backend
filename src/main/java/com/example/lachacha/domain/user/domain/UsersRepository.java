package com.example.lachacha.domain.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long>
{
    boolean existsByUsername(String username);

    Users findByUsername(String username);
}
