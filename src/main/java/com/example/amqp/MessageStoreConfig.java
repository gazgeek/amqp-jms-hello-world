package com.example.amqp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageStoreConfig {

    @Value("${protocol}")
    private String protocol;

    @Value("${host}")
    private String host;

    @Value("${username}")
    private String username;

    @Value("${password}")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrlString() {
        return String.format("%1s://%2s?amqp.idleTimeout=3600000", protocol, host);
    }
}
