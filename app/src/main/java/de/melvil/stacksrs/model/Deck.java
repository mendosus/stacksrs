package de.melvil.stacksrs.model;

import android.os.Environment;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {

    private String name;
    private String languageFront;
    private String languageBack;
    private boolean useTTS = false;

    private List<Card> stack = new ArrayList<>();
    private static Random random = new Random(System.currentTimeMillis());

    public Deck(){

    }

    public Deck(String name){
        this.name = name;
    }

    public static Deck loadDeck(String name) {
        try {
            Gson gson = new Gson();
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/StackSRS/" + name + ".txt");
            return gson.fromJson(FileUtils.readFileToString(file), Deck.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new Deck();
        }
    }

    public void saveDeck() {
        try {
            Gson gson = new Gson();
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/StackSRS/" + name + ".txt");
            FileUtils.writeStringToFile(file, gson.toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Card getNextCardToReview() {
        return stack.get(0);
    }

    public void putReviewedCardBack(boolean right) {
        Card card = stack.remove(0);
        if (right) {
            card.setLevel(card.getLevel() + 1);
            int newPos;
            if(card.getLevel() >= 10)
                newPos = Integer.MAX_VALUE;
            else
                newPos = 1 << (card.getLevel() * 2 + 2);
            newPos = newPos + random.nextInt((newPos / 3) + 1) - (newPos / 3);
            stack.add(Math.min(stack.size(), newPos), card);
        } else {
            card.setLevel(Math.max(0, card.getLevel() - 2));
            stack.add(2, card);
        }
        saveDeck();
    }

    public void addNewCard(Card newCard) {
        if(stack.size() < 3)
            stack.add(newCard);
        else
            stack.add(2, newCard);
        saveDeck();
    }

    public void editCurrentCard(String front, String back){
        stack.get(0).edit(front, back);
        saveDeck();
    }

    public boolean deleteCurrentCard(){
        if(stack.size() > 1) {
            stack.remove(0);
            saveDeck();
            return true;
        } else {
            return false;
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(stack);
        saveDeck();
    }

}