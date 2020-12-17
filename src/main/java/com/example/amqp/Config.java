package com.example.amqp;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.policy.JmsDefaultRedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.jms.ConnectionFactory;

@Configuration
@EnableScheduling
public class Config {

    @Value("${spring.application.name}")
    private String clientId;

    @Bean
    public ConnectionFactory jmsConnectionFactory(MessageStoreConfig details) {
        JmsConnectionFactory jmsConnectionFactory = new JmsConnectionFactory(details.getUrlString());
        jmsConnectionFactory.setUsername(details.getUsername());
        jmsConnectionFactory.setPassword(details.getPassword());
        jmsConnectionFactory.setClientID(clientId);

        JmsDefaultRedeliveryPolicy redeliveryPolicy = new JmsDefaultRedeliveryPolicy();
        redeliveryPolicy.setMaxRedeliveries(10);
        jmsConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);

        return new CachingConnectionFactory(jmsConnectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplateQueue(ConnectionFactory jmsConnectionFactory) {
        return createJmsTemplate(jmsConnectionFactory, false);
    }

    @Bean
    public JmsTemplate jmsTemplateTopic(ConnectionFactory jmsConnectionFactory) {
        return createJmsTemplate(jmsConnectionFactory, true);
    }

    private JmsTemplate createJmsTemplate(ConnectionFactory jmsConnectionFactory, boolean pubSubDomain) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setPubSubDomain(pubSubDomain);
        jmsTemplate.setConnectionFactory(jmsConnectionFactory);
        return jmsTemplate;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setSessionTransacted(true);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactoryTopic(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setSubscriptionDurable(true);
        factory.setSessionTransacted(true);
        return factory;
    }

}
