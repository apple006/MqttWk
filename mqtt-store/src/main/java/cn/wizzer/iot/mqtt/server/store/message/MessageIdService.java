/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.wizzer.iot.mqtt.server.store.message;

import cn.wizzer.iot.mqtt.server.common.message.IMessageIdService;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class MessageIdService implements IMessageIdService {

    private final int MIN_MSG_ID = 1;

    private final int MAX_MSG_ID = 65535;

    private final int lock = 0;

    @Inject
    private RedisService redisService;

    private int nextMsgId = MIN_MSG_ID - 1;

    @Override
    public int getNextMessageId() {
        try {
            do {
                nextMsgId++;
                if (nextMsgId > MAX_MSG_ID) {
                    nextMsgId = MIN_MSG_ID;
                }
            } while (redisService.get("mqttwk:messageid:" + String.valueOf(nextMsgId)) != null);
            redisService.set("mqttwk:messageid:" + String.valueOf(nextMsgId), String.valueOf(nextMsgId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nextMsgId;
    }

    @Override
    public void releaseMessageId(int messageId) {
        try {
            redisService.del("mqttwk:messageid:" + String.valueOf(messageId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
