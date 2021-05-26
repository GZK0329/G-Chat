<<<<<<< develop
# 实时通讯工具G-Chat实现

## 项目背景
G-Chat-

## 项目架构

## 项目流程图

## 核心技术点

## 核心功能

## 性能分析
### 多线程优化

## 安全性分析

## 进一步完善计划
=======
# G-Chat

[1.构建思路](#jump1)

[2.使用方式](#jump2)

[3.测试分析](#jump3)


## 项目结构
```
├─lib-clink
│  ├─src
│  │  ├─main
│  │  │  ├─java
│  │  │  │  ├─box
│  │  │  │  ├─connect
│  │  │  │  ├─impl
│  │  │  │  │  └─async
│  │  │  │  └─utils
│  │  │  └─resources
│  │  └─test
│  │      └─java
│  └─target
│      ├─classes
│      │  ├─box
│      │  ├─connect
│      │  ├─impl
│      │  │  └─async
│      │  └─utils
│      └─generated-sources
│          └─annotations
├─sample-client
│  ├─src
│  │  ├─main
│  │  │  ├─java
│  │  │  │  ├─client
│  │  │  │  └─META-INF
│  │  │  └─resources
│  │  └─test
│  │      └─java
│  └─target
│      ├─classes
│      │  └─client
│      └─generated-sources
│          └─annotations
├─sample-config
│  ├─src
│  │  ├─main
│  │  │  ├─java
│  │  │  │  └─constants
│  │  │  └─resources
│  │  └─test
│  │      └─java
│  └─target
│      ├─classes
│      │  └─constants
│      └─generated-sources
│          └─annotations
└─sample-server
    ├─src
    │  ├─main
    │  │  ├─java
    │  │  │  ├─META-INF
    │  │  │  └─server
    │  │  │      └─handle
    │  │  └─resources
    │  └─test
    │      └─java
    └─target
        ├─classes
        │  └─server
        │      └─handle
        └─generated-sources
            └─annotations
```          


<span id="jump1"></span>

## 1.构建思路
### 1.1 UDP广播搜索再建立TCP连接

<div align="center">
<img src=https://raw.githubusercontent.com/GZK0329/picture_store/master/UDPSearch.png />
</div>

### 1.2 NIO优化线程优化

阻塞IO,在大量客户端连接时候，频繁地增加线程,会造成大量的资源消耗
<div align="center">
<img src=https://raw.githubusercontent.com/GZK0329/picture_store/master/NIO%E4%BC%98%E5%8C%96%E5%89%8D.png />
</div>

非阻塞IO，通过一个线程维护大量客户端的注册
<div align="center">
<img src=https://raw.githubusercontent.com/GZK0329/picture_store/master/NIO%E4%BC%98%E5%8C%96%E5%90%8E.png />
</div>

### 1.3 消息粘包消息不完整的解决
#### 消息粘包
TCP是可靠的，保证数据的完整性，本身不存在粘包。这里的消息粘包是指数据处理而不是传输时候的粘包。

<div align="center">
<img src=https://raw.githubusercontent.com/GZK0329/picture_store/master/%E6%95%B0%E6%8D%AE%E6%8E%A5%E6%94%B6.png />
</div>

#### 消息不完整
TCP是可靠的，保证数据的完整性，本身也不存在丢失的可能，也是指的数据处理层面的消息不完整。
指的是如缓冲区空间不够而误以为已经接收完成，造成的数据不完整，或者因为网络等原因，数据包还未完整送达，而接收方误以为已经传输完成，进行了后续消息处理的步骤，从而造成消息不完整。

<div align="center">
<img src=https://raw.githubusercontent.com/GZK0329/picture_store/master/%E6%B6%88%E6%81%AF%E4%B8%8D%E5%AE%8C%E6%95%B4.png />
</div>

#### 解决思路
对数据包进行分析，构建一个数据头部，其中包含要接收数据的大小，特征等，数据头 + 数据体 = 完整数据包

### 1.4 文件传输的实现实现流传输 消息分片支持文件快传
### 流传输改造
byte[]可支持传输的文件大小有限，改为stream传输以支持大文件的传输。
### 消息分片


### 1.5 心跳包维持连接

<span id="jump2"></span>
## 2.使用方式

<span id="jump3"></span>
## 3.测试分析
>>>>>>> master
