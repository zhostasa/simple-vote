package com.simplevote.api.comments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplevote.DataSources;
import com.simplevote.api.comments.objects.PostsResponse;
import com.simplevote.api.login.MtmAPI;
import com.simplevote.db.Tables;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MtmCommentsAPI implements CommentsAPI {

    private static final String MTM_API_URL_KEY = "mattermost.api.url";
    private static final String MTM_CHANNEL_POSTS_API = "/channels/{channel_id}/posts";

    private static MtmCommentsAPI thisClass;

    // TODO Retry for valid token
    private String token;
    private String teamId = "jx6zqxsd5b8b8xtr5fhcrjdgsr";
    private String channelId = "ou69tescbtfu8cicoukrnozjho";

    private static String email = "bot@test.com";
    private static String password = "heslo";


    public static synchronized MtmCommentsAPI getCommentsAPI() throws IOException {
        if (thisClass == null)
            thisClass = new MtmCommentsAPI();
        return thisClass;
    }

    private MtmCommentsAPI() throws IOException {
       token = getToken();
    }

    @Override
    public Tables.Comment createComment(Long userId, Long pollId, String comment) {
        return null;
    }

    @Override
    public void deleteComment(Long commentId) {

    }

    @Override
    public LazyList<Tables.Comment> getPollComments(Long pollId) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) getPostsApiUrl(channelId).openConnection();
        conn.setDoOutput(false);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.connect();

        System.out.println(conn.getResponseCode() + ":" + conn.getResponseMessage());

        PostsResponse postsResponse = new ObjectMapper().readValue(conn.getInputStream(), PostsResponse.class);

        LazyListProxy<Tables.Comment> list = new LazyListProxy<>();

        for (String post : postsResponse.getOrder()){
            Tables.Comment comment = new Tables.Comment();
            comment.set("comment", postsResponse.getPosts().get(post).getMessage());
            comment.set("poll_id", pollId);
            comment.set("user_id", 1);
            list.add(comment);
        }

        return list;
    }

    private class LazyListProxy<T extends Model> extends LazyList<T> {

    }

    private URL getPostsApiUrl(String channelId) throws MalformedURLException {
        return new URL(DataSources.PROPERTIES.getProperty(MTM_API_URL_KEY) + MTM_CHANNEL_POSTS_API.replace("{channel_id}", channelId.toString()));
    }

    private String getToken() throws IOException {
        return new MtmAPI().getToken(email, password);
    }

}
