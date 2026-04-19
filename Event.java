import java.util.ArrayList;
import java.util.List;

public class Event {
    int id;
    String title, location;
    User organizer;
    List<String> participants = new ArrayList<>();

    public Event(int id, String title, String location, User organizer) {
        this.id        = id;
        this.title     = title;
        this.location  = location;
        this.organizer = organizer;
    }

    public String toString() {
        return "Event#" + id + " [" + title + "] à " + location
             + " | participants=" + participants.size();
    }
}
