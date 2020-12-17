/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class QueueReaderWriter {

    private final static Logger LOGGER = LoggerFactory.getLogger(QueueReaderWriter.class);

    private final static String QUEUE = "g3queue";

    private final AtomicInteger count;
    private final JmsTemplate jmsTemplateQueue;

    public QueueReaderWriter(JmsTemplate jmsTemplateQueue) {
        this.count = new AtomicInteger(100);
        this.jmsTemplateQueue = jmsTemplateQueue;
    }

    @Scheduled(fixedRate = 3000)
    public void send() {
        int count = this.count.incrementAndGet();
        LOGGER.info(format("-> %d", count));
        jmsTemplateQueue.send(QUEUE, (Session session) -> session.createTextMessage(String.valueOf(count)));
    }

    @JmsListener(destination = QUEUE)
    public void receive(String message) {
        LOGGER.info(format("<- %s", message));
    }

}
