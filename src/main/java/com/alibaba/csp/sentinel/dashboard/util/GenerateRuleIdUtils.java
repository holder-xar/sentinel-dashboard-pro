package com.alibaba.csp.sentinel.dashboard.util;

import com.alibaba.csp.sentinel.dashboard.rule.zookeeper.ZookeeperConfigUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * @author holder
 * @date 2019/6/13
 * @Description: 通过zookeeper+Snowflake算法 生成分布式的全局唯一ID
 */
@Component
public class GenerateRuleIdUtils {

    private final Logger logger = LoggerFactory.getLogger(GenerateRuleIdUtils.class);

    private static int serverId = 1;

    private static IdWorker idWorker;

    private static final int MAX_SERVER_NUM = 999;

    @Autowired
    private CuratorFramework zkClient;

    @PostConstruct
    private void init() {
        /**
         *
         * 功能描述: 连接zookeeper中心，获取服务器id
         * CreateMode.EPHEMERAL 临时节点，session结束时销毁
         */
        for (int i = 1; i < MAX_SERVER_NUM; i++) {

            String path = ZookeeperConfigUtils.getServerIdZkPath(i);
            Stat stat = null;
            try {
                stat = zkClient.checkExists().forPath(path);
                if (null != stat) {
                    continue;
                }
                serverId = i;
                zkClient.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(path, String.valueOf(i).getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                logger.error("zkClient init throws exception to [{}]",e.getMessage());
            }
            logger.warn(">>> server id init:with serverId [{}]",i);
            break;
        }

    }

    public static long nextId(){
        if(null != idWorker){
           return idWorker.nextId();
        }
        // lazy loading
        idWorker = new IdWorker(serverId,1L);
        return idWorker.nextId();
    }


}
