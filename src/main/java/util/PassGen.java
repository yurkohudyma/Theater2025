package util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PassGen {

    public static String generate () {
        return generator(8);
    }

    private static String generator(int size) {
        StringBuilder result = new StringBuilder();
            for (int i = 0; i < size; i++) {
                result.append(randomize(93, +33));
            }
        return result.toString();
    }

    private static StringBuilder shuffle(StringBuilder str) {
        List<String> list = Arrays.asList(str.toString().split(""));
        Collections.shuffle(list);
        StringBuilder res = new StringBuilder();
        for (String s: list){
            res.append(s);
        }
        return res;
    }


    private static char randomize(int range, int startingPos) {
        return (char) (new Random().nextInt((range)) + startingPos);
    }
}
