package com.aws.carepoint.service;

import com.aws.carepoint.dto.DetailDto;
import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.DetailMapper;
import com.aws.carepoint.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
public class DetailService {

    private final DetailMapper detailMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DetailService(DetailMapper detailMapper, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.detailMapper = detailMapper;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void insertDetail(DetailDto detailDto) {     // 회원가입
        detailMapper.insertDetail(detailDto);
/*        System.out.println("DB에 저장할 데이터: " + detailDto);
        System.out.println("회원가입 DB 저장 완료!");*/
    }

    public Map<String, Object> getUserFullInfo(int userPk) {        // 상세정보 + 기본정보 다 담아오는 !!!
        UsersDto userInfo = userMapper.getUserById(userPk);
        DetailDto userDetail = detailMapper.getUserDetail(userPk);

        if (userInfo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (userDetail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User details not found");
        }

        Map<String, Object> userFullInfo = new HashMap<>();
        userFullInfo.put("userInfo", userInfo);
        userFullInfo.put("userDetail", userDetail);


        return userFullInfo;
    }


    @Transactional
    public boolean updateUserInfo(UsersDto usersDto) {
            // 비밀번호가 존재하는 경우 암호화
            if (usersDto.getUserPwd() != null && !usersDto.getUserPwd().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(usersDto.getUserPwd());
                usersDto.setUserPwd(encodedPassword); // 암호화된 비밀번호 저장
                System.out.println("✅ 암호화된 비밀번호: " + encodedPassword);
            }


        return userMapper.updateUserInfo(usersDto) > 0;
    }

    @Transactional
    public boolean updateDetailInfo(DetailDto detailDto) {
        return detailMapper.updateDetailInfo(detailDto) > 0;
    }


}
