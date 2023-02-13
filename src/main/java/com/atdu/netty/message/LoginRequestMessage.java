package com.atdu.netty.message;

import lombok.Data;

@Data
public class LoginRequestMessage extends Message {
    private String username;
    private String password;
    private String Nickname;
    public LoginRequestMessage(String username, String password,String Nickname) {
        this.username = username;
        this.password = password;
        this.Nickname=Nickname;
    }

    public LoginRequestMessage(String username,String password) {
        this.username=username;
        this.password=password;
    }

    @Override
    public String toString() {
        return "LoginRequestMessage{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' + '}';
    }

    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }
}
