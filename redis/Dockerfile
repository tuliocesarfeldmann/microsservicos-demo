FROM redis:latest

# Coloca o arquivo de configuração no container
COPY redis.conf /etc/redis/redis.conf

# Executa passando o arquivo de conf
CMD ["redis-server", "/etc/redis/redis.conf"]
