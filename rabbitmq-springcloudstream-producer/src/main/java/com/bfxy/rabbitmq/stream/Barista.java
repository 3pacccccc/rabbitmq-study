package com.bfxy.rabbitmq.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface Barista {

    String OUTPUT_CHANNEL = "output_channel";

    @Output(Barista.OUTPUT_CHANNEL)
    MessageChannel logoutput();
}
