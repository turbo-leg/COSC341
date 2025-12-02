package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView lvHistory;
    private TextView tvNoHistory;
    private DatabaseReference mDatabase;
    private List<Listing> historyList;
    private List<String> displayList;
    private ArrayAdapter<String> adapter;

    // Hardcoded user for now, matching other activities
    private final String CURRENT_USER_NAME = "John Doe";
    // In a real app, we would use IDs. Assuming names are unique for this prototype or we'd match IDs.
    private final String CURRENT_USER_ID = "user1"; // Placeholder ID for John Doe

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lvHistory = findViewById(R.id.lvHistory);
        tvNoHistory = findViewById(R.id.tvNoHistory);

        historyList = new ArrayList<>();
        displayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        lvHistory.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance("https://neighborhood-help-exchange-default-rtdb.firebaseio.com/").getReference();

        fetchHistory();

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Listing selectedListing = historyList.get(position);
                openReviewPage(selectedListing);
            }
        });
    }

    private void fetchHistory() {
        // Assuming listings are at the root level based on the provided DB structure
        DatabaseReference postingsRef = mDatabase; 
        postingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                displayList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    // Skip the Reviews node
                    if ("Reviews".equals(postSnapshot.getKey())) continue;

                    Listing listing = null;
                    try {
                        listing = postSnapshot.getValue(Listing.class);
                    } catch (Exception e) {
                        continue;
                    }

                    if (listing != null && listing.getTitle() != null) {
                        listing.setId(postSnapshot.getKey()); // Ensure ID is set

                        // Normalize helper name
                        String helperName = listing.getHelperName();
                        if (helperName == null) helperName = listing.getHelper();
                        listing.setHelperName(helperName);

                        // Check if completed and user is involved
                        boolean isRequester = CURRENT_USER_NAME.equals(listing.getRequesterName());
                        boolean isHelper = CURRENT_USER_NAME.equals(listing.getHelperName());

                        // Include if the user is involved
                        if (isRequester || isHelper) {
                            historyList.add(listing);
                            String role = isRequester ? "Requester" : "Helper";
                            String otherParty = isRequester ? listing.getHelperName() : listing.getRequesterName();
                            if (otherParty == null) otherParty = "None";
                            
                            displayList.add(listing.getTitle() + " (" + role + ")\nWith: " + otherParty);
                        }
                    }
                }

                if (historyList.isEmpty()) {
                    tvNoHistory.setVisibility(View.VISIBLE);
                    lvHistory.setVisibility(View.GONE);
                } else {
                    tvNoHistory.setVisibility(View.GONE);
                    lvHistory.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openReviewPage(Listing listing) {
        if (!listing.isComplete()) {
            Toast.makeText(this, "Cannot review: Listing not complete", Toast.LENGTH_SHORT).show();
            return;
        }

        if (CURRENT_USER_NAME.equals(listing.getRequesterName())) {
            // I am the requester, reviewing the helper
            String helperName = listing.getHelperName();
            if (helperName == null) {
                Toast.makeText(this, "Cannot review: No helper assigned", Toast.LENGTH_SHORT).show();
                return;
            }
            // Pass listing ID and helper name
            ReviewActivity.start(this, listing.getId(), helperName);
        } else {
            // I am the helper.
            Toast.makeText(this, "Only requesters can leave reviews", Toast.LENGTH_SHORT).show();
        }
    }
}
