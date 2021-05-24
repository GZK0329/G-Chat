# G-Chat
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

### 1.3 消息粘包消息半包的解决


### 1.4 文件传输的实现实现流传输 消息分片支持文件快传

### 1.5 心跳包维持连接

## 2.测试分析
