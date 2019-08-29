package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author holder
 * @date 2019/6/12
 * @Description:
 */
@Configuration
public class ZookeeperConfig {

    private final Logger logger = LoggerFactory.getLogger(ZookeeperConfig.class);

    private static final int DEFAULT_ZK_SESSION_TIMEOUT = 30000;
    private static final int DEFAULT_ZK_CONNECTION_TIMEOUT = 10000;
    private static final int RETRY_TIMES = 3;
    private static final int SLEEP_TIME = 3000;

    @Value("${zookeeper.remoteAddress}")
    public String remoteAddress;



    @Bean
    public Converter<List<FlowRuleEntity>, String> flowRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<FlowRuleEntity>> flowRuleEntityDecoder() {
        return s -> JSON.parseArray(s, FlowRuleEntity.class);
    }

    @Bean
    public Converter<List<ParamFlowRuleEntity>, String> paramFlowRuleEntityEncoder() {
        //禁止循环引用 SerializerFeature.DisableCircularReferenceDetect
        return arr -> JSON.toJSONString(arr,SerializerFeature.DisableCircularReferenceDetect);
    }

    @Bean
    public Converter<String, List<ParamFlowRuleEntity>> paramFlowRuleEntityDecoder() {
        //使用 fastJson 进行反序列化
        ObjectMapper objectMapper = new ObjectMapper();
        return str -> {
            try {
                System.out.println("paramFlowRuleEntityDecoder >>>"+str);
                return objectMapper.readValue(str,
                        objectMapper.getTypeFactory().constructParametricType(List.class, ParamFlowRuleEntity.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        };

        /*return s -> JSON.parseArray(s, ParamFlowRuleEntity.class);*/
    }

    public static void main(String[] args) throws IOException {
        List<ParamFlowRuleEntity> rules = new ArrayList<>();
        ParamFlowRuleEntity paramFlowRuleEntity = new ParamFlowRuleEntity();
        ParamFlowRule paramFlowRule;
        paramFlowRule = JSON.parseObject("{\"burstCount\":0,\"clusterMode\":false,\"controlBehavior\":0,\"count\":7.0,\"durationInSec\":1,\"grade\":1,\"limitApp\":\"default\",\"maxQueueingTimeMs\":0,\"paramFlowItemList\":[{\"classType\":\"int\",\"count\":3,\"object\":\"1\"}],\"paramIdx\":7,\"resource\":\"getAdService222\"}",ParamFlowRule.class);
        paramFlowRuleEntity
                .setApp("ssp-consumer")
                .setGmtCreate(new Date())
                .setGmtModified(new Date())
                .setIp("192.168.2.166")
                .setPort(9006)
                .setRule(paramFlowRule)
                .setId(6888509021491200L);

        rules.add(paramFlowRuleEntity);
//        String prostr = "[{\"app\":\"ssp-consumer\",\"clusterMode\":false,\"count\":40.0,\"gmtCreate\":1562039169559,\"gmtModified\":1562047002676,\"grade\":1,\"id\":6920807238471680,\"ip\":\"192.168.2.177\",\"limitApp\":\"default\",\"paramFlowItemList\":[{\"classType\":\"int\",\"count\":3,\"object\":\"1\"},{\"classType\":\"int\",\"count\":6,\"object\":\"2\"}],\"paramIdx\":0,\"port\":9007,\"resource\":\"com.xj.rtb.common.dubbo.provider.IBidderProvider:helloWorld()\",\"rule\":{\"burstCount\":0,\"clusterMode\":false,\"controlBehavior\":0,\"count\":40.0,\"durationInSec\":1,\"grade\":1,\"limitApp\":\"default\",\"maxQueueingTimeMs\":0,\"paramFlowItemList\":[{\"$ref\":\"$[0].paramFlowItemList[0]\"},{\"$ref\":\"$[0].paramFlowItemList[1]\"}],\"paramIdx\":0,\"resource\":\"com.xj.rtb.common.dubbo.provider.IBidderProvider:helloWorld()\"}}]";

        String string = JSON.toJSONString(rules,SerializerFeature.DisableCircularReferenceDetect);
        System.out.println("provider"+string);

        List<ParamFlowRule> flowRuleEntities = JSON.parseArray(string, ParamFlowRule.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<ParamFlowRuleEntity> list = objectMapper
                .readValue(string, objectMapper.getTypeFactory().constructParametricType(List.class, ParamFlowRuleEntity.class));

        list.forEach(System.out::println);



        /*System.out.println(JSON.parseArray(s,ParamFlowRuleEntity.class));*/
    }

    @Bean(destroyMethod = "close")
    public CuratorFramework zkClient() {
        int sessionTimeout = DEFAULT_ZK_SESSION_TIMEOUT;
        int connectionTimeout = DEFAULT_ZK_CONNECTION_TIMEOUT;
        /*if (properties.getSessionTimeout() > 0) {
            sessionTimeout = properties.getSessionTimeout();
        }
        if (properties.getConnectionTimeout() > 0) {
            connectionTimeout = properties.getConnectionTimeout();
        }*/

        CuratorFramework zkClient = CuratorFrameworkFactory.newClient(remoteAddress,
                sessionTimeout, connectionTimeout,
                new ExponentialBackoffRetry(SLEEP_TIME, RETRY_TIMES));
        zkClient.start();

        logger.info("Initialize zk client CuratorFramework, connectString={}, sessionTimeout={}, connectionTimeout={}, retry=[sleepTime={}, retryTime={}]",
                remoteAddress, sessionTimeout, connectionTimeout, SLEEP_TIME, RETRY_TIMES);
        return zkClient;
    }






}
