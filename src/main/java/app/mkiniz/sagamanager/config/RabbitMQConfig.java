package app.mkiniz.sagamanager.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange-input}")
    private String exchangeInput;

    @Value("${rabbitmq.exchange-output}")
    private String exchangeOutput;

    @Value("${rabbitmq.saga-queue}")
    private String sagaQueue;

    @Value("${rabbitmq.dlx-exchange}")
    private String dlxExchangeInput;

    @Value("${rabbitmq.dlx-queue}")
    private String dlqQueue;

    @Value("${rabbitmq.dlx-routing-key}")
    private String dlqRoutingKey;

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Dead Letter Setup
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxExchangeInput);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqQueue).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(dlqRoutingKey);
    }

    @Bean
    public HeadersExchange inputExchange() {
        return new HeadersExchange(exchangeInput);
    }

    @Bean
    public HeadersExchange outputExchange() {
        return new HeadersExchange(exchangeOutput);
    }

    @Bean
    public Queue sagaQueue() {
        return QueueBuilder.durable(sagaQueue).build();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        // Garante que o admin tentará se auto-inicializar
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public ApplicationRunner initializeQueues(RabbitAdmin rabbitAdmin) {
        return args -> {
            // Força a declaração de todos os beans de Queue, Exchange e Binding
            rabbitAdmin.initialize();
        };
    }
}
