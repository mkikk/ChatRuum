package networking.messages;

import networking.Message;

public class TestMessage implements Message {
    public final String text;

    public TestMessage(String text) {
        this.text = text;
    }
}
