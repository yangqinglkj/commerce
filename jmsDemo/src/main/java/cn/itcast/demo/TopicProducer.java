package cn.itcast.demo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 发布订阅模式
 * 消息生产者
 */
public class TopicProducer {
    public static void main(String[] args)throws Exception {
        //1.创建连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
        //2.创建连接对象
        Connection connection = connectionFactory.createConnection();
        //3.启动连接
        connection.start();
        //4.获取session  参数1：是否启动事务  参数2：消息的确认模式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5.创建主题对象
        Topic topic = session.createTopic("text-topic");
        //6.创建消息生产者对象
        MessageProducer producer = session.createProducer(topic);
        //7.创建消息对象
        TextMessage textMessage = session.createTextMessage("欢迎来到品优购");
        //8.发送消息
        producer.send(textMessage);
        //9.关闭资源
        producer.close();
        session.close();
        connection.close();

    }
}
