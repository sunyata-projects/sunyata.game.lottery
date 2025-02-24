package org.sunyata.game.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * Created by leo on 17/5/22.
 */
@Component
@Configuration
public class MessageClient {
    final Logger logger = LoggerFactory.getLogger(MessageClient.class);

    @Value("${yde.rabbit.queue:ydeQueue}")
    public String rabbitQueue;
    //
    @Value("${yde.rabbit.exchange:ydeExchange}")
    public String rabbitExchange;
//    @Autowired
//    RabbitTemplate rabbitTemplate;

    public void asyncSaveGameModel(GameModel gameModel) {
//        ComplexMessageInfo messageInfo = new ComplexMessageInfo();
//        messageInfo.setJobInfoType(MessageInfoType.SyncGameModel);
//        messageInfo.setBodyJsonString(Json.encode(gameModel));
//        String messageInfoString = Json.encode(messageInfo);
//        try {
//            rabbitTemplate.convertAndSend(rabbitExchange, rabbitQueue, messageInfoString);
//        } catch (AmqpException aex) {
//            logger.info(String.format("[job发送异常][%s]_%s_%s", MessageInfoType.SyncGameModel.getValue(),
//                    MessageInfoType.SyncGameModel, messageInfoString));
//            throw aex;
//        }
    }
}
