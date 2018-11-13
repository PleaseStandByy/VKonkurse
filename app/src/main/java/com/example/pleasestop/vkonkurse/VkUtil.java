package com.example.pleasestop.vkonkurse;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.pleasestop.vkonkurse.MainActivity.TAG;

public class VkUtil {



    public static Pair<String,String> getGroupAndPostIds(String link) {
        if(link.isEmpty()){
            return null;
        }
        List<Character> charList = new ArrayList<>();
        Integer parsePoint = 0;
        for(int i = 0; i<10 ; i++){
            charList.add((char) i);
        }
        charList.add('_');

        String[] partsLink = link.split("wall-");
        String[] parts = partsLink[1].split("_");

        Pair<String, String> pair = new Pair<>(parts[0],parts[1]);
        return pair;
    }

    public static String getSponsorId(String stringForParse){
        String[] strings = stringForParse.split("club");
        stringForParse = strings[1];
        strings = stringForParse.split("СПОНСОРА");
        return strings[0].substring(0,strings[0].length()-1);
    }
}
