package com.example.pleasestop.vkonkurse;

public class Constans {

    public static final String TOKEN = "token";
    public static final String USER_ID = "user_id";
    public static String REJECT = "REJECT";
    public static String ALLOWED = "ALLOWED";
    public static String REJECT_FOREVER = "REJECT_FOREVER";

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
