package org.cjavellana.repositories;

import org.cjavellana.models.UserInfo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoRepository extends PagingAndSortingRepository<UserInfo, Integer> {

    List<UserInfo> findAll();

}
