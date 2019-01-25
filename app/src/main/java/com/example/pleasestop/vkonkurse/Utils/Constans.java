package com.example.pleasestop.vkonkurse.Utils;

public class Constans {

    public static final String VK_URL = "https://vk.com/";
    public static final String TOKEN = "token";
    public static final String USER_ID = "user_id";
    public static final String IS_AUTO = "isAuto";
    public static final String ERROR_MESSAGE = "error";
    public static final String INFO_MESSAGE = "info";
    public static String REJECT = "REJECT";
    public static String ALLOWED = "ALLOWED";
    public static String REJECT_FOREVER = "REJECT_FOREVER";

    public static Integer MIN_ADDED_NUM_VK_DEKAY = -2;
    public static Integer MAX_ADDED_NUM_VK_DEKAY = 2;
    public static Integer delayGetWall = 110;

    public enum Modes {
        REJECT ("REJECT"),
        ALLOWED ("ALLOWED"),
        REJECT_FOREVER ("REJECT_FOREVER");

        private final String name;

        private Modes(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }
}
