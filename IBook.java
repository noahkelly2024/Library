package com.pinetree;
import java.io.Serializable;

public interface IBook extends Serializable {
    String getTitle();
    String getAuthor();
    String getISBN();
    String getHomeLibrary();
}