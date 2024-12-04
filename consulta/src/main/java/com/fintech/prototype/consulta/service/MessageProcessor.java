package com.fintech.prototype.consulta.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.prototype.consulta.dto.ConsultRequestDTO;
import com.fintech.prototype.consulta.dto.ConsultResponseDTO;
import com.fintech.prototype.consulta.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MessageProcessor {

    private final String SUBSCRIBE_QUEUE = "queue-consult-rabbit";

    private final String RESPONSE_EXCHANGE = "reply-consult-rabbit";

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    public MessageProcessor(RabbitTemplate rabbitTemplate,
                            ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = SUBSCRIBE_QUEUE)
    public void processMessage(Message message) throws JsonProcessingException {

        String body = new String(message.getBody());
        Map<String, Object> requestHeaders = message.getMessageProperties().getHeaders();

        log.info("Received message. body: {} | headers: {}", body, requestHeaders);

        try {

            log.info("Starting convert request body to ConsultRequestDTO... identifier: {}", requestHeaders.get("IDENTIFIER"));

            ConsultRequestDTO consultRequest = objectMapper.readValue(body, ConsultRequestDTO.class);

            log.info("Starting consult processing... identifier: {}", requestHeaders.get("IDENTIFIER"));

            ConsultResponseDTO response = process(consultRequest);

            log.info("Starting response processing... identifier: {}", requestHeaders.get("IDENTIFIER"));

            sentResponse(requestHeaders, response);

        } catch (Exception e) {
            handleException(e, requestHeaders);
        }

    }

    private void sentResponse(Map<String, Object> requestHeaders, ConsultResponseDTO response) {

        sentRabbimq(requestHeaders, response);

    }

    private void handleException(Exception e, Map<String, Object> headers) {

        log.error("Ocorreu um erro não esperado. identifier: {} | message: {}", headers.get("IDENTIFIER"), e.getMessage());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .error("ERROR INTERNO")
                .details(e.getMessage())
                .build();

        sentRabbimq(headers, errorResponse);

    }

    private void sentRabbimq(Map<String, Object> headers, Object response) {
        try {

            String payload = objectMapper.writeValueAsString(response);

            rabbitTemplate.convertAndSend(
                    RESPONSE_EXCHANGE + "-" + headers.get("IDENTIFIER"),
                    "",
                    payload
            );

            log.info("Response has been sent: {} | identifier: {}", payload, headers.get("IDENTIFIER"));
        } catch (Throwable exception) {
            log.error("Erro ao enviar resposta: {}", exception.getMessage());
        }
    }

    private ConsultResponseDTO process(ConsultRequestDTO request) {

        return ConsultResponseDTO.builder()
                .name("Teste")
                .document("03900000000")
                .build();
    }
}
