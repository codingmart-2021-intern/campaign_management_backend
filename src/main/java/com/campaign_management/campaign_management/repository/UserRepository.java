package com.campaign_management.campaign_management.repository;

import java.util.List;
import com.campaign_management.campaign_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("FROM User WHERE email=:email")
    User findByEmail(@Param("email") String email);

    @Query("FROM User WHERE verificationCode=:code")
    User findByVerificationCode(String code);

    @Query("FROM User WHERE otp=:otp")
    User findByOtp(String otp);

    @Query("FROM User WHERE id=:id")
    User findByUserId(int id);

    @Query("FROM User WHERE phone=:phone")
    User findByPhoneNumber(Long phone);

    @Query(value = "SELECT * FROM user_tbl WHERE role!='admin'", nativeQuery = true)
    List<User> findAllUsers();
}
