package com.rst.jwt_example.jwt;

public class LoginForm {
    public LoginForm(){}
    public LoginForm(String username, String password){
        this.username = username;
        this.password = password;
    }
    private String username;
    private String password;
    public String getUsername(){return username;}
    public String getPassword(){return password;}
}
