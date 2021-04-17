package networking.data;

import java.io.Serializable;
import java.time.LocalDate;


/**
 * Data for a sent message
 */
public class MessageData implements Serializable {
    public final String text;
    public final String senderName;
    public final LocalDate sendTime;

    public MessageData(String text, String senderName, LocalDate sendTime) {
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
