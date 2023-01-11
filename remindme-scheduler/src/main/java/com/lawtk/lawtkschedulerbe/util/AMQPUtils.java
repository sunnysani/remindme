package com.lawtk.lawtkschedulerbe.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AMQPUtils {

    private static RabbitTemplate rabbitTemplate;
    private static AmqpAdmin amqpAdmin;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AMQPUtils(RabbitTemplate rabbitTemplate, AmqpAdmin amqpAdmin) {
        AMQPUtils.rabbitTemplate = rabbitTemplate;
        AMQPUtils.amqpAdmin = amqpAdmin;

    }

    public static RabbitTemplate getRabbitTemplate() {
        return AMQPUtils.rabbitTemplate;
    }

    public static AmqpAdmin getAmqpAdmin() {
        return AMQPUtils.amqpAdmin;
    }

    public static ObjectMapper getObjectMapper() {
        return AMQPUtils.objectMapper;
    }

}
