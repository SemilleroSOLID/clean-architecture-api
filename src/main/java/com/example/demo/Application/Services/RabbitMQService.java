package com.example.demo.Application.Services;

import com.example.demo.Application.RabbitInterfaces.IAmqpConfig;
import com.example.demo.Application.RabbitInterfaces.IAmqpExchange;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitMQService {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

    private IAmqpConfig config;
    private Connection connection;
    private Channel channel;
    private boolean finished;

    public RabbitMQService(IAmqpConfig config) {
        this.connection = null;
        this.finished = false;
        this.config = config;
    }

    public void init(){
        try{
            this.connect();
            this.config.getExchanges().forEach(this::addExchange);
        }catch (Exception e){

        }

    }

    public void addExchange(IAmqpExchange exchange){
        try {
            this.channel.exchangeDeclare(exchange.getName(), exchange.getType());
        }catch (Exception e){
           logger.error("Error agregando el intercambiador", e);
        }
    }

    public void addQueue(String queueName){
        try{
            this.channel.queueDeclare(queueName, true, false, false, null);
        }catch (Exception e){
            logger.error("Error agragando la cola", e);
        }
    }

    public void addBinding(String exchangeName, String queueName, String key){
        try{
            this.channel.queueBind(queueName, exchangeName, key);
        }catch (Exception e){
            logger.error("Error agregando el intermediario", e);
        }
    }

    public void connect() throws IOException, TimeoutException {
        if (this.connection != null) {
            logger.info("[connect] looks like broker is already connected, skip");
            return;
        }
        if (this.config.getConnection().getAmqpUrl() == null) {
            logger.error("rabbitMQ host name is undefined! unable to connect");
            return;
        }
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(this.config.getConnection().getAmqpUrl());
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            this.connection.addShutdownListener(cause -> {
                if (cause.isHardError()) {
                    logger.error("[Broker-AMQP] connection error", cause.getMessage());
                }
                this.connection = null;
            });
        } catch (Exception e) {
            logger.error("ERROR! [connect] on trying to connect to " + this.config.getConnection().getAmqpUrl(), e);
            this.connection = null;
            if (!this.finished) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                connect();
            }
        }
    }

    public void close() throws IOException, TimeoutException {
        if (this.channel != null) {
            this.channel.close();
        }
        if (this.connection != null) {
            this.connection.close();
        }
        this.finished = true;
        logger.info("The connection was closed");
    }

    public void send(String exchange, String key, Object msg, boolean noAck, Map<String, Object> options) throws IOException {
        AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder()
                .deliveryMode(2) // Persistent
                .timestamp(new Date())
                .contentEncoding("utf-8")
                .contentType("application/json")
                .headers(Map.of("source", exchange + ":" + key));

        if (options != null) {
            propsBuilder.headers(options);
        }

        AMQP.BasicProperties props = propsBuilder.build();
        String msgToSend = (msg instanceof String) ? (String) msg : new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(msg);
        this.channel.basicPublish(exchange, key, props, msgToSend.getBytes(StandardCharsets.UTF_8));
        logger.info("Message sent: " + msgToSend);
    }
}
