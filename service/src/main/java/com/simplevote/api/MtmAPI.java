package com.simplevote.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplevote.DataSources;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MtmAPI {

    private static final String MTM_API_URL_KEY = "mattermost.api.url";
    private static final String MTM_LOGIN_API = "users/login";


    public static boolean validateUser(String email, String password) throws IOException {

        URL url = new URL(DataSources.PROPERTIES.getProperty(MTM_API_URL_KEY) + MTM_LOGIN_API);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        String input = new ObjectMapper().writeValueAsString(new LoginCall(email, password));

        OutputStream os = conn.getOutputStream();
        os.write(input.getBytes());
        os.flush();

        conn.connect();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
            return false;

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException("Unable to authorize via Mattermost, http response " + conn.getResponseCode() + " : " + conn.getResponseMessage());

        return true;
    }

    private static class LoginCall {

        private String login_id;
        private String password;

        public LoginCall(String login_id, String password) {
            this.login_id = login_id;
            this.password = password;
        }

        public String getLogin_id() {
            return login_id;
        }

        public String getPassword() {
            return password;
        }
    }
}
