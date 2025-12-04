package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LeaderBoardActivity extends AppCompatActivity {
    DatabaseReference root;
    HashMap<String, Integer> helperScores = new HashMap<>();
    ListView leaderboardList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leader_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        root = FirebaseDatabase.getInstance().getReference();
        leaderboardList = findViewById(R.id.leaderboard);
        readData();
    }
    public void readData(){
        root.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                int i = 0;
                for (DataSnapshot usersData : usersSnapshot.getChildren()) {
                    String username = usersData.child("name").getValue(String.class);
                    helperScores.put(username, 0);
                    i++;
                }
                root.child("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot reviewSnapshot) {
                        for (DataSnapshot reviewData: reviewSnapshot.getChildren()){
                            String helperName = reviewData.child("helper").getValue(String.class);
                            Integer rating = reviewData.child("rating").getValue(Integer.class);

                            if (rating != null && helperName != null && helperScores.containsKey(helperName)) {
                                int score = helperScores.get(helperName);
                                switch (rating) {
                                    case 1: helperScores.put(helperName, score - 30); break;
                                    case 2: helperScores.put(helperName, score - 10); break;
                                    case 3: helperScores.put(helperName, score + 20); break;
                                    case 4: helperScores.put(helperName, score + 30); break;
                                    case 5: helperScores.put(helperName, score + 50); break;
                                }
                            }

                        }
                        root.child("Listings").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot listingSnapshot) {
                                for (DataSnapshot listingData: listingSnapshot.getChildren()) {
                                    String helperNameListing = listingData.child("helper").getValue(String.class);
                                    Boolean complete = listingData.child("complete").getValue(Boolean.class);
                                    if (Boolean.TRUE.equals(complete) && helperNameListing != null && helperScores.containsKey(helperNameListing)) {
                                        helperScores.put(helperNameListing, helperScores.get(helperNameListing) + 30);
                                    }
                                }
                                buildLeaderboard();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(LeaderBoardActivity.this, "No Data to Read", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LeaderBoardActivity.this, "No Data to Read", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LeaderBoardActivity.this, "No Data to Read", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void buildLeaderboard() {
        ArrayList<String> display = new ArrayList<>();

        for (String key : helperScores.keySet()) {
            display.add(key + ": Helperscore of " + helperScores.get(key));
        }

        // sort by score
        Collections.sort(display, (a, b) -> {
            int scoreA = Integer.parseInt(a.substring(a.lastIndexOf(" ") + 1));
            int scoreB = Integer.parseInt(b.substring(b.lastIndexOf(" ") + 1));
            return scoreB - scoreA;
        });

        if (!display.isEmpty()) display.set(0, "1st Place! " + display.get(0));
        if (display.size() > 1) display.set(1, "2nd Place! " + display.get(1));
        if (display.size() > 2) display.set(2, "3rd Place! " + display.get(2));


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display);

        leaderboardList.setAdapter(adapter);
    }
    public void sendBack(View view){
        finish();
    }
}