package com.micro.entity;

import java.util.Date;

public class User {
    private Integer uid;
    private String username;
    private String password;
    private String nickname;
    private Integer role;
    private Date registerTime;

    public User() {}

    public Integer getUid() { return uid; }
    public void setUid(Integer uid) { this.uid = uid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public Date getRegisterTime() { return registerTime; }
    public void setRegisterTime(Date registerTime) { this.registerTime = registerTime; }
}
