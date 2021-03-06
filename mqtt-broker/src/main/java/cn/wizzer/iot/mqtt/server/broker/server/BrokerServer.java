/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.wizzer.iot.mqtt.server.broker.server;

import cn.wizzer.iot.mqtt.server.broker.config.BrokerProperties;
import cn.wizzer.iot.mqtt.server.broker.handler.BrokerHandler;
import cn.wizzer.iot.mqtt.server.broker.listener.MqttServerListener;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ssl.SslConfig;
import org.tio.server.ServerGroupContext;
import org.tio.server.TioServer;

import java.io.InputStream;

/**
 * Netty启动Broker
 */
@IocBean
public class BrokerServer implements ServerFace {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServer.class);
    @Inject
    private BrokerProperties brokerProperties;
    @Inject("refer:$ioc")
    private Ioc ioc;

    private SslConfig sslConfig;

    public void start() throws Exception {
        LOGGER.info("Initializing {} MQTT Broker ...", "[" + brokerProperties.getId() + "]");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("keystore/server.jks");
        sslConfig = SslConfig.forServer(inputStream, null, brokerProperties.getSslPassword());
        mqttServer();
//		websocketServer();
//		LOGGER.info("MQTT Broker {} is up and running. Open SSLPort: {} WebSocketSSLPort: {}", "[" + brokerProperties.getId() + "]", brokerProperties.getSslPort(), brokerProperties.getWebsocketSslPort());
    }


    public void stop() {
//		LOGGER.info("Shutdown {} MQTT Broker ...", "[" + brokerProperties.getId() + "]");
//		bossGroup.shutdownGracefully();
//		bossGroup = null;
//		workerGroup.shutdownGracefully();
//		workerGroup = null;
//		channel.closeFuture().syncUninterruptibly();
//		channel = null;
//		websocketChannel.closeFuture().syncUninterruptibly();
//		websocketChannel = null;
//		LOGGER.info("MQTT Broker {} shutdown finish.", "[" + brokerProperties.getId() + "]");
    }

    @IocBean(name = "serverGroupContext")
    public ServerGroupContext getServerGroupContext() throws Exception {
        ServerGroupContext serverGroupContext = new ServerGroupContext(ioc.get(BrokerHandler.class), ioc.get(MqttServerListener.class));
        serverGroupContext.setName(brokerProperties.getId());
        serverGroupContext.setHeartbeatTimeout(brokerProperties.getKeepAlive());
        if (brokerProperties.getSslEnabled())
            serverGroupContext.setSslConfig(sslConfig);
        return serverGroupContext;
    }

    @IocBean(name = "tioServer")
    public TioServer getTioServer(@Inject ServerGroupContext serverGroupContext) {
        return new TioServer(serverGroupContext);
    }

    private void mqttServer() throws Exception {
        ioc.getByType(TioServer.class).start(brokerProperties.getHost(), brokerProperties.getPort());
    }

    private void websocketServer() throws Exception {
//		ServerBootstrap sb = new ServerBootstrap();
//		sb.group(bossGroup, workerGroup)
//			.channel(brokerProperties.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
//			// handler在初始化时就会执行
//			.handler(new LoggingHandler(LogLevel.INFO))
//			.childHandler(new ChannelInitializer<SocketChannel>() {
//				@Override
//				protected void initChannel(SocketChannel socketChannel) throws Exception {
//					ChannelPipeline channelPipeline = socketChannel.pipeline();
//					// Netty提供的心跳检测
//					channelPipeline.addFirst("idle", new IdleStateHandler(0, 0, brokerProperties.getKeepAlive()));
//					// Netty提供的SSL处理
//					SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
//					sslEngine.setUseClientMode(false);        // 服务端模式
//					sslEngine.setNeedClientAuth(false);        // 不需要验证客户端
//					channelPipeline.addLast("ssl", new SslHandler(sslEngine));
//					// 将请求和应答消息编码或解码为HTTP消息
//					channelPipeline.addLast("http-codec", new HttpServerCodec());
//					// 将HTTP消息的多个部分合成一条完整的HTTP消息
//					channelPipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
//					// 将HTTP消息进行压缩编码
//					channelPipeline.addLast("compressor ", new HttpContentCompressor());
//					channelPipeline.addLast("protocol", new WebSocketServerProtocolHandler(brokerProperties.getWebsocketPath(), "mqtt,mqttv3.1,mqttv3.1.1", true, 65536));
//					channelPipeline.addLast("mqttWebSocket", new MqttWebSocketCodec());
//					channelPipeline.addLast("decoder", new MqttDecoder());
//					channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
//					channelPipeline.addLast("broker", new BrokerHandler(protocolProcess));
//				}
//			})
//			.option(ChannelOption.SO_BACKLOG, brokerProperties.getSoBacklog())
//			.childOption(ChannelOption.SO_KEEPALIVE, brokerProperties.isSoKeepAlive());
//		websocketChannel = sb.bind(brokerProperties.getWebsocketSslPort()).sync().channel();
    }

}
