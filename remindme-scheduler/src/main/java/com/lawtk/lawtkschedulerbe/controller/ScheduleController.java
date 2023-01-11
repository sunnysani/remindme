package com.lawtk.lawtkschedulerbe.controller;

import com.lawtk.lawtkschedulerbe.entity.Schedule;
import com.lawtk.lawtkschedulerbe.repository.ScheduleRepository;
import com.lawtk.lawtkschedulerbe.service.ScheduleService;
import com.lawtk.lawtkschedulerbe.util.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping(value = "api")
public class ScheduleController {
    private final ScheduleService service;
    private final ScheduleRepository scheduleRepository;
    private final JWTUtils jwtUtils;

    private String getUsername(String token) {
        if (token.equals(""))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authentication token provided");

        var username = "";
        try {
            username = jwtUtils.getUserUsername(token);
        } catch (ExpiredJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
        } catch (MalformedJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token malformed");
        } catch (UnsupportedJwtException | SignatureException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        return username;
    }

    private Schedule getSchedule(String token, Integer id) {
        var scheduleOpt = scheduleRepository.findById(id);
        if (scheduleOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var schedule = scheduleOpt.get();

        if (!schedule.getUsername().equals(getUsername(token))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return schedule;
    }

    @Autowired
    public ScheduleController(ScheduleService service, ScheduleRepository scheduleRepository, JWTUtils jwtUtils){
        this.service = service;
        this.scheduleRepository = scheduleRepository;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(value = "/scheduler")
    public Schedule saveSchedule(
            @RequestBody Schedule schedule,
            @RequestHeader(value = "Authorization", defaultValue = "") String token){
        schedule.setUsername(getUsername(token));
        return service.addSchedule(schedule);
    }

    @GetMapping(value = "/scheduler/all")
    public List<Schedule> getAllSchedule(
            @RequestHeader(value = "Authorization", defaultValue = "") String token
    ){
        return service.retrieveAllSchedule(getUsername(token));
    }

    @GetMapping(value = "/scheduler/{id}")
    public Schedule getScheduleById(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", defaultValue = "") String token
    ){
        return getSchedule(token, id);
    }

    @PutMapping(value = "/scheduler/{id}")
    public Schedule updateSchedule(
            @PathVariable Integer id,
            @RequestBody Schedule inputSchedule,
            @RequestHeader(value = "Authorization", defaultValue = "") String token
    ){
        var schedule = getSchedule(token, id);

        schedule.setScheduleTime(inputSchedule.getScheduleTime());
        schedule.setScheduleInterval(inputSchedule.getScheduleInterval());
        schedule.setActivityName(inputSchedule.getActivityName());
        return service.updateSchedule(schedule);
    }

    @DeleteMapping(value = "/scheduler/{id}")
    public void deleteSchedule(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", defaultValue = "") String token
    ){
        var schedule = getSchedule(token, id);

        try {
            service.deleteSchedule(schedule);
        } catch (NoSuchElementException ignored) {

        }
    }
}
