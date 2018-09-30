package com.simplevote.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplevote.DataSources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MtmAPI {

    private static final String MTM_API_URL_KEY="mattermost.api.url";
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

        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
        }

        conn.disconnect();

        return false;
    }

    private static class LoginCall{

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
