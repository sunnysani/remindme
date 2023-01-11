package com.lawtk.lawtkschedulerbe.repository;

import com.lawtk.lawtkschedulerbe.entity.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobInfoRepository extends JpaRepository<JobInfo, Integer> {
    JobInfo findByJobName(String jobName);

}
