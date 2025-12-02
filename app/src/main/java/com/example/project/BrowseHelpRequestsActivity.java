package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
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

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageButton hamButton;

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

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        hamButton = findViewById(R.id.hamButton);

        hamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.hamReview) {
                    Intent intent = new Intent(BrowseHelpRequestsActivity.this, HistoryActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamStats) {
                    Intent intent = new Intent(BrowseHelpRequestsActivity.this, ViewStatistics.class);
                    startActivity(intent);
                }
                // Add other menu items here as needed
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
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