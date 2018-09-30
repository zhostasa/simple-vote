package com.simplevote.api;

public interface LoginAPI {

    boolean validateUser(String email, String password);
}
