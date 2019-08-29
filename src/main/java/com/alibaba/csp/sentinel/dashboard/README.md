# sentinel-dashboard-Production-transformation
## sentinel控制台生产环境改造
>Sentinel 控制台是流量控制、熔断降级规则统一配置和管理的入口，它为用户提供了机器自发现、簇点链路自发现、监控、规则配置等功能。在 Sentinel 控制台上，我们可以配置规则并实时查看
流量控制效果。由于原有的规则默认是通过API方式发送给客户端，并且保存在内存中，只要控制台重启或者客户端重启，规则数据就会丢失，这是不可能用在生产环境的，所以需要进行规则持久化改造。

>Sentinel 由阿里巴巴开源，github链接：https://github.com/alibaba/Sentinel

## 生产环境
>Dubbo + Zookeeper

## 改造过程



