package com.aws.carepoint.service;

import com.aws.carepoint.mapper.PlanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanService {

    @Autowired
    private final PlanMapper planMapper;

}
