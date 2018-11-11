package com.simplevote.api.login;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractRestLoginAPI implements LoginAPI{

    private HttpURLConnection makeAPICall(String email, String password) throws IOException{
        HttpURLConnection conn = (HttpURLConnection) getURL().openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        String input = new ObjectMapper().writeValueAsString(getBody(email, password));

        OutputStream os = conn.getOutputStream();
        os.write(input.getBytes());
        os.flush();

        conn.connect();

        return conn;
    }

    public boolean validateUser(String email, String password) throws IOException {

        HttpURLConnection conn = makeAPICall(email, password);

        if (conn.getResponseCode() == getFailedLoginResponseCode())
            return false;

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException("Unable to authorize, http response " + conn.getResponseCode() + " : " + conn.getResponseMessage());

        return true;
    }

    public String getToken(String email, String password) throws IOException{
        HttpURLConnection conn = makeAPICall(email, password);

        if (conn.getResponseCode() == getFailedLoginResponseCode())
            return null;

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException("Unable to authorize for token, http response " + conn.getResponseCode() + " : " + conn.getResponseMessage());

        return conn.getHeaderField("token");
    }

    protected abstract URL getURL() throws MalformedURLException;

    protected abstract Object getBody(String email, String password);

    protected abstract int getFailedLoginResponseCode();
}
