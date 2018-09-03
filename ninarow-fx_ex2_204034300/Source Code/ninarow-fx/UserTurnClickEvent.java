import javafx.event.EventType;

public class UserTurnClickEvent extends CustomEvent {

    public static final EventType<CustomEvent> CUSTOM_EVENT_TYPE_1 =
            new EventType(CUSTOM_EVENT_TYPE, "CustomEvent1");

    private final int column;

    public UserTurnClickEvent(int column) {
        super(CUSTOM_EVENT_TYPE_1);
        this.column = column;
    }

    @Override
    public void invokeHandler(UserTurnClickEventHandler handler) {
        handler.onUserClick(column);
    }

}
