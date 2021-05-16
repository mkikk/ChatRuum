package networking.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.Instant;


/**
 * Data for a sent message
 */
public class MessageData implements Serializable {
    private static final long serialVersionUID = 177597097269338496L;

    public final String text;
    public final String senderName;
    public final Instant sendTime;

    public MessageData(String text, String senderName, Instant sendTime) {
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
