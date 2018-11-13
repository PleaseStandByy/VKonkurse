package com.example.pleasestop.vkonkurse.model;

import com.google.gson.annotations.SerializedName;

public class DelayItems {

    @SerializedName("vk_delay")
    Integer vkDelay;

    @SerializedName("contest_request_delay")
    private  Integer contestRequestDelay;

    @SerializedName("contest_list_delay")
    private Integer contestListDelay;

    public Integer getVkDelay() {
        return vkDelay;
    }

    public void setVkDelay(Integer vkDelay) {
        this.vkDelay = vkDelay;
    }

    public Integer getContestRequestDelay() {
        return contestRequestDelay;
    }

    public void setContestRequestDelay(Integer contestRequestDelay) {
        this.contestRequestDelay = contestRequestDelay;
    }

    public Integer getContestListDelay() {
        return contestListDelay;
    }

    public void setContestListDelay(Integer contestListDelay) {
        this.contestListDelay = contestListDelay;
    }
}
