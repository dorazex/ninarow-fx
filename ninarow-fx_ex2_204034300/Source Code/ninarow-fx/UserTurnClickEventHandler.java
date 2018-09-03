import javafx.event.EventHandler;

public abstract class UserTurnClickEventHandler implements EventHandler<CustomEvent> {

    public abstract void onUserClick(int column);


    @Override
    public void handle(CustomEvent event) {
        event.invokeHandler(this);
    }
}
