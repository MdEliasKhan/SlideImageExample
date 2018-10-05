package com.currentaffairs21.elias.slideimageexample;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class HotOfferId {
    @Exclude
    public String hotOffer_id;

    public <T extends HotOffer> T withId(@NonNull final String id){
        this.hotOffer_id = id;
        return (T) this;
    }
}
