/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.wizzer.iot.mqtt.server.store.subscribe;

import cn.hutool.core.util.StrUtil;
import cn.wizzer.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import cn.wizzer.iot.mqtt.server.common.subscribe.SubscribeStore;
import cn.wizzer.iot.mqtt.server.store.cache.SubscribeNotWildcardCache;
import cn.wizzer.iot.mqtt.server.store.cache.SubscribeWildcardCache;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订阅存储服务
 */
@IocBean
public class SubscribeStoreService implements ISubscribeStoreService {

    @Inject
    private SubscribeNotWildcardCache subscribeNotWildcardCache;

    @Inject
    private SubscribeWildcardCache subscribeWildcardCache;

    @Override
    public void put(String topicFilter, SubscribeStore subscribeStore) {
        if (StrUtil.contains(topicFilter, '#') || StrUtil.contains(topicFilter, '+')) {
            ConcurrentHashMap<String, SubscribeStore> map =
                    subscribeWildcardCache.containsKey(topicFilter) ? subscribeWildcardCache.get(topicFilter) : new ConcurrentHashMap<String, SubscribeStore>();
            map.put(subscribeStore.getClientId(), subscribeStore);
            subscribeWildcardCache.put(topicFilter, map);
        } else {
            ConcurrentHashMap<String, SubscribeStore> map =
                    subscribeNotWildcardCache.containsKey(topicFilter) ? subscribeNotWildcardCache.get(topicFilter) : new ConcurrentHashMap<String, SubscribeStore>();
            map.put(subscribeStore.getClientId(), subscribeStore);
            subscribeNotWildcardCache.put(topicFilter, map);
        }
    }

    @Override
    public void remove(String topicFilter, String clientId) {
        if (StrUtil.contains(topicFilter, '#') || StrUtil.contains(topicFilter, '+')) {
            if (subscribeWildcardCache.containsKey(topicFilter)) {
                ConcurrentHashMap<String, SubscribeStore> map = subscribeWildcardCache.get(topicFilter);
                if (map.containsKey(clientId)) {
                    map.remove(clientId);
                    if (map.size() > 0) {
                        subscribeWildcardCache.put(topicFilter, map);
                    } else {
                        subscribeWildcardCache.remove(topicFilter);
                    }
                }
            }
        } else {
            if (subscribeNotWildcardCache.containsKey(topicFilter)) {
                ConcurrentHashMap<String, SubscribeStore> map = subscribeNotWildcardCache.get(topicFilter);
                if (map.containsKey(clientId)) {
                    map.remove(clientId);
                    if (map.size() > 0) {
                        subscribeNotWildcardCache.put(topicFilter, map);
                    } else {
                        subscribeNotWildcardCache.remove(topicFilter);
                    }
                }
            }
        }
    }

    @Override
    public void removeForClient(String clientId) {
        subscribeNotWildcardCache.all().forEach((entry, map) -> {
            if (map.containsKey(clientId)) {
                map.remove(clientId);
                if (map.size() > 0) {
                    subscribeNotWildcardCache.put(entry, map);
                } else {
                    subscribeNotWildcardCache.remove(entry);
                }
            }
        });
        subscribeWildcardCache.all().forEach((entry, map) -> {
            if (map.containsKey(clientId)) {
                map.remove(clientId);
                if (map.size() > 0) {
                    subscribeWildcardCache.put(entry, map);
                } else {
                    subscribeWildcardCache.remove(entry);
                }
            }
        });
    }

    @Override
    public List<SubscribeStore> search(String topic) {
        List<SubscribeStore> subscribeStores = new ArrayList<SubscribeStore>();
        if (subscribeNotWildcardCache.containsKey(topic)) {
            ConcurrentHashMap<String, SubscribeStore> map = subscribeNotWildcardCache.get(topic);
            Collection<SubscribeStore> collection = map.values();
            List<SubscribeStore> list = new ArrayList<SubscribeStore>(collection);
            subscribeStores.addAll(list);
        }
        subscribeWildcardCache.all().forEach((topicFilter, map) -> {
            if (StrUtil.split(topic, '/').size() >= StrUtil.split(topicFilter, '/').size()) {
                List<String> splitTopics = StrUtil.split(topic, '/');
                List<String> spliteTopicFilters = StrUtil.split(topicFilter, '/');
                String newTopicFilter = "";
                for (int i = 0; i < spliteTopicFilters.size(); i++) {
                    String value = spliteTopicFilters.get(i);
                    if (value.equals("+")) {
                        newTopicFilter = newTopicFilter + "+/";
                    } else if (value.equals("#")) {
                        newTopicFilter = newTopicFilter + "#/";
                        break;
                    } else {
                        newTopicFilter = newTopicFilter + splitTopics.get(i) + "/";
                    }
                }
                newTopicFilter = StrUtil.removeSuffix(newTopicFilter, "/");
                if (topicFilter.equals(newTopicFilter)) {
                    Collection<SubscribeStore> collection = map.values();
                    List<SubscribeStore> list = new ArrayList<SubscribeStore>(collection);
                    subscribeStores.addAll(list);
                }
            }
        });
        return subscribeStores;
    }

}
