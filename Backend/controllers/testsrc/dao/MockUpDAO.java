/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import databaseaccess.interfaces.ICommentDao;
import databaseaccess.interfaces.ICourseNotesDao;
import databaseaccess.interfaces.ICourseNotesReferenceDao;
import databaseaccess.interfaces.IReplyDao;
import databaseaccess.interfaces.IVideoDao;
import databaseaccess.interfaces.IVideoReferenceDao;

import java.util.ArrayList;
import java.util.List;

import courses.User;
import models.Comment;
import models.CommentReference;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Reply;
import models.Video;
import models.VideoReference;

/**
 *
 * @author Juta
 */
public class MockUpDAO implements ICommentDao, IReplyDao, IVideoDao, IVideoReferenceDao, ICourseNotesDao, ICourseNotesReferenceDao {
    
    private List<Video> videos = new ArrayList<>();
    private List<CourseNotes> coursenotes = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private List<Reply> replies = new ArrayList<>();
    private int idCounter = 1;
    
    // VIDEOS

    @Override
    public List<Video> getVideos() {
        return videos;
    }

    @Override
    public Video getVideo(int videoID) throws ClassicNotFoundException {
        for (Video video : videos) {
            if (video.getId() == videoID) {
                return video;
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public int addVideo(int parentId, Video video) {
        int id = idCounter;
        idCounter++;
        video.setId(id);
        videos.add(video);
        return id;
    }

    @Override
    public void updateVideo(Video video) throws ClassicNotFoundException {
        boolean updated = false;
        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).getId() == video.getId()) {
                videos.remove(i);
                videos.add(i, video);
                updated = true;
            }
        }
        if (!updated) {
            throw new ClassicNotFoundException();
        }
    }

