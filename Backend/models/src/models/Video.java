package models;

public class Video extends CrossReferencingObject {

    private String title;
    private String url;
    private int duration;
    private int id;

    public Video(String title, String url, int duration) {
            super();
            this.title = title;
            this.url = url;
            this.duration = duration;
    }

    public String getTitle() {
            return title;
    }

    public void setTitle(String title) {
            this.title = title;
    }

    public String getUrl() {
            return url;
    }

    public void setUrl(String url) {
            this.url = url;
    }

    public int getDuration() {
            return duration;
    }
	
    public void setDuration(int duration) {
            this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
