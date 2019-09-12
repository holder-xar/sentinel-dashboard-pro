package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.datasource.Converter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author holder
 * @date 2019/6/12
 * @Description:
 */
@Component("flowRuleZookeeperPublisher")
public class FlowRuleZookeeperPublisher implements DynamicRulePublisher<List<FlowRuleEntity>> {

    private final Logger log = LoggerFactory.getLogger(FlowRuleZookeeperPublisher.class);

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private Converter<List<FlowRuleEntity>, String> converter;

    @Override
    public void publish(String app, List<FlowRuleEntity> rules) throws Exception {
        String zkPath = ZookeeperConfigUtils.getFlowRuleZkPath(app);
        log.warn(">>>> FlowRuleZookeeperPublisher: create zk node with [{}]",zkPath);
        Stat stat = zkClient.checkExists().forPath(zkPath);
        if (stat == null) {
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(zkPath, null);
        }
        byte[] data = null;
        String json = "";
        if (!CollectionUtils.isEmpty(rules)) {
            json = converter.convert(rules);
            data = json.getBytes(StandardCharsets.UTF_8);
        }
        log.warn(">>>> FlowRuleZookeeperPublisher: convert FlowRule [{}]",json);
        zkClient.setData().forPath(zkPath, data);
    }

}
