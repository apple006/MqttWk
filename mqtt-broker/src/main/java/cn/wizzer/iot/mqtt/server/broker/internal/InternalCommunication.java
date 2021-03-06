/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.wizzer.iot.mqtt.server.broker.internal;

import cn.wizzer.iot.mqtt.server.broker.config.BrokerProperties;
import cn.wizzer.iot.mqtt.server.broker.service.KafkaService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息转发，基于kafka
 */
@IocBean
public class InternalCommunication {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalCommunication.class);
    @Inject
    private BrokerProperties brokerProperties;
    @Inject
    private KafkaService kafkaService;

    public void internalSend(InternalMessage internalMessage) {
        try {
            kafkaService.send(internalMessage);//内部是异步调用
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
