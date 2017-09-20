package controllermockup;

import interfaces.CommentControllerInterface;

import java.util.ArrayList;
import java.util.List;

import courses.Role;
import courses.User;
import models.Comment;
import models.CourseNotesReference;
import models.Reply;
import models.VideoReference;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;

public class CommentControllerMockup implements CommentControllerInterface {

    @Override
    public int addVideoComment(int videoId, Comment comment, List<VideoReference> selfVideoRefs, int userId) {
        return 1;
    }

    @Override
    public Comment getComment(int commentId) throws ClassicNotFoundException {
        if (commentId == 0) {
            throw new ClassicNotFoundException();
        }
        Comment comment = new Comment("body", false);
        comment.setUser(new User("user", Role.ADMIN, "password"));
        comment.setId(1);
        comment.setCreationTime(1);
        return comment;
    }

    @Override
    public List<Comment> getVideoComments(int videoId) throws ClassicNotFoundException {
        if (videoId == 0) {
            throw new ClassicNotFoundException();
        }
        List<Comment> list = new ArrayList<>();
        Comment comment = new Comment("body", false);
        comment.setUser(new User("user", Role.ADMIN, "password"));
        comment.setId(1);
        comment.setCreationTime(1);
        list.add(comment);
        return list;
    }

    @Override
    public void updateComment(int commentId, Comment comment, int userId) throws ClassicNotFoundException {
        if (commentId == 0) {
            throw new ClassicNotFoundException();
        }
        return;
    }

    @Override
    public void deleteComment(int commentId, int userId) throws ClassicNotFoundException {
        if (commentId == 0) {
            throw new ClassicNotFoundException();
        }
        return;
    }

    @Override
    public int addCourseNotesComment(int courseNotesId, Comment comment,
            List<CourseNotesReference> selfCourseNotesRefs, int userId)
            throws ClassicDatabaseException, ClassicNotFoundException {
        if (courseNotesId == 0) {
            throw new ClassicNotFoundException();
        }
        return 1;
    }

    @Override
    public List<Comment> getCourseNotesComments(int courseNotesId)
            throws ClassicDatabaseException, ClassicNotFoundException {
        if (courseNotesId == 0) {
            throw new ClassicNotFoundException();
        }
        List<Comment> list = new ArrayList<>();
        Comment comment = new Comment("body", false);
        comment.setUser(new User("user", Role.ADMIN, "password"));
        comment.setId(1);
        comment.setCreationTime(1);
        list.add(comment);
        return list;
    }

    @Override
    public int addReplytoComment(int commentId, Reply reply, int userId)
            throws ClassicDatabaseException, ClassicNotFoundException {
        if (commentId == 0) {
            throw new ClassicNotFoundException();
        }
        return 1;
    }

    @Override
    public int addReplytoReply(int replyId, Reply reply, int userId)
            throws ClassicDatabaseException, ClassicNotFoundException {
        if (replyId == 0) {
            throw new ClassicNotFoundException();
        }
        return 1;
    }

    @Override
    public void updateReply(int replyId, Reply reply, int userId)
            throws ClassicDatabaseException, ClassicNotFoundException {
        if (replyId == 0) {
            throw new ClassicNotFoundException();
        }
        return;
    }

    @Override
    public void deleteReply(int replyId, int userId) throws ClassicDatabaseException,
            ClassicNotFoundException {
        if (replyId == 0) {
            throw new ClassicNotFoundException();
        }
        return;
    }

    @Override
    public List<Reply> getReplies(int commentId)
            throws ClassicDatabaseException, ClassicNotFoundException {
        if (commentId == 0) {
            throw new ClassicNotFoundException();
        }
        List<Reply> replies = new ArrayList<>();
        replies.add(getReply(1));
        return replies;
    }

    @Override
    public Reply getReply(int replyId) throws ClassicDatabaseException,
            ClassicNotFoundException {
        if (replyId == 0) {
            throw new ClassicNotFoundException();
        }
        Reply reply = new Reply("body");
        reply.setUser(new User("user", Role.ADMIN, "password"));
        reply.setId(1);
        return reply;
    }

    @Override
    public void voteComment(int commentId, boolean upvote, int userid)
            throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

    @Override
    public void setApprovedComment(int commentId, boolean approved, int userid)
            throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

    @Override
    public void voteReply(int replyId, boolean upvote, int userid)
            throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

    @Override
    public void setApprovedReply(int replyId, boolean approved, int userid)
            throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

}