    @Override
    public void deleteVideo(int videoID) throws ClassicNotFoundException {
        boolean deleted = false;
        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).getId() == videoID) {
                videos.remove(i);
                deleted = true;
            }
        }
        if (!deleted) {
            throw new ClassicNotFoundException();
        }
    }
    
    // COMMENTS

    @Override
    public List<Comment> getVideoComments(int videoID) throws ClassicNotFoundException {
        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).getId() == videoID) {
                List<CommentReference> references = videos.get(i).getCommentRefs();
                List<Comment> comments = new ArrayList<>();
                for (CommentReference reference : references) {
                    comments.add(reference.getComment());
                }
                return comments;
            }
        }
        throw new ClassicNotFoundException();
    }
    
    @Override
    public List<Comment> getCourseNotesComments(int coursenotesID) throws ClassicNotFoundException {
        for (int i = 0; i < coursenotes.size(); i++) {
            if (coursenotes.get(i).getId() == coursenotesID) {
                List<CommentReference> references = coursenotes.get(i).getCommentRefs();
                List<Comment> comments = new ArrayList<>();
                for (CommentReference reference : references) {
                    comments.add(reference.getComment());
                }
                return comments;
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public int addComment(Comment comment) {
        comment.setId(idCounter);
        idCounter++;
        comments.add(comment);
        return idCounter-1;
    }
    
    @Override
    public Comment getComment(int commentId) throws ClassicNotFoundException {
        for (Comment comment : comments) {
            if (comment.getId() == commentId) {
                return comment;
            }
        }
        throw new ClassicNotFoundException();
    }
    
    @Override
    public void deleteComment(int commentID) throws ClassicNotFoundException {
        Comment com = null;
        for (Comment comment : comments) {
            if (comment.getId() == commentID) {
                com = comment;
            }
        }
        if (com == null) {
            throw new ClassicNotFoundException();
        }
        comments.remove(com);
    }
    @Override
    public void updateComment(Comment comment) throws ClassicNotFoundException {
        Comment com = null;
        for (Comment c : comments) {
            if (c.getId() == comment.getId()) {
                com = c;
            }
        }
        if (com == null) {
            throw new ClassicNotFoundException();
        }
        comments.remove(com);
        comments.add(comment);
    }
    
    // REPLIES

    @Override
    public int addReply(int parentID, Reply reply) throws ClassicNotFoundException {
        boolean found = false;
        for (Reply r : replies) {
            if (r.getId() == parentID) {
                found = true;
                r.addChild(reply);
            }
        }
        for (Comment c : comments) {
            if (c.getId() == parentID) {
                found = true;
                c.addChild(reply);
            }
        }
        if (!found) {
            throw new ClassicNotFoundException();
        }
        reply.setId(idCounter);
        replies.add(reply);
        idCounter++;
        return idCounter-1;
    }

    @Override
    public void deleteReply(int replyID) throws ClassicDatabaseException, ClassicNotFoundException {
        boolean found = false;
        for (Comment c : comments) {
            Reply reply = null;
            for (Reply r : c.getChildren()) {
                if (r.getId() == replyID) {
                    found = true;
                    reply = r;
                }
            }
            c.getChildren().remove(reply);
        }
        Reply deletedReply = null;
        for (Reply r : replies) {
            Reply reply = null;
            for (Reply r2 : r.getChildren()) {
                if (r2.getId() == replyID) {
                    found = true;
                    reply = r2;
                }
            }
            r.getChildren().remove(reply);
            if (r.getId() == replyID) {
                deletedReply = r;
            }
        }
        replies.remove(deletedReply);
        
        if (!found) {
            throw new ClassicNotFoundException();
        }
    }

    @Override
    public List<Reply> getReplies(int parentID) throws ClassicDatabaseException, ClassicNotFoundException {
        for (Comment c : comments) {
            if (c.getId() == parentID) {
                return c.getChildren();
            }
        }
        for (Reply r : replies) {
            if (r.getId() == parentID) {
                return r.getChildren();
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public Reply getReply(int replyID) throws ClassicDatabaseException, ClassicNotFoundException {
        for (Reply r : replies) {
            if (r.getId() == replyID) {
                return r;
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public void updateReply(Reply reply) throws ClassicDatabaseException, ClassicNotFoundException {
        Reply rep = null;
        for (Reply r : replies) {
            if (r.getId() == reply.getId()) {
                rep = r;
            }
        }
        if (rep == null) {
            throw new ClassicNotFoundException();
        }
        replies.remove(rep);
        replies.add(reply);
        
        for (Comment c : comments) {
            for (Reply r : c.getChildren()) {
                if (r.getId() == reply.getId()) {
                    rep = r;
                }
            }
            c.getChildren().remove(rep);
            c.getChildren().add(reply);
        }
    }
    
    //VIDEO REFERENCES

    @Override
    public int addVideoReference(int videoID, int commentId, VideoReference videoRef) throws ClassicNotFoundException {
        Comment comment = getComment(commentId);
        CommentReference ref = null;
        
        int id = idCounter;
        idCounter++;
        videoRef.setRefId(id);
        
        boolean found = false;
        for (Video video : videos) {
            if (video.getId() == videoID) {
                found = true;
                ref = new CommentReference(comment);

                ref.getComment().addReference(videoRef);
                video.addReference(ref);
            }
        }
        if (!found) {
            throw new ClassicNotFoundException();
        }
        
        return id;
    }

    @Override
    public List<VideoReference> getVideoReferences(int commentId) throws ClassicNotFoundException {
        for (Video video : videos) {
            for (CommentReference reference : video.getCommentRefs()) {
                if (reference.getComment().getId() == commentId) {
                    return reference.getComment().getVideoRefs();
                }
            }
        }
        for (Comment c : comments) {
            if (c.getId() == commentId) {
                return new ArrayList<>();
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public List<VideoReference> getSelfVideoReferences(int videoID, int commentID) throws ClassicNotFoundException {
        for (Video video : videos) {
            if (video.getId() == videoID) {
                for (CommentReference reference : video.getCommentRefs()) {
                    if (reference.getComment().getId() == commentID) {
                        List<VideoReference> selfrefs = new ArrayList<>();
                        for (VideoReference ref : reference.getComment().getVideoRefs()){
                            if (ref.getVideo().getId() == videoID) {
                                selfrefs.add(ref);
                            }
                        }
                        return selfrefs;
                    }
                }
            }
        }
        throw new ClassicNotFoundException();
    }
    
    @Override
    public void deleteVideoReference(int refId) throws ClassicDatabaseException, ClassicNotFoundException {
        boolean deleted = false;
        for (Video video : videos) {
            for (CommentReference reference : video.getCommentRefs()) {
                VideoReference videoreference = null;
                for (VideoReference ref : reference.getComment().getVideoRefs()){
                    if (ref.getRefId() == refId) {
                        videoreference = ref;
                    }
                }
                if (videoreference != null) {
                    reference.getComment().getVideoRefs().remove(videoreference);
                    deleted = true;
                }
            }
        }
        if (!deleted) {
            throw new ClassicNotFoundException();
        }   
    }

    @Override
    public void updateVideoReference(VideoReference videoRef) throws ClassicDatabaseException, ClassicNotFoundException {
        boolean updated = false;
        for (Video video : videos) {
            for (CommentReference reference : video.getCommentRefs()) {
                VideoReference videoreference = null;
                for (VideoReference ref : reference.getComment().getVideoRefs()){
                    if (ref.getRefId() == videoRef.getRefId()) {
                        videoreference = ref;
                    }
                }
                if (videoreference != null) {
                    reference.getComment().getVideoRefs().remove(videoreference);
                    reference.getComment().getVideoRefs().add(videoRef);
                    updated = true;
                }    
            }
        }
        if (!updated) {
            throw new ClassicNotFoundException();
        }
    }

    // COURSE NOTES

    @Override
    public int addCourseNotes(int parentId, CourseNotes courseNotes) {
        int id = idCounter;
        idCounter++;
        courseNotes.setId(id);
        coursenotes.add(courseNotes);
        return id;
    }

    @Override
    public void deleteCourseNotes(int courseNotesID) throws ClassicNotFoundException {
        boolean deleted = false;
        for (int i = 0; i < coursenotes.size(); i++) {
            if (coursenotes.get(i).getId() == courseNotesID) {
                coursenotes.remove(i);
                deleted = true;
            }
        }
        if (!deleted) {
            throw new ClassicNotFoundException();
        }
    }

    @Override
    public List<CourseNotes> getAllCourseNotes() {
        return coursenotes;
    }

    @Override
    public CourseNotes getCourseNotes(int courseNotesID) throws ClassicNotFoundException {
        for (int i = 0; i < coursenotes.size(); i++) {
            if (coursenotes.get(i).getId() == courseNotesID) {
                return coursenotes.get(i);
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public void updateCourseNotes(CourseNotes courseNotes) throws ClassicNotFoundException {
        boolean updated = false;
        for (int i = 0; i < coursenotes.size(); i++) {
            if (coursenotes.get(i).getId() == courseNotes.getId()) {
                coursenotes.remove(i);
                coursenotes.add(courseNotes);
                updated = true;
            }
        }
        if (!updated) {
            throw new ClassicNotFoundException();
        }
    }
    
    // COURSE NOTES REFERENCES

    @Override

    public int addCourseNotesReference(int courseNotesID, int commentID, CourseNotesReference courseNotesRef) throws ClassicNotFoundException {
        boolean found = false;
        Comment comment = getComment(commentID);

        CommentReference ref = null;
        for (CourseNotes note : coursenotes) {
            if (note.getId() == courseNotesID) {
                ref = new CommentReference(comment);
                ref.getComment().addReference(courseNotesRef);
                note.addReference(ref);
                found = true;
            }
        }
        if (!found) {
            throw new ClassicNotFoundException();
        }
        int id = idCounter;
        idCounter++;
        courseNotesRef.setRefId(id);
        return id;
    }

    @Override
    public void deleteCourseNotesReference(int refId) throws ClassicDatabaseException, ClassicNotFoundException {
        boolean deleted = false;
        for (CourseNotes note : coursenotes) {
            for (CommentReference reference : note.getCommentRefs()) {
                CourseNotesReference notesreference = null;
                for (CourseNotesReference ref : reference.getComment().getCourseNotesRefs()){
                    if (ref.getRefId() == refId) {
                        notesreference = ref;
                    }
                }
                if (notesreference != null) {
                    reference.getComment().getCourseNotesRefs().remove(notesreference);
                    deleted = true;
                }
            }
        }
        if (!deleted) {
            throw new ClassicNotFoundException();
        }
    }

    @Override
    public List<CourseNotesReference> getSelfCourseNotesReferences(int courseNotesId, int commentId) throws ClassicNotFoundException {
        for (CourseNotes note : coursenotes) {
            if (note.getId() == courseNotesId) {
                for (CommentReference reference : note.getCommentRefs()) {
                    if (reference.getComment().getId() == commentId) {
                        List<CourseNotesReference> selfrefs = new ArrayList<>();
                        for (CourseNotesReference ref : reference.getComment().getCourseNotesRefs()){
                            if (ref.getCourseNotes().getId() == courseNotesId) {
                                selfrefs.add(ref);
                            }
                        }
                        return selfrefs;
                    }
                }
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public List<CourseNotesReference> getCourseNotesReferences(int commentID) throws ClassicDatabaseException, ClassicNotFoundException {
        for (CourseNotes note : coursenotes) {
            for (CommentReference reference : note.getCommentRefs()) {
                if (reference.getComment().getId() == commentID) {
                    return reference.getComment().getCourseNotesRefs();
                }
            }
        }
        for (Comment c : comments) {
            if (c.getId() == commentID) {
                return new ArrayList<>();
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public void updateCourseNotesReference(CourseNotesReference courseNotesRef) throws ClassicDatabaseException, ClassicNotFoundException {
        boolean updated = false;
        for (CourseNotes note : coursenotes) {
            for (CommentReference reference : note.getCommentRefs()) {
                CourseNotesReference notesreference = null;
                for (CourseNotesReference ref : reference.getComment().getCourseNotesRefs()){
                    if (ref.getRefId() == courseNotesRef.getRefId()) {
                        notesreference = ref;
                    }
                }
                if (notesreference != null) {
                    reference.getComment().getCourseNotesRefs().remove(notesreference);
                    reference.getComment().getCourseNotesRefs().add(courseNotesRef);
                    updated = true;
                }
            }
        }
        if (!updated) {
            throw new ClassicNotFoundException();
        }
    }
    
    
    @Override
    public VideoReference getVideoReference(int refID) throws ClassicNotFoundException {
        for (Video video : videos) {
            for (CommentReference reference : video.getCommentRefs()) {
                for (VideoReference ref : reference.getComment().getVideoRefs()){
                    if (ref.getRefId() == refID) {
                        return ref;
                    }
                }
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public CourseNotesReference getCourseNotesReference(int refID) throws ClassicNotFoundException {
        for (CourseNotes note : coursenotes) {
            for (CommentReference reference : note.getCommentRefs()) {
                for (CourseNotesReference ref : reference.getComment().getCourseNotesRefs()){
                    if (ref.getRefId() == refID) {
                        return ref;
                    }
                }
            }
        }
        throw new ClassicNotFoundException();
    }
    
    // CLEAN TABLE
    
    @Override
    public void cleanTable() throws ClassicDatabaseException {
        videos.clear();
        comments.clear();
        coursenotes.clear();
        idCounter = 1;
    }

    @Override
    public List<Video> getVideos(int parentID) throws ClassicDatabaseException, ClassicNotFoundException {
        return videos;
    }

    @Override
    public List<CourseNotes> getParentCourseNotes(int parentID) throws ClassicDatabaseException, ClassicNotFoundException {
        return coursenotes;
    }

    @Override
    public User getOwner(int id) throws ClassicDatabaseException, ClassicNotFoundException {
        User user = new User("username", Role.ADMIN, "password");
        user.setId(1);
        return user;
    }

    @Override
    public void vote(int userID, int commentID, boolean upvote) throws ClassicUnauthorizedException, ClassicDatabaseException, ClassicNotFoundException {
    	try {
    		Comment comment = getComment(commentID);
    		int votes = comment.getUpvotes();
    		if (votes==0) 
    			comment.setUpvotes(votes+(upvote?1:-1));
    		else
    			throw new ClassicUnauthorizedException();
    	} catch (ClassicNotFoundException e) {
    		Reply reply = getReply(commentID);
    		int votes = reply.getUpvotes();
    		if (votes==0) 
    			reply.setUpvotes(votes+(upvote?1:-1));
    		else
    			throw new ClassicUnauthorizedException();
    		
    	}
    }

    @Override
    public void approve(int commentID, boolean approve) throws ClassicDatabaseException, ClassicNotFoundException {
    	try {
    		Comment comment = getComment(commentID);
    		comment.setApproved(approve);
    	} catch (ClassicNotFoundException e) {
    		Reply reply = getReply(commentID);
    		reply.setApproved(approve);
    		
    	}
    }

    @Override
    public int[] getCourseAndLectureForVideo(int videoID) throws ClassicNotFoundException, ClassicDatabaseException {
        return new int[]{1,0};
    }

    @Override
    public int[] getCourseAndLectureForCourseNotes(int courseNotesID) throws ClassicNotFoundException, ClassicDatabaseException {
        return new int[]{1,0};
    }
}