package com.example.project;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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
import java.util.List;
import java.text.DecimalFormat;
public class ViewStatistics extends AppCompatActivity {
    private boolean listingsLoaded = false;
    private boolean reviewsLoaded = false;
    private DatabaseReference root;
    private DrawerLayout drawerLayout;
    private ImageButton hamButton;
    private NavigationView navView;
    private int listingsCreated = 0;
    private int totalRating = 0;
    private int numberOfReviews = 0;
    private int listingsHelped = 0;

    //TODO implement to count people helped when data is in firebase
    //private int peopleHelped = 0;
    private int [] categoryMostHelped = {0,0,0,0,0,0,0};
    private int[] categoryMostListed = {0,0,0,0,0,0,0};

    //TODO implement to count most common help category when data is added to firebase
    //private int[] categoryMostHelped = {0,0,0,0,0,0,0};

    private final String user = "John Doe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_statistics);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        drawerLayout = findViewById(R.id.drawer_layout);
        hamButton = findViewById(R.id.hamButton);
        navView = findViewById(R.id.nav_view);

        // Open drawer on button click
        hamButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.closeHam) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (id == R.id.hamNewReq) {
                    Intent intent = new Intent(ViewStatistics.this, CreateHelpRequestActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamBrowse) {
                    Intent intent = new Intent(ViewStatistics.this, BrowseHelpRequestsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamReview) {
                    Intent intent = new Intent(ViewStatistics.this, HistoryActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamMessage) {
                    Intent intent = new Intent(ViewStatistics.this, MessagesListActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamStats) {
                    // Already here
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                
                return true;
            }
        });
        root = FirebaseDatabase.getInstance().getReference();
        getStatistics();
    }
    public void openLeaderboard(View view) {
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
    }

    public void getStatistics() { //gathers the statistics for the testuser
        // Fetch from root to handle different structures
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listingsCreated = 0;
                categoryMostListed = new int[]{0,0,0,0,0,0,0}; // Reset counts

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if ("Reviews".equals(key)) continue;

                    if ("Listings".equals(key) || "Postings".equals(key)) {
                        for (DataSnapshot innerSnapshot : postSnapshot.getChildren()) {
                            processListing(innerSnapshot);
                        }
                        continue;
                    }
                    processListing(postSnapshot);
                }
                listingsLoaded = true;
                if (reviewsLoaded) updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewStatistics.this, "Failed to load listings", Toast.LENGTH_SHORT).show();
            }
        });

        root.child("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot reviewsSnapshot) {
                numberOfReviews = 0;
                totalRating = 0;
                for (DataSnapshot review : reviewsSnapshot.getChildren()) {
                    // Check if review is for this user (as helper)
                    // The Review class has 'helper' field which is the name
                    String helperName = review.child("helper").getValue(String.class);
                    
                    if (user.equals(helperName)) {
                        numberOfReviews++;
                        // Handle both Double and Integer for rating
                        Object ratingObj = review.child("rating").getValue();
                        if (ratingObj instanceof Double) {
                            totalRating += (Double) ratingObj;
                        } else if (ratingObj instanceof Long) {
                            totalRating += (Long) ratingObj;
                        } else if (ratingObj instanceof Integer) {
                            totalRating += (Integer) ratingObj;
                        }
                    }
                }
                reviewsLoaded = true;
                if (listingsLoaded) updateUI();
            }

            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewStatistics.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processListing(DataSnapshot listingSnapshot) {
        String requesterName = listingSnapshot.child("requesterName").getValue(String.class);
        String category = listingSnapshot.child("category").getValue(String.class);
        String helper = listingSnapshot.child("helperName").getValue(String.class);
        Boolean complete = listingSnapshot.child("complete").getValue(Boolean.class);

        //increments if you list that category
        if (category != null && requesterName != null && requesterName.equals(user))
            incrementCategoryListed(category);
        //increments if you help that category
        if (category != null && helper != null && helper.equals(user))
            incrementCategoryHelped(category);

        //increments if listing was created by user
        if (requesterName != null && requesterName.equals(user)) {
            listingsCreated++;
        }
        if (complete != null && complete && helper != null && helper.equals(user)) {
            listingsHelped++;
        }
    }

    private void updateUI() {
        ((TextView)findViewById(R.id.numOfList2)).setText(Integer.toString(listingsCreated));
        String commonCatList = findHighestCat(categoryMostListed);
        ((TextView)findViewById(R.id.catneedhelp2)).setText(commonCatList);
        ((TextView)findViewById(R.id.numOfReview2)).setText(Integer.toString(numberOfReviews));
        if (numberOfReviews == 0){
            ((TextView) findViewById(R.id.avgReview2)).setText("N/A");
        }else {
            avgRating = (double) totalRating / numberOfReviews;
            DecimalFormat format = new DecimalFormat("#.##");
            String str = format.format(avgRating);
            ((TextView) findViewById(R.id.avgReview2)).setText(str);
        }
        String commonCatHelp = findHighestCat(categoryMostHelped);
        ((TextView)findViewById(R.id.catYouHelp2)).setText(commonCatHelp);
        ((TextView)findViewById(R.id.numOfHelp2)).setText(Integer.toString(listingsHelped));

    }
    public void incrementCategoryListed (String cat){
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
    public void incrementCategoryHelped (String cat){
        switch (cat){
            case "Gardening": categoryMostHelped[0]++; break;
            case "Car Maintenance": categoryMostHelped[1]++; break;
            case "Babysitting": categoryMostHelped[2]++; break;
            case "Cooking": categoryMostHelped[3]++; break;
            case "Pet Care": categoryMostHelped[4]++; break;
            case "Moving": categoryMostHelped[5]++; break;
            case "Miscellaneous": categoryMostHelped[6]++; break;
        }
    }
    public String findHighestCat(int [] cat){
        int max = 0;
        int maxidx = -1;
        for (int i = 0; i<7;i++){
            if (cat[i] > max) {
                max = cat[i];
                maxidx = i;
            }
        }
        String highestCat = "";
        switch (maxidx) {
            case -1:
                highestCat = "N/A";
                break;
            case 0:
                highestCat = "Gardening";
                break;
            case 1:
                highestCat = "Car Maintenance";
                break;
            case 2:
                highestCat = "Babysitting";
                break;
            case 3:
                highestCat = "Cooking";
                break;
            case 4:
                highestCat = "Pet Care";
                break;
            case 5:
                highestCat = "Moving";
                break;
            case 6:
                highestCat = "Miscellaneous";
                break;
        }
        return highestCat;
    }
    public void sendLeaderboard(View view){
        Intent intent = new Intent(ViewStatistics.this,LeaderBoardActivity.class);
        startActivity(intent);
    }
}