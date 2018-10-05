package com.currentaffairs21.elias.slideimageexample;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private List<HotOffer> hotOfferList;
    private ViewPager viewPager;
    private Button btn_uploadHotOffers;
    private FirebaseFirestore firebaseFirestore;
    private LikeButton star_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hotOfferList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();


        viewPager = findViewById(R.id.view_pager);
        star_button = findViewById(R.id.star_button);
        star_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeButton.setLiked(true);
                Toast.makeText(MainActivity.this, "Likerd", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(MainActivity.this, "Dislike", Toast.LENGTH_SHORT).show();

            }
        });

        firebaseFirestore.collection("Hot_Posts").addSnapshotListener(MainActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String hotOffer_id = doc.getDocument().getId();
                            //final Sms sms = doc.getDocument().toObject(Sms.class).withId(sms_id);
                            final HotOffer hotOffer = doc.getDocument().toObject(HotOffer.class).withId(hotOffer_id);
                            hotOfferList.add(hotOffer);
                        }
                    }
                }else{
                    //Toast.makeText(getActivity(),R.string.tabMore, Toast.LENGTH_SHORT).show();
                   // progressDialog.dismiss();
                }
                ViewPagerAdapter adapter = new ViewPagerAdapter(MainActivity.this, hotOfferList);
                viewPager.setAdapter(adapter);
            }
        });


        btn_uploadHotOffers = findViewById(R.id.btn_uploadHotOffers);
        btn_uploadHotOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UploadHotOfferActivity.class);
                startActivity(intent);
            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimeTask(),4000,4000);
    }

    public class MyTimeTask extends TimerTask{

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager.getCurrentItem() == 0){
                        viewPager.setCurrentItem(1);
                    }else if(viewPager.getCurrentItem() == 1){
                        viewPager.setCurrentItem(2);
                    }else{
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
}
