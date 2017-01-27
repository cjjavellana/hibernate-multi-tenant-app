package org.cjavellana.models;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "USERINFO")
public class UserInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "userinfoid")
    private Integer userInfoId;

    @Column(name = "name", length = 255)
    private String name;

    public Integer getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Integer userInfoId) {
        this.userInfoId = userInfoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
