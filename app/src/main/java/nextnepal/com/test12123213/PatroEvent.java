package nextnepal.com.test12123213;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PatroEvent {
    @SerializedName("event_db")
    @Expose
    private List<EventDb> eventDb = null;

    public List<EventDb> getEventDb() {
        return eventDb;
    }

    public void setEventDb(List<EventDb> eventDb) {
        this.eventDb = eventDb;
    }
}
