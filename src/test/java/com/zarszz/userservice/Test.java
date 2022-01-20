package com.zarszz.userservice;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String, Integer> counter = new HashMap<>();
        counter.put("a", 0);
        counter.put("i", 0);
        counter.put("u", 0);
        counter.put("e", 0);
        counter.put("o", 0);
        String input = "saya tinggal di jakarta".toLowerCase();
        String result = "";
        String[] splitted = input.split(" ");

        for (int i = splitted.length - 1; i >= 0; i--) {
            result = result + splitted[i] + " ";
        }
        System.out.println(result);

        for (char chr : input.toCharArray()) {
            String key = String.valueOf(chr);
            if (counter.containsKey(key)) {
                counter.put(key, counter.get(key) + 1);
            }   
        }
        System.out.println("Jumlah vocal : " + counter);
    }

}
