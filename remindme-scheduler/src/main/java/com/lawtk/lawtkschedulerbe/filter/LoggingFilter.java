package com.lawtk.lawtkschedulerbe.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lawtk.lawtkschedulerbe.util.JWTUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LoggingFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private String key;

    @Value("${log.service}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);

        var requestBody = getStringValue(requestWrapper.getContentAsByteArray(),
                request.getCharacterEncoding());
        var responseBody = getStringValue(responseWrapper.getContentAsByteArray(),
                response.getCharacterEncoding());
        responseWrapper.copyBodyToResponse();

        postToLogService(request, response, requestBody, responseBody);

    }

    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
        try {
            return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void postToLogService(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String requestBody,
                                  String responseBody){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> payload = new HashMap<>();

        payload.put("time", getDateNow());
        payload.put("service", "service-scheduler");
        payload.put("code", response.getStatus());

        if(response.getStatus() >= 200 && response.getStatus()<400){
            payload.put("type", 1);
        }else if(response.getStatus() >= 400 && response.getStatus()<500){
            payload.put("type", 3);
        }else{
            payload.put("type", 2);
        }

        var jwtUtils = new JWTUtils(key);
        var token = request.getHeader("Authorization");
        try{
            var username = jwtUtils.getUserUsername(token);
            payload.put("user", username);
        }catch (Exception e){
            payload.put("user", "guest");
        }

        payload.put("host", request.getServerName());
        payload.put("method", request.getMethod());
        payload.put("path", request.getRequestURI());
        payload.put("requestBody", requestBody);
        payload.put("response", responseBody);
        HttpEntity<?> requestLog = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(url, requestLog, String.class);
    }

    public String getDateNow(){
        Date dateNow = new Date();
        var timeZone = TimeZone.getTimeZone("GMT+7");
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.uuuuuu", Locale.UK);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(dateNow);
    }

}