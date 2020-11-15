package com.example.test_task.managers;

import com.example.test_task.exception.AccountException;
import com.example.test_task.exception.AuthException;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

class AccountManagerTest {
    AccountManager accountManager = new AccountManager();
    @Test
    void addAccount() {
        String user="name3";
        String password="pass";
        try {
            accountManager.addAccount(user,password);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AccountException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeAccount() {
        try {
            accountManager.removeAccount("name6", "pass");
        } catch (IOException e) {
            e.printStackTrace();
        }
        getAllAccounts();
    }

    @Test
    void getAllAccounts() {
        try {
            List<String> accounts=accountManager.getAllAccounts();
            for (String el:accounts) {
                System.out.println(el);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void writeAccounts() {
    }

    @Test
    void authorize() {
        Date date = new Date();
        System.out.println(date);

        try {
            String token = accountManager.authorize("name7", "pass",date);
            System.out.println(token);
            System.out.println(TokenManager.getInstance().checkToken(token));
        } catch (AuthException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}