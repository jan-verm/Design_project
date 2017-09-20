package models;

public class VideoReference implements Reference {

    private int refId;
    private Video video;
    private int timestamp;
    private boolean visible;

    public VideoReference(Video video, int timestamp) {
            super();
            this.video = video;
            this.timestamp = timestamp;
            this.visible = true;
    }

    public Video getVideo() {
        return video;
    }

    public int getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
