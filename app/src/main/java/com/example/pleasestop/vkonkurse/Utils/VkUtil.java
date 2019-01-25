package com.example.pleasestop.vkonkurse.Utils;

import android.util.Log;
import android.util.Pair;
import android.widget.ProgressBar;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class VkUtil {

    static List<Character> numMassive = new ArrayList<>();

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

        if(strings.length == 1) {
            strings = stringForParse.split("vk.com/club");
            if(strings.length == 1) {
                strings = stringForParse.split("vk.com/");
                for(int i = 1; i<strings.length; i++){
                    listId.add(customSplit(strings[i], '\n'));
                    return listId;
                }
            }
            for(int i = 1; i<strings.length; i++){
                listId.add(customSplit(strings[i], '\n'));
                return listId;
            }
        }
        for(int i = 1; i<strings.length; i++){
            listId.add(customSplit(strings[i], '|'));
        }
        for(String string : listId){
            string.replace(" ", "");
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

    public static String removeTextFromTwoSymbols(char firsSymbol, char secondSymbol, String text){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < text.length(); i++){
            if (text.charAt(i) == firsSymbol){
                while (true) {
                    if (text.charAt(i) == secondSymbol) {
                        break;
                    }
                    if(i<text.length())
                        i++;
                    else
                        break;
                }
            } else {
                stringBuilder.append(text.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * какой идентификатор группы - id или screenName
     */
    public static boolean idOrScreenNameOfGroup(String idGroup){
        idGroup.replace(" ", "");

        Log.i("groupcheck", idGroup + " - idOrScreenNameOfGroup чеканье " );
        numMassive = new ArrayList<>();
        numMassive.add('0');
        numMassive.add('1');
        numMassive.add('2');
        numMassive.add('3');
        numMassive.add('4');
        numMassive.add('5');
        numMassive.add('6');
        numMassive.add('7');
        numMassive.add('8');
        numMassive.add('9');
        boolean isId = true;
        for(char num : idGroup.toCharArray()){
            if(!numMassive.contains(num)) {
                isId = false;
                Log.i("groupcheck", idGroup + " - idOrScreenNameOfGroup аааааааааааааааааааааааааааааааааааа" );
                break;
            }
        }
        return isId;
    }
    public static Integer getTimeForVkDelay(){
        Random rnd = new Random(System.currentTimeMillis());
        return Constans.MIN_ADDED_NUM_VK_DEKAY
                + rnd.nextInt(Constans.MAX_ADDED_NUM_VK_DEKAY - Constans.MIN_ADDED_NUM_VK_DEKAY + 1);
    }
}
