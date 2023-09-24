package com.example.pro_hive;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String jobTitle;
    private boolean vc;
    private String field;
    private String country;
    private String desc;
    private String skills;

    private String avatarImage;

    public User() {
        // required empty constructor
    }

    public User(String firstName, String lastName, String email, String phone, String jobTitle, boolean vc, String field, String country, String id, String desc, String skills) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.jobTitle = jobTitle;
        this.vc = vc;
        this.field = field;
        this.country = country;
        this.desc = desc;
        this.skills = skills;
    }

    public User(String id, String firstName, String lastName, String email, String phone, String jobTitle, boolean vc, String field, String country, String desc, String skills, String avatarImage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.jobTitle = jobTitle;
        this.vc = vc;
        this.field = field;
        this.country = country;
        this.desc = desc;
        this.skills = skills;
        this.avatarImage = avatarImage;
    }

    protected User(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        phone = in.readString();
        jobTitle = in.readString();
        vc = in.readByte() != 0;
        field = in.readString();
        country = in.readString();
        desc = in.readString();
        skills = in.readString();
        avatarImage = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public boolean isVc() {
        return vc;
    }

    public void setVc(boolean vc) {
        this.vc = vc;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(jobTitle);
        parcel.writeByte((byte) (vc ? 1 : 0));
        parcel.writeString(field);
        parcel.writeString(country);
        parcel.writeString(desc);
        parcel.writeString(skills);
        parcel.writeString(avatarImage);
    }
}

