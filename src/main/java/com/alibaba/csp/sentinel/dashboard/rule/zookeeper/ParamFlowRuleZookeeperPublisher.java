package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
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
 * @date 2019/7/1
 * @Description:
 */
@Component("paramFlowRuleZookeeperPublisher")
public class ParamFlowRuleZookeeperPublisher implements DynamicRulePublisher<List<ParamFlowRuleEntity>> {

    private final Logger log = LoggerFactory.getLogger(ParamFlowRuleZookeeperPublisher.class);

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private Converter<List<ParamFlowRuleEntity>, String> converter;


    @Override
    public void publish(String app, List<ParamFlowRuleEntity> rules) throws Exception {
        String zkPath = ZookeeperConfigUtils.getFlowParamRuleZkPath(app);
        Stat stat = zkClient.checkExists().forPath(zkPath);
        if (stat == null) {
            log.warn(">>>> ParamFlowRuleZookeeperPublisher: create zk node with [{}]",zkPath);
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(zkPath, null);
        }
        byte[] data = null;
        String json = "";
        if (!CollectionUtils.isEmpty(rules)) {
            json = converter.convert(rules);
            data = json.getBytes(StandardCharsets.UTF_8);
        }
        log.warn(">>>> ParamFlowRuleZookeeperPublisher: convert ParamFlowRule [{}]",json);
        zkClient.setData().forPath(zkPath, data);
    }

}
