package com.example.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.jms.Session;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

@Service
public class TopicReaderWriter {

    private final static Logger LOGGER = LoggerFactory.getLogger(TopicReaderWriter.class);

    private final static String TOPIC = "g3topic";
    private final static String TOPIC_SUB = "g3topic-sub";

    private final AtomicInteger count;
    private final JmsTemplate jmsTemplateTopic;

    public TopicReaderWriter(JmsTemplate jmsTemplateTopic) {
        this.count = new AtomicInteger(0);
        this.jmsTemplateTopic = jmsTemplateTopic;
    }

    @Scheduled(fixedRate = 3000)
    public void send() {
        int count = this.count.incrementAndGet();
        LOGGER.info(format("-> %d", count));
        jmsTemplateTopic.send(TOPIC, (Session session) -> session.createTextMessage(String.valueOf(count)));
    }

    @JmsListener(destination = TOPIC, subscription = TOPIC_SUB, containerFactory = "jmsListenerContainerFactoryTopic")
    public void receive(String message) {
        LOGGER.info(format("<- %s", message));
    }
}
