package com.aot.studentmanagement.service;

import com.aot.studentmanagement.model.Admin;
import com.aot.studentmanagement.config.AppConfig;
import com.aot.studentmanagement.util.PasswordUtil;

public class AuthenticationService {

    private Admin currentAdmin;

    public AuthenticationService() {
        this.currentAdmin = null;
    }

    public boolean authenticate(String username, String password) {
        String defaultUsername = AppConfig.getDefaultAdminUsername();
        String defaultPassword = AppConfig.getDefaultAdminPassword();

        if (username != null && username.equals(defaultUsername)
                && password != null && PasswordUtil.verifyPassword(password, defaultPassword)) {
            currentAdmin = new Admin(defaultUsername, defaultPassword, "Administrator", "ADMIN");
            return true;
        }
        return false;
    }

    public void logout() {
        currentAdmin = null;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public boolean isLoggedIn() {
        return currentAdmin != null;
    }
}
