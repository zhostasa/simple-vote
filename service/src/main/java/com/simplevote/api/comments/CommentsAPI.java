package com.simplevote.api.comments;

import com.simplevote.db.Tables;
import org.javalite.activejdbc.LazyList;

import java.io.IOException;

public interface CommentsAPI {



    public Tables.Comment createComment(Long userId, Long pollId, String comment);

    public void deleteComment(Long commentId);

    public LazyList<Tables.Comment> getPollComments(Long pollId) throws IOException;

}
