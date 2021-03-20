package server;

import networking.Session;
import server.User;

public interface ServerSession extends Session {
    /**
     * Seostab sessiooni konkreetse kasutajaga. Seda meetodit peaks rakendama pärast seda kui sisselogimine on edukalt läbi viidud ja kliendi isik on kontrollitud.
     * @param user Kasutaja millega sessioon seostada.
     */
    void setUser(User user);

    /**
     * Selle sessiooniga seostatud kasutaja, või null kui selle sessiooniga pole veel kasutajat seostatud (sisselogimist pole toimunud).
     * @return Sessiooniga seostatud kasutaja.
     */
    User getUser();

    void setActiveChannel(Channel activeChannel);

    Channel getActiveChannel();
}
