package com.example.tutoresi.model;

/**
 * Represents a rating
 */
public class Rating {

    private float rate;

    public Rating() {
        // Default constructor required for calls to DataSnapshot.getValue(Rating.class)
    }

    /**
     * Constructor of a rating
     * @param rate rate
     */
    public Rating(float rate){
        if(rate > 0){
            this.rate = roundToHalf(rate);
        }else{
           this.rate = 0;
        }
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    private float roundToHalf(float f) {
        return Math.round(f * 2) / 2.0f;
    }
}
