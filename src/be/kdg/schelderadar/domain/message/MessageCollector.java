package be.kdg.schelderadar.domain.message;

import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.out.store.MessageStorage;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: michaelkees
 * Date: 01/11/15
 */
public class MessageCollector {
    private Collection<PositionMessage> positionMessages = new ArrayList<>();

    private MessageStorage msgStorage;

    public MessageCollector(MessageStorage msgStorage) {
       this.msgStorage = msgStorage;
    }

    public Collection<PositionMessage> getPositionMessages() {
        return positionMessages;
    }

    public void setPositionMessages(Collection<PositionMessage> positionMessages) {
        this.positionMessages = positionMessages;
    }

    public void addPostitionMessage(PositionMessage message) {
        this.positionMessages.add(message);
        msgStorage.saveMessage(message);
    }

    public void clear(){
        positionMessages.clear();
    }
}
