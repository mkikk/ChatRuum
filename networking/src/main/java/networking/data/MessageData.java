package networking.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Data for a sent message
 */
public class MessageData implements Serializable {
    public final String text;
    public final String senderName;
    public final String sendTime;

    public MessageData(String text, String senderName, String sendTime) {
        this.text = text;
        this.senderName = senderName;
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "MessageData{" +
                "text='" + text + '\'' +
                ", senderName='" + senderName + '\'' +
                ", sendTime=" + sendTime +
                '}';
    }
}
