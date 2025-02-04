package com.aws.carepoint.repository;

import com.aws.carepoint.vo.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // JpaRepository<User, Integer>를 상속하면 기본적인 CRUD 기능이 자동으로 생성
    Optional<User> findByUserid(String userid); // 아이디로 회원 조회
}
