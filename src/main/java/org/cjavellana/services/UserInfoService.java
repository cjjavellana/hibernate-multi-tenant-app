package org.cjavellana.services;


import org.cjavellana.models.UserInfo;
import org.cjavellana.repositories.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Transactional(readOnly = true, propagation = Propagation.NEVER)
    public List<UserInfo> findAll() {
        return userInfoRepository.findAll();
    }

}
