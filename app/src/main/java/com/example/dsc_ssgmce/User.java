package com.example.dsc_ssgmce;

/**
 * Created by :-> Sudhir Ghagare
 * Date :-> 22/3/2021
 * Time :-> 6:23 PM
 */

public class User {
    public String username;
    public String email;
    public String password;
    public String phoneNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String password, String phoneNumber) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;

    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
