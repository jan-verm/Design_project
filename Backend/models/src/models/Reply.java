package models;

import courses.User;

import java.util.ArrayList;
import java.util.List;

public class Reply extends CrossReferencingObject {

    private User user;
    private String body;
    private long creationTime;
    private List<Reply> children;
    private int upvotes;
    private boolean approved;
    private int id;
    private List<VideoReference> selfVideoReferences = new ArrayList<>();
    private List<CourseNotesReference> selfCourseNotesReferences = new ArrayList<>();

    public Reply(String body) {
        super();
        this.body = body;
        this.children = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void addChild(Reply r) {
        children.add(r);
    }

    public List<Reply> getChildren() {
        return children;
    }

    public void setChildren(List<Reply> children) {
        this.children = children;
    }

    public boolean isApproved() {
            return approved;
    }

    public void setApproved(boolean approved) {
            this.approved = approved;
    }

    public int getUpvotes() {
            return upvotes;
    }

    public void setUpvotes(int upvotes) {
            this.upvotes = upvotes;
    }

    public void upvote() {
            this.upvotes++;
    }

    public void approve() {
            this.approved = true;
    }

    public List<VideoReference> getSelfVideoReferences() {
        return selfVideoReferences;
    }

    public void setSelfVideoReferences(List<VideoReference> selfVideoReferences) {
        this.selfVideoReferences = selfVideoReferences;
    }

    public List<CourseNotesReference> getSelfCourseNotesReferences() {
        return selfCourseNotesReferences;
    }

    public void setSelfCourseNotesReferences(List<CourseNotesReference> selfCourseNotesReferences) {
        this.selfCourseNotesReferences = selfCourseNotesReferences;
    }
}
