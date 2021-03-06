# G-Chat

[1.构建思路与测试分析](#jump1)


## 项目结构


<span id="jump1"></span>

## 1.构建思路
### 1.1 UDP广播搜索再建立TCP连接

<div align="center">
<img src=https://static-gzk-personal.oss-cn-beijing.aliyuncs.com/G-chat/README_img/UDPSearch.png />
</div>

### 1.2 NIO优化线程优化

阻塞IO,在大量客户端连接时候，频繁地增加线程,会造成大量的资源消耗
<div align="center">
<img src=https://static-gzk-personal.oss-cn-beijing.aliyuncs.com/G-chat/README_img/NIO%E4%BC%98%E5%8C%96%E5%89%8D.png />
</div>

非阻塞IO，通过一个线程维护大量客户端的注册
<div align="center">
<img src=https://static-gzk-personal.oss-cn-beijing.aliyuncs.com/G-chat/README_img/NIO%E4%BC%98%E5%8C%96%E5%90%8E.png />
</div>


线程优化前 创建2000客户端连接服务器，服务需要创建4000+线程来处理每个客户端的读取与写入，很多线程长时间处于阻塞状态，非常低效
<div align="center">
<img src=https://static-gzk-personal.oss-cn-beijing.aliyuncs.com/G-chat/README_img/server%E7%AB%AF%E6%9C%AA%E4%BC%98%E5%8C%96.PNG />
</div>
通过线程池优化后，创建2000客户端连接服务器
<div align="center">
<img src=https://static-gzk-personal.oss-cn-beijing.aliyuncs.com/G-chat/README_img/server%E7%AB%AF%E4%BC%98%E5%8C%96%E5%90%8E.PNG />
</div>

### 1.3 消息粘包消息不完整的解决
#### 消息粘包
TCP是可靠的，保证数据的完整性，本身不存在粘包。这里的消息粘包是指数据处理而不是传输时候的粘包。

<div align="center">
<img src=https://static-gzk-personal.oss-cn-beijing.aliyuncs.com/G-chat/README_img/%E6%95%B0%E6%8D%AE%E6%8E%A5%E6%94%B6.png />
</div>

#### 消息不完整
TCP是可靠的，保证数据的完整性，本身也不存在丢失的可能，也是指的数据处理层面的消息不完整。
指的是如缓冲区空间不够而误以为已经接收完成，造成的数据不完整，或者因为网络等原因，数据包还未完整送达，而接收方误以为已经传输完成，进行了后续消息处理的步骤，从而造成消息不完整。

<div align="center">
<img src=https://static-gzk-personal.oss-cn-beijing.aliyuncs.com/G-chat/README_img/%E6%B6%88%E6%81%AF%E4%B8%8D%E5%AE%8C%E6%95%B4.png />
</div>

#### 解决思路
对数据包进行分析，构建一个数据头部，其中包含要接收此数据的大小，特征等，数据头 + 数据体 = 完整数据包 

### 1.4 文件传输的实现实现流传输 消息分片支持文件快传
#### 文件传输实现
文件传输成功进行MD5校验无误
<div align="center">
<img src=https://static-gzk-personal.oss-cn-beijing.aliyuncs.com/G-chat/README_img/%E6%96%87%E4%BB%B6%E6%88%90%E5%8A%9F%E4%BC%A0%E8%BE%93.PNG />
</div>

## 后续待完善计划

### 大文件消息分片

### 心跳包维持连接

### 语音传输的支持

