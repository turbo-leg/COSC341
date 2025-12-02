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
import java.text.DecimalFormat;
public class ViewStatistics extends AppCompatActivity {
    private boolean listingsLoaded = false;
    private boolean reviewsLoaded = false;
    private DatabaseReference root;
    private ImageButton hamButton;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private int listingsCreated = 0;
    private int totalRating = 0;
    private int numberOfReviews = 0;

    //TODO implement to count people helped when data is in firebase
    //private int peopleHelped = 0;
    private String catMostNeedHelp;
    private int[] categoryMostListed = {0,0,0,0,0,0,0};

    //TODO implement to count most common help category when data is added to firebase
    //private int[] categoryMostHelped = {0,0,0,0,0,0,0};

    private final String user = "John Doe";
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

        // Optional: handle menu item clicks
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId(); // Get the clicked item's ID
            sendToChoice(id);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


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
                listingsLoaded = true;
                if (reviewsLoaded) updateUI();
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
                reviewsLoaded = true;
                if (listingsLoaded) updateUI();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewStatistics.this, "No Data to Read", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUI() {
        ((TextView)findViewById(R.id.numOfList2)).setText(Integer.toString(listingsCreated));
        String mostCommonCat = findHighestCat(categoryMostListed);
        ((TextView)findViewById(R.id.catneedhelp2)).setText(mostCommonCat);
        ((TextView)findViewById(R.id.numOfReview2)).setText(Integer.toString(numberOfReviews));
        if (numberOfReviews == 0){
            ((TextView) findViewById(R.id.avgReview2)).setText("N/A");
        }else {
            double avgRating = totalRating / (numberOfReviews + 0.0);
            DecimalFormat format = new DecimalFormat("#.##");
            String str = format.format(avgRating);
            ((TextView) findViewById(R.id.avgReview2)).setText(str);
        }

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
    public void sendToChoice(int id){
        if (id == R.id.closeHam) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.hamNewReq) {
            startActivity(new Intent(this, CreateHelpRequestActivity.class));
        } else if (id == R.id.hamBrowse) {
            startActivity(new Intent(this, BrowseHelpRequestsActivity.class));
        } else /*if (id == R.id.hamReview) {
            startActivity(new Intent(this, LeaveReviewActivity.class));
        } else if (id == R.id.hamMessage) {
            startActivity(new Intent(this, MessagesListActivity.class));
        } else*/ if (id == R.id.hamStats) {
            startActivity(new Intent(this, ViewStatistics.class));
        }
    }
}