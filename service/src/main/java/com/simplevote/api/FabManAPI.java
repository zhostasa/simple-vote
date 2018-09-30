package com.simplevote.api;

public class FabManAPI implements LoginAPI {
    @Override
    public boolean validateUser(String email, String password) {
        return false;
    }
}
