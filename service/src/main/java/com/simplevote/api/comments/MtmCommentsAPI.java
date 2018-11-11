package com.simplevote.api.comments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplevote.DataSources;
import com.simplevote.api.login.MtmAPI;
import com.simplevote.db.Tables;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MtmCommentsAPI implements CommentsAPI {

    private static final String MTM_API_URL_KEY = "mattermost.api.url";
    private static final String MTM_TEAM_API = "/teams/name/{name}";
    private static final String MTM_CHANNEL_API = "/teams/{team_id}/channels/name/{channel_name}";
    private static final String MTM_CHANNEL_POSTS_API = "/channels/{channel_id}/posts";

    private static MtmCommentsAPI thisClass;

    // TODO Retry for valid token
    private String token;
    private Integer teamId, channelId;

    private static String email = "bot@test.com";
    private static String password = "heslo";
    private static String teamName = "FabLab";
    private static String channelName = "PollTest";



    public static synchronized MtmCommentsAPI getCommentsAPI() throws IOException {
        if (thisClass == null)
            thisClass = new MtmCommentsAPI();
        return thisClass;
    }

    private MtmCommentsAPI() throws IOException {
        token = getToken();
        teamId = getTeamId();
        channelId = getChannelId();

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

        PostsResponse postsResponse = new ObjectMapper().readValue(conn.getInputStream(), PostsResponse.class);

        LazyListProxy<Tables.Comment> list = new LazyListProxy<>();

        for (String post : postsResponse.getOrder()){
            Tables.Comment comment = new Tables.Comment();
            comment.set("comment", postsResponse.getPosts().get(post).getMessage());
            comment.set("poll_id", pollId);
            list.add(comment);
        }

        return list;
    }

    private class LazyListProxy<T extends Model> extends LazyList<T>{

    }

    private URL getPostsApiUrl(Integer channelId) throws MalformedURLException {
        return new URL(DataSources.PROPERTIES.getProperty(MTM_API_URL_KEY) + MTM_CHANNEL_POSTS_API.replace("{channel}", channelId.toString()));
    }

    private String getToken() throws IOException {
        return new MtmAPI().getToken(email, password);
    }

    private Integer getChannelId() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) getChannelApiUrl(teamId, channelName).openConnection();
        conn.setDoOutput(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.connect();

        IdResponse idResponse = new ObjectMapper().readValue(conn.getInputStream(), IdResponse.class);

        return idResponse.getId();
    }

    private URL getChannelApiUrl(Integer teamId, String channelName) throws MalformedURLException {
        return new URL(DataSources.PROPERTIES.getProperty(MTM_API_URL_KEY) + MTM_CHANNEL_API.replace("{team_id}", teamId.toString()).replace("{channel_name}", channelName));
    }

    private Integer getTeamId() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) getTeamApiUrl(teamName).openConnection();
        conn.setDoOutput(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.connect();

        IdResponse idResponse = new ObjectMapper().readValue(conn.getInputStream(), IdResponse.class);

        return idResponse.getId();

    }

    private URL getTeamApiUrl(String team) throws MalformedURLException {
        return new URL(DataSources.PROPERTIES.getProperty(MTM_API_URL_KEY) + MTM_TEAM_API.replace("{name}", team));
    }

    private class IdResponse {
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    private class PostsResponse{

        private List<String> order;

        private Map<String, MessageObject> posts;

        public List<String> getOrder() {
            return order;
        }

        public void setOrder(List<String> order) {
            this.order = order;
        }

        public Map<String, MessageObject> getPosts() {
            return posts;
        }

        public void setPosts(Map<String, MessageObject> posts) {
            this.posts = posts;
        }
    }

    private class MessageObject{
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
