package models;

import java.util.List;

public class CourseNotesReference implements Reference {

    private int refId;
    private CourseNotes courseNotes;
    private List<Location> locations;
    private boolean visible;

    public CourseNotesReference(CourseNotes courseNotes, List<Location> locations) {
            super();
            this.courseNotes = courseNotes;
            this.locations = locations;
            this.visible = true;
    }

    public CourseNotes getCourseNotes() {
        return courseNotes;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
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
