package nextnepal.com.test12123213;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventDb {
    @SerializedName("event_id")
    @Expose
    private String eventId;
    @SerializedName("event_detail_np")
    @Expose
    private String eventDetailNp;
    @SerializedName("event_detail_en")
    @Expose
    private String eventDetailEn;
    @SerializedName("tithe")
    @Expose
    private String tithe;
    @SerializedName("holiday")
    @Expose
    private Integer holiday;

    public EventDb(String eventId, String eventDetailNp, String eventDetailEn, String tithe, Integer holiday) {
        this.eventId = eventId;
        this.eventDetailNp = eventDetailNp;
        this.eventDetailEn = eventDetailEn;
        this.tithe = tithe;
        this.holiday = holiday;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventDetailNp() {
        return eventDetailNp;
    }

    public void setEventDetailNp(String eventDetailNp) {
        this.eventDetailNp = eventDetailNp;
    }

    public String getEventDetailEn() {
        return eventDetailEn;
    }

    public void setEventDetailEn(String eventDetailEn) {
        this.eventDetailEn = eventDetailEn;
    }

    public String getTithe() {
        return tithe;
    }

    public void setTithe(String tithe) {
        this.tithe = tithe;
    }

    public Integer getHoliday() {
        return holiday;
    }

    public void setHoliday(Integer holiday) {
        this.holiday = holiday;
    }

}
