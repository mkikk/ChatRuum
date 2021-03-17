package networking;

public interface Session {
    /**
     * Saadab sõnumi esimesel võimalusel.
     * See meetod lisab sõnumi järjekorda ning ei oota, et sõnumit saadetakse.
     * Kui sessioon on suletud siis ei pruugi sõnumi saatmine toimuda.
     * @param message
     */
    public void sendMessage(Message message);
}
