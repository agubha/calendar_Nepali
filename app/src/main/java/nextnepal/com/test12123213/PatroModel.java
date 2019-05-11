package nextnepal.com.test12123213;

public class PatroModel {

    private int nyear;
    private int nmonth;
    private int nday;
    private int eyear;
    private int emonth;
    private int eday;
    private String id;
    private int dayOfWeek;

    public PatroModel(int nyear, int nmonth, int nday, int eyear, int emonth, int eday, String id, int dayOfWeek) {
        this.nyear = nyear;
        this.nmonth = nmonth;
        this.nday = nday;
        this.eyear = eyear;
        this.emonth = emonth;
        this.eday = eday;
        this.id = id;
        this.dayOfWeek = dayOfWeek;
    }

    public int getNyear() {
        return nyear;
    }

    public void setNyear(int nyear) {
        this.nyear = nyear;
    }

    public int getNmonth() {
        return nmonth;
    }

    public void setNmonth(int nmonth) {
        this.nmonth = nmonth;
    }

    public int getNday() {
        return nday;
    }

    public void setNday(int nday) {
        this.nday = nday;
    }

    public int getEyear() {
        return eyear;
    }

    public void setEyear(int eyear) {
        this.eyear = eyear;
    }

    public int getEmonth() {
        return emonth;
    }

    public void setEmonth(int emonth) {
        this.emonth = emonth;
    }

    public int getEday() {
        return eday;
    }

    public void setEday(int eday) {
        this.eday = eday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


}
