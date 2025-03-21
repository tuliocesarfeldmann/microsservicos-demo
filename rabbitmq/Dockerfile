FROM rabbitmq:3.12.2-management-alpine

ENV RABBIT_DEFINITIONS rabbit_definitions.json

RUN apk update
RUN apk upgrade

ADD /rabbit/conf/rabbitmq.conf /etc/rabbitmq/rabbitmq.conf
ADD /rabbit/conf/rabbit_definitions.json /etc/rabbitmq/rabbit_definitions.json

CMD ["rabbitmq-server"]
