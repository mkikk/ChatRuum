package networking;

@FunctionalInterface
public interface EventHandler<S extends Session, T> {
    /**
     * Kutsutakse välja kui vastav sündmus toimub.
     * @param session Aktiivne sessioon millega sündmus seotud on
     * @param event Sündmus
     */
    void handle(S session, T event);
}
