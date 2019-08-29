package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import java.io.File;

/**
 * @author holder
 * @date 2019/6/12
 * @Description:
 */
public class ZookeeperConfigUtils {

    public static final String GROUP_ID = "SENTINEL_GROUP";

    private static final String FLOW_RULE_DATA_ID_POSTFIX = "-flow-rules";

    private static final String FLOW_PARAM_RULE_DATA_ID_POSTFIX = "-param-flow-rules";

    private static final String SERVER_ID_PREFIX = "server-id-";


    /**
     *
     * @param app name
     * @return zk path
     */
    public static String getFlowRuleZkPath(String app){
        return File.separator + GROUP_ID + File.separator + app + FLOW_RULE_DATA_ID_POSTFIX;
    }

    /**
     *
     * @param app name
     * @return zk param rule path
     */
    public static String getFlowParamRuleZkPath(String app){
        return File.separator + GROUP_ID + File.separator + app + FLOW_PARAM_RULE_DATA_ID_POSTFIX;
    }

    /**
     *
     * @param serverId
     * @return SENTINEL_GROUP serverId path
     */
    public static String getServerIdZkPath(int serverId){
        return File.separator + GROUP_ID + File.separator + SERVER_ID_PREFIX + serverId ;
    }

}
