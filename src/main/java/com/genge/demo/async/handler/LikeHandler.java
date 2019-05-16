package com.genge.demo.async.handler;

import com.genge.demo.async.EventHandler;
import com.genge.demo.async.EventModel;
import com.genge.demo.async.EventType;
import com.genge.demo.model.Message;
import com.genge.demo.model.User;
import com.genge.demo.service.MessageService;
import com.genge.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandler(EventModel model) {
        Message message = new Message();
        message.setFromId(3);
        message.setToId(model.getActorId());
        User user = userService.getUser(model.getActorId());
        message.setContent("用户"+user.getName()+"赞了你的资讯，http://127.0.0.1:8080/news"
        +model.getEntityId());
        message.setCreateDate(new Date());
        messageService.addMessage(message);
        System.out.println("Liked");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
