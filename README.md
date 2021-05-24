# G-Chat
## 1.构建思路
### 1.1 UDP广播搜索再建立TCP连接
![](https://raw.githubusercontent.com/GZK0329/picture_store/master/UDPSearch.png)
### 1.2 NIO优化 线程优化
原本的
![](https://raw.githubusercontent.com/GZK0329/picture_store/master/NIO%E4%BC%98%E5%8C%96%E5%89%8D.png)
![](https://raw.githubusercontent.com/GZK0329/picture_store/master/NIO%E4%BC%98%E5%8C%96%E5%90%8E.png)
### 1.3 消息粘包消息半包的解决
### 1.4 文件传输的实现 消息分片支持文件快传
### 1.5 心跳包维持连接
## 2.测试分析
