# Sistema de Microsserviços Financeiro

Ssistema de microsserviços que simula requisições financeiras, projetado para processar consultas e saques simulados. O sistema é composto por cinco aplicações que trabalham de forma integrada utilizando RabbitMQ para mensageria e Redis para armazenamento temporário de dados.

## Estrutura do Sistema

```
.
├── ./consulta
├── ./saque
├── ./gateway
├── ./redis
└── ./rabbitmq
```

### 1. ./consulta
- Aplicação Spring Boot.
- Inscreve-se na fila do RabbitMQ `queue-consult-rabbit`.
- Processa as requisições de consulta.

### 2. ./saque
- Aplicação Spring Boot.
- Inscreve-se na fila do RabbitMQ `queue-cash-withdrawal-rabbit`.
- Processa as requisições de saque.

### 3. ./gateway
- Aplicação Spring Boot.
- Processa as requisições REST de entrada e publica nas respectivas filas.
- Endpoints:
  - `/consult`: Entrada para as requisições de consulta.
  - `/{identifier}/cash-withdrawal`: Transações de efetivação simulada de saque.

### 4. ./redis
- Banco de dados em memória.
- Armazena as informações temporárias entre as requisições de consulta e saque.

### 5. ./rabbitmq
- Sistema de mensageria.
- Intermedia a comunicação entre os microsserviços.

## Como Executar o Projeto

### Requisitos

- Docker e Docker Compose instalados.
- Java 17+ e Maven instalados.

### Passos para Execução

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/tuliocesarfeldmann/sistema-microsservicos-demo.git
   cd sistema-microsservicos-demo
   ```

2. **Inicie os containers com Docker Compose:**
   ```bash
   docker-compose up -d
   ```

3. **Verifique se todos os containers estão em execução:**
   ```bash
   docker ps
   ```

4. **Acesse a aplicação:**
   - Consulta: `http://localhost:9991/consult`
   - Saque: `http://localhost:9991/{identifier}/cash-withdrawal`

