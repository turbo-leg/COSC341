package com.example.project;

import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
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

import java.util.ArrayList;

public class ViewStatistics extends AppCompatActivity {
    private DatabaseReference root;
    private ImageButton hamButton;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private int listingsCreated = 0;
    private int totalRating = 0;
    private int numberOfReviews = 0;

    //TODO implement to count people helped when data is in firebase
    //private int peopleHelped = 0;
    private String mostPopularCategory;
    private int avgRating;
    private int[] categoryMostListed = {0,0,0,0,0,0,0};

    //TODO implement to count most common help category when data is added to firebase
    //private int[] categoryMostHelped = {0,0,0,0,0,0,0};

    private final String user = "John Doe";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_statistics);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
       /* drawerLayout = findViewById(R.id.drawer_layout);
        hamButton = findViewById(R.id.hamButton);
        navView = findViewById(R.id.nav_view);

        // Open drawer on button click
        btnHamburger.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Optional: handle menu item clicks
        navView.setNavigationItemSelectedListener(item -> {
            // Handle clicks here
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        */
        root = FirebaseDatabase.getInstance().getReference();
        getStatistics();


    }
    public void getStatistics() { //gathers the statistics for the testuser
        root.child("Listings").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot listingsSnapshot) {
                for (DataSnapshot listing : listingsSnapshot.getChildren()) {
                    String requesterName = listing.child("requesterName").getValue(String.class);
                    String category = listing.child("category").getValue(String.class);
                    if (category != null)
                        incrementCategory(category);

                    //increments if listing was created by user
                    if (requesterName != null && requesterName.equals(user)) {
                        listingsCreated++;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewStatistics.this, "No Data to Read", Toast.LENGTH_SHORT).show();
            }
        });

        root.child("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot reviewsSnapshot) {
                for (DataSnapshot review : reviewsSnapshot.getChildren()) {
                    String helperName = review.child("helper").getValue(String.class);
                    Integer rating = review.child("rating").getValue(Integer.class);

                    //increments if review was abt user
                    if (helperName != null && helperName.equals(user) && rating != null) {
                        numberOfReviews++;
                        totalRating += rating;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewStatistics.this, "No Data to Read", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void incrementCategory (String cat){
        switch (cat){
            case "Gardening": categoryMostListed[0]++; break;
            case "Car Maintenance": categoryMostListed[1]++; break;
            case "Babysitting": categoryMostListed[2]++; break;
            case "Cooking": categoryMostListed[3]++; break;
            case "Pet Care": categoryMostListed[4]++; break;
            case "Moving": categoryMostListed[5]++; break;
            case "Miscellaneous": categoryMostListed[6]++; break;
        }
    }
}