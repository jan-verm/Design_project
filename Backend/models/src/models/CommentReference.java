package models;

public class CommentReference implements Reference {

	private Comment comment;

	public CommentReference(Comment comment) {
		super();
		this.comment = comment;
	}

    public Comment getComment() {
        return comment;
    }
}
