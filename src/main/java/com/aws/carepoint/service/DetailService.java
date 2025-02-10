package com.aws.carepoint.service;

import com.aws.carepoint.dto.DetailDto;
import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.DetailMapper;
import com.aws.carepoint.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
public class DetailService {

    private final DetailMapper detailMapper;
    private final UserMapper userMapper;

    @Autowired
    public DetailService(DetailMapper detailMapper, UserMapper userMapper) {
        this.detailMapper = detailMapper;

        this.userMapper = userMapper;
    }

  public void insertDetail(DetailDto detailDto) {     // 회원가입
        detailMapper.insertDetail(detailDto);
      System.out.println("DB에 저장할 데이터: " + detailDto);
        System.out.println("회원가입 DB 저장 완료!");
    }

    public Map<String, Object> getUserFullInfo(int userPk) {
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




}
