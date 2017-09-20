package models;

import java.util.ArrayList;
import java.util.List;

public class Comment extends Reply {


    private boolean question;

    public Comment(String body, boolean question) {
        super(body);
        this.question = question;
    }

    public boolean isQuestion() {
        return question;
    }

    public void setQuestion(boolean question) {
        this.question = question;
    }
}
