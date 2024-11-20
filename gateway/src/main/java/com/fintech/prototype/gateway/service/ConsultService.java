package com.fintech.prototype.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.prototype.gateway.dto.ConsultRequestDTO;
import com.fintech.prototype.gateway.dto.ConsultResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class ConsultService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String REPLY_EXCHANGE = "reply-consult-rabbit";

    private final String REPLY_QUEUE = "reply-consult-rabbit";

    public ConsultResponseDTO consultSend(ConsultRequestDTO consult) throws JsonProcessingException {

        log.info("Sending consult... | identifier: {}", consult.getIdentifier());

        String payload = objectMapper.writeValueAsString(consult);

        sendMessage(payload, consult.getIdentifier());

        String replyQueue = REPLY_EXCHANGE + "-" + consult.getIdentifier();

        Object response = rabbitTemplate.receiveAndConvert(
                replyQueue,
                30000
        );

        if (isNull(response)) {
            throw new RuntimeException("Timeout waiting for response on queue " + replyQueue);
        } else {
            String jsonResponse = response.toString();
            return objectMapper.readValue(jsonResponse, ConsultResponseDTO.class);
        }
    }

    private void sendMessage(String payload, String identifier) {

        subscribeResponse(identifier);

        Map<String, Object> headers = new HashMap<>();
        headers.put("IDENTIFIER", identifier);

        rabbitTemplate.convertAndSend(
                "exchange-consult-rabbit",
                "",
                payload, message -> {
                    Map<String, Object> headerMap = message.getMessageProperties().getHeaders();
                    headerMap.putAll(headers);
                    return message;
                });
    }

    private void subscribeResponse(String identifier) {

        String replyExchange = REPLY_EXCHANGE + "-" + identifier;
        String replyQueue = REPLY_QUEUE + "-" + identifier;

        rabbitTemplate.execute(channel -> {
            /*
             * Arg1 - Nome da fila
             * Arg2 - Fila exclusiva para a conexão que a criou
             * Arg3 - Deletar quando não houver consumidores
             * Arg4 - Argumentos
             */
            channel.queueDeclare(replyQueue, false, true, true, null);

            /*
             * Arg1 - Nome da exchange
             * Arg2 - Tipo (direct, fanout, topic, header)
             * Arg3 - Persiste após a reinicialização do broker
             * Arg4 - Deletar quando não houver filas vinculadas
             * Arg5 - Argumentos
             */
            channel.exchangeDeclare(replyExchange, "direct", false, true, null);

            channel.queueBind(replyQueue, replyExchange, "");

            return null;
        });

        log.info("Listening on reply queue: {}", replyQueue);
    }

}
