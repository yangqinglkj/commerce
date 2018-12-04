package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class itemSearchListener implements MessageListener{

    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();//JSON字符串
            System.out.println("监听到消息:"+text);
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
            if (!CollectionUtils.isEmpty(itemList)) {
                itemSearchService.importList(itemList);
                System.out.println("导入solr索引库");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
