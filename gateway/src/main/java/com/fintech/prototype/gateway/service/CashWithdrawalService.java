package com.fintech.prototype.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.prototype.gateway.dto.CashWithdrawalRequestDTO;
import com.fintech.prototype.gateway.dto.CashWithdrawalResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class CashWithdrawalService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String REPLY_EXCHANGE = "reply-cash-withdrawal-rabbit";

    private final String REPLY_QUEUE = "reply-cash-withdrawal-rabbit";

    private final Integer TTL = 40000;

    public CashWithdrawalResponseDTO cashWithdrawalSend(CashWithdrawalRequestDTO cashWithdrawal, String identifier) throws Exception {

        log.info("Sending cash withdrawal... | identifier: {}", identifier);

        String payload = objectMapper.writeValueAsString(cashWithdrawal);

        sendMessage(payload, identifier);

        String replyQueue = REPLY_EXCHANGE + "-" + identifier;

        Object response = rabbitTemplate.receiveAndConvert(
                replyQueue,
                TTL
        );

        if (isNull(response)) {
            throw new RuntimeException("Timeout waiting for response on queue " + replyQueue);
        } else {
            String jsonResponse = response.toString();

            if (jsonResponse.contains("error")) {
                throw new Exception(jsonResponse);
            }

            return objectMapper.readValue(jsonResponse, CashWithdrawalResponseDTO.class);
        }
    }

    private void sendMessage(String payload, String identifier) {

        subscribeResponse(identifier);

        Map<String, Object> headers = new HashMap<>();
        headers.put("IDENTIFIER", identifier);

        rabbitTemplate.convertAndSend(
                "exchange-cash-withdrawal-rabbit",
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
            Map<String, Object> args = new HashMap<>();
            args.put("x-message-ttl", TTL);
            args.put("x-expires", TTL);

            /*
             * Arg1 - Nome da fila
             * Arg2 - Fila exclusiva para a conexão que a criou
             * Arg3 - Deletar quando não houver consumidores
             * Arg4 - Argumentos
             */
            channel.queueDeclare(replyQueue, false, true, true, args);

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
