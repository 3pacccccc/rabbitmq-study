package com.bfxy.rabbitmq.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface Barista {

    String INPUT_CHANNEL = "input_channel";

    @Input(Barista.INPUT_CHANNEL)
    SubscribableChannel loginput();
}
