package com.example.project;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BrowseHelpRequestsActivity extends AppCompatActivity {
    ListView lvCategory;
    DatabaseReference root;
    List<String[]> displayedPostings;
    String[][] postingTitleAndUser = {  {"Catering for Small Family Gathering", "John Doe", "Cooking"},
                                        {"Help Weeding Garden", "John Doe", "Gardening"},
                                        {"Help Moving Couch", "Rose Gale","Moving"},
                                        {"Babysitting for Wednesday","Jim Rolland","Babysitting"},
                                        {"Dog Walking for One Hour", "Cathy Franks","Pet Care"}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_help_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        root = FirebaseDatabase.getInstance("https://neighborhood-help-exchange-default-rtdb.firebaseio.com/").getReference();
        intializeList();
        lvCategory = findViewById(R.id.listView);
        ListingAdapter listingAdapter = new ListingAdapter(this, postingTitleAndUser);
        lvCategory.setAdapter(listingAdapter);
        setupSortListener();
    }

    public void setupSortListener(){

    }
    public void intializeList(){
        DatabaseReference postings = root.child("Postings");
        postings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postingSnapshot : snapshot.getChildren()) {
                    Log.e("COSC341", postingSnapshot.getKey() + " " + postingSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}