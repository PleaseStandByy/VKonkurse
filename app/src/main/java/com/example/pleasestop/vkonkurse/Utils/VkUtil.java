package com.example.pleasestop.vkonkurse.Utils;

import android.util.Log;
import android.util.Pair;

import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.R;

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

    public static List<String> getSponsorId(String stringForParse){
        String[] strings = stringForParse.split("club");

        List<String> listId = new ArrayList<>();
        for(int i = 1; i<strings.length; i++){
            listId.add(customSplit(strings[i], '|'));
        }

        return listId;
    }

    private static String customSplit(String text, char splitSymbol) {
        String resultText = "";
        for(char symbol : text.toCharArray()){
            if(symbol != splitSymbol){
                resultText += symbol;
            } else
                break;
        }
        return resultText;
    }
}
