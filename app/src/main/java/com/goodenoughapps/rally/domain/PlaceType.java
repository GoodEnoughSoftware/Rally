package com.goodenoughapps.rally.domain;

/**
 * Type for Google Place types
 * @author Aaron Vontell
 */
public enum PlaceType {

    BAR, BOOK_STORE, CAFE, LIBRARY, NIGHT_CLUB, PARK, RESTAURANT, SCHOOL, SHOPPING_MALL, UNIVERSITY;

    /**
     * Returns the string for use in HTTP calls
     * @return the string for use in HTTP calls
     */
    public String getCodeString() {
        return this.toString().toLowerCase();
    }

    /**
     * Returns the string for use within the app
     * @return the string for use within the app
     */
    public String getPresentableString() {

        String[] words = this.getCodeString().split("_");
        String result = "";
        for (String word : words) {
            result += word.substring(0,1).toUpperCase() + word.substring(1, word.length());
            result += " ";
        }

        return result.trim();

    }

}
