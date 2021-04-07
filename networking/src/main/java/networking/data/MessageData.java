package networking.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Data for a sent message
 */
public class MessageData implements Serializable {
    public final String text;
    public final String senderName;
    public final Date sendTime;

    public MessageData(String text, String senderName, Date sendTime) {
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
