package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.datasource.Converter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
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
    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private Converter<List<ParamFlowRuleEntity>, String> converter;


    @Override
    public void publish(String app, List<ParamFlowRuleEntity> rules) throws Exception {
        String zkPath = ZookeeperConfigUtils.getFlowParamRuleZkPath(app);
        Stat stat = zkClient.checkExists().forPath(zkPath);
        if (stat == null) {
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(zkPath, null);
        }
        byte[] data = null;
        if (!CollectionUtils.isEmpty(rules)) {
            data = converter.convert(rules).getBytes(StandardCharsets.UTF_8);
        }
        zkClient.setData().forPath(zkPath, data);
    }

}
