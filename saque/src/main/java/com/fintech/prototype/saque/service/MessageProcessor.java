package com.fintech.prototype.saque.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.prototype.saque.dto.CashWithdrawalRequestDTO;
import com.fintech.prototype.saque.dto.CashWithdrawalResponseDTO;
import com.fintech.prototype.saque.dto.CommomDataDTO;
import com.fintech.prototype.saque.dto.ErrorResponseDTO;
import com.fintech.prototype.saque.repository.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class MessageProcessor {

    private final String SUBSCRIBE_QUEUE = "queue-cash-withdrawal-rabbit";

    private final String RESPONSE_EXCHANGE = "reply-cash-withdrawal-rabbit";

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    private final RedisRepository redisRepository;

    public MessageProcessor(RabbitTemplate rabbitTemplate,
                            ObjectMapper objectMapper, RedisRepository redisRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.redisRepository = redisRepository;
    }

    @RabbitListener(queues = SUBSCRIBE_QUEUE)
    public void processMessage(Message message) throws JsonProcessingException {

        String body = new String(message.getBody());
        Map<String, Object> requestHeaders = message.getMessageProperties().getHeaders();

        log.info("Received message. body: {} | headers: {}", body, requestHeaders);

        try {

            String identifier = (String) requestHeaders.get("IDENTIFIER");

            log.info("Starting convert request body to ConsultRequestDTO... identifier: {}", identifier);

            CashWithdrawalRequestDTO consultRequest = objectMapper.readValue(body, CashWithdrawalRequestDTO.class);

            log.info("Starting consult processing... identifier: {}", identifier);

            CashWithdrawalResponseDTO response = process(consultRequest, identifier);

            log.info("Starting response processing... identifier: {}", identifier);

            sentResponse(requestHeaders, response);

        } catch (Exception e) {
            handleException(e, requestHeaders);
        }

    }

    private void sentResponse(Map<String, Object> requestHeaders, CashWithdrawalResponseDTO response) {

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

    private CashWithdrawalResponseDTO process(CashWithdrawalRequestDTO request, String identifier) throws Exception {

        Optional<CommomDataDTO> cacheData = redisRepository.findById(identifier);

        if (cacheData.isEmpty()) {
            throw new Exception("Cache data not found for identifier: " + identifier);
        }

        log.info("Found consult by identifier: {} | body: {}", identifier, objectMapper.writeValueAsString(cacheData.get()));

        cacheData.get().setAmount(request.getAmount());
        cacheData.get().setPassword(request.getPassword());

        // Após ter os dados das duas requisições (consulta + saque) faria o processamento da transação...
        // efetivarTransacao(cacheData);

        return CashWithdrawalResponseDTO.builder()
                .status("OK")
                .build();
    }
}
