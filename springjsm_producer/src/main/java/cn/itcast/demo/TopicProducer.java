package cn.itcast.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * springJsm 订阅模式 消息生产者
 */
@Component
public class TopicProducer {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination topicTextDestination;

    /**
     * 发送文本消息
     * @param text
     */
    public void sendTestMessage(final String text)throws Exception{
        jmsTemplate.send(topicTextDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                return session.createTextMessage(text);
            }
        });
    }
}
