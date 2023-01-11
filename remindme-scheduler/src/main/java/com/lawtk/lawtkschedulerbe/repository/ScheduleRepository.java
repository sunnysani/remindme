package com.lawtk.lawtkschedulerbe.repository;

import com.lawtk.lawtkschedulerbe.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findAllByUsername(String username);
}
