package com.simplevote.api.comments.objects;

import java.util.Collection;
import java.util.Map;

public class PostsResponse {

    private Collection<String> order;

     private Map<String, MessageObject> posts;


    public PostsResponse() {
    }

    public Collection<String> getOrder() {
        return order;
    }

    public void setOrder(Collection<String> order) {
        this.order = order;
    }

        public Map<String, MessageObject> getPosts() {
            return posts;
        }

        public void setPosts(Map<String, MessageObject> posts) {
            this.posts = posts;
        }

}
