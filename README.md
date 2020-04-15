# Java Learning
这里为了学习 Java Feature、理解 Java 源码、提高 Java 基础而建立的 Repo.

在方法名的上方使用注释记笔记，不同话题之间用三级标题 `回车+###` 标注

特殊名词、变量、方法名，使用 `「」` 标注主要这样输入比较方便，我输入法启用了中文半角符号，但在IDEA无效

## 语言基础

### 语言特性
[ 反射 ](https://github.com/imzhizi/java-learning/blob/master/src/main/java/com/imzhizi/javalearning/%E8%AF%AD%E8%A8%80%E5%9F%BA%E7%A1%80/%E5%8F%8D%E5%B0%84.java )

[ 注解 ](https://github.com/imzhizi/java-learning/blob/master/src/main/java/com/imzhizi/javalearning/%E8%AF%AD%E8%A8%80%E5%9F%BA%E7%A1%80/%E6%B3%A8%E8%A7%A3.java )

### 数据结构/容器类
在所有基本容器中, HashMap 肯定是Java最具有代表性的一个类, 它的实现使用了大量优化技巧, 很多非常精妙, 另一方面也是 LinkedHashMqp、HashSet 的实现基础

[ 基本数据结构 ](https://github.com/imzhizi/java-learning/blob/master/src/main/java/com/imzhizi/javalearning/%E8%AF%AD%E8%A8%80%E5%9F%BA%E7%A1%80/%E5%9F%BA%E6%9C%AC%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84.java )

[ 高级数据结构 ](https://github.com/imzhizi/java-learning/blob/master/src/main/java/com/imzhizi/javalearning/%E8%AF%AD%E8%A8%80%E5%9F%BA%E7%A1%80/%E9%AB%98%E7%BA%A7%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84.java )


## 多线程

### 并发组件
为了实现对多线程更好地控制, Java 提供了一系列API, 尤其是以AQS为基础的ReentrantLock、Condition、CountDownLatch、CyclicBarrier、Semaphore, 满足了不同场景下的锁需求.

同时我画了几张关于它们的流程图, [ 不过需要保存下来自行用浏览器打开 ](https://github.com/imzhizi/java-learning/blob/master/Java%E5%B9%B6%E5%8F%91%E7%BB%84%E4%BB%B6.html )

### 多线程题目

关于多线程, 我选择了一些[ 常见的多线程题目, 选择多种方法进行实现 ](https://github.com/imzhizi/java-learning/tree/master/src/main/java/com/imzhizi/javalearning/%E5%BA%94%E7%94%A8/%E5%A4%9A%E7%BA%BF%E7%A8%8B ).


## Spring
[ Spring AOP ](https://github.com/imzhizi/java-learning/blob/master/src/main/java/com/imzhizi/javalearning/spring/AOPTest.java )

[ AspectJ ](https://github.com/imzhizi/java-learning/blob/master/src/main/java/com/imzhizi/javalearning/spring/AspectTest.java )

[ Spring DI ](https://github.com/imzhizi/java-learning/blob/master/src/main/java/com/imzhizi/javalearning/spring/DITest.java )

[ Spring Event ](https://github.com/imzhizi/java-learning/blob/master/src/main/java/com/imzhizi/javalearning/spring/EventTest.java )


## 其他

### 编译期注解与Lombok
Lombok 提供了一系列非常有用的注解, 比如说为实体类自动添加 getter()、setter()、toString() 等方法. 与传统注解不同, Lombok利用了所谓的编译期注解, 通过编译期对抽象语法树(AST)进行修改达到自动添加方法的目的. 在本项目中我实现了一个简单的 [ 类 Lombok 注解, 能够为类添加 getter() 方法 ](https://github.com/imzhizi/java-learning/tree/master/src/main/java/com/imzhizi/javalearning/DevKit/lombok ).