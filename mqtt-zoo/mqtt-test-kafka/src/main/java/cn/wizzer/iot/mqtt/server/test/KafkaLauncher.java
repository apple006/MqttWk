package cn.wizzer.iot.mqtt.server.test;

import cn.hutool.core.util.HexUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.nutz.boot.NbApp;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.Modules;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by wizzer on 2018
 */
@IocBean(create = "init")
@Modules(packages = "cn.wizzer.iot")
public class KafkaLauncher {
    private static final Log log = Logs.get();
    @Inject
    private PropertiesProxy conf;

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.setMainPackage("cn.wizzer.iot");
        nb.run();
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        for (String key : conf.keySet()) {
            if (key.startsWith("mqttwk.broker.kafka.")) {
                properties.put(key.substring("mqttwk.broker.kafka.".length()), conf.get(key));
            }
        }
        return properties;
    }

    public void init() {
        KafkaConsumer kafkaConsumer=new KafkaConsumer(getProperties());
        //kafka消费消息,接收MQTT发来的消息
        kafkaConsumer.subscribe(Arrays.asList(conf.get("mqttwk.broker.kafka.producer.topic")));
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(500);
            log.debug("records:::" + Json.toJson(records));
            for (ConsumerRecord<String, String> record : records) {
                log.debugf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                log.debug(new String(HexUtil.decodeHex(record.value())));
            }
        }

    }
}
