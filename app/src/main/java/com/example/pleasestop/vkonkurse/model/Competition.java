package com.example.pleasestop.vkonkurse.model;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Competition {

    public Competition(){}

    private List<String> imageLinks;

    private String text;

    private String winner;

    private Integer participants;

    @SerializedName("max_participants")
    private Integer maxParticipants;

    private String link;

    private Integer id;

    private String expires;

    private String action;

    private Pair<String, String> pairIdAndPostid;

    public List<String> getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(List<String> imageLinks) {
        this.imageLinks = imageLinks;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Pair<String, String> getPairIdAndPostid() {
        return pairIdAndPostid;
    }

    public void setPairIdAndPostid(Pair<String, String> pairIdAndPostid) {
        this.pairIdAndPostid = pairIdAndPostid;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Integer getParticipants() {
        return participants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
