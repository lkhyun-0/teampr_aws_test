package com.aws.carepoint.vo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") // 실제 DB 테이블명
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_pk;
    private int auth_level;
    private int social_login_status;
    private String userid;
    private String username;
    private String userpwd;
    private String usernick;
    private String phone;
    private String email;
    private String join_date;
    private String update_date;
    private int del_status;
    private String delDate;
}
