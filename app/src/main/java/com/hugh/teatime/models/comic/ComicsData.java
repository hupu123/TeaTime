package com.hugh.teatime.models.comic;

import java.io.Serializable;
import java.util.ArrayList;

public class ComicsData implements Serializable {
    private int comicPosition;
    private ArrayList<Comic> comics;

    public ComicsData(int comicPosition, ArrayList<Comic> comics) {
        this.comicPosition = comicPosition;
        this.comics = comics;
    }

    public int getComicPosition() {
        return comicPosition;
    }

    public void setComicPosition(int comicPosition) {
        this.comicPosition = comicPosition;
    }

    public ArrayList<Comic> getComics() {
        return comics;
    }

    public void setComics(ArrayList<Comic> comics) {
        this.comics = comics;
    }

    public Comic getCurrentComic() {
        if (comics == null || comics.size() <= 0 || comicPosition < 0) {
            return null;
        }
        return comics.get(comicPosition);
    }
}
