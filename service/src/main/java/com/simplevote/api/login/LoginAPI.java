package com.simplevote.api.login;

import java.io.IOException;

public interface LoginAPI {

    boolean validateUser(String email, String password) throws IOException;

    String getToken(String email, String password) throws IOException;
}
