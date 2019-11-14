package com.example.camera_system.entity;

import java.io.Serializable;

public class User implements Serializable {
    private Integer id;
    private String idCard;
    private String sex;
    private String address;
    private String nationality;
    private String birthday;
    private String name;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", idCard='" + idCard + '\'' +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", nationality='" + nationality + '\'' +
                ", birthday='" + birthday + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
