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
        DatabaseReference postingsRef = mDatabase.child("Postings");
        postingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                displayList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Listing listing = postSnapshot.getValue(Listing.class);
                    if (listing != null) {
                        listing.setId(postSnapshot.getKey()); // Ensure ID is set

                        // Check if completed and user is involved
                        // Note: Using names for comparison as per existing code patterns, ideally should be IDs
                        boolean isRequester = CURRENT_USER_NAME.equals(listing.getRequesterName());
                        boolean isHelper = CURRENT_USER_NAME.equals(listing.getHelperName());

                        // For testing purposes, we might want to show all listings or just completed ones
                        // listing.isComplete() should be checked.
                        // For now, I'll include it if the user is involved.
                        // TODO: Uncomment isComplete check when data is real
                        // if (listing.isComplete() && (isRequester || isHelper)) {

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
        String reviewerId = CURRENT_USER_ID;
        String revieweeId;
        String revieweeName;

        if (CURRENT_USER_NAME.equals(listing.getRequesterName())) {
            // I am the requester, reviewing the helper
            revieweeName = listing.getHelperName();
            revieweeId = "user_helper_placeholder"; // We need the helper's ID. Listing might not have it, only name.
        } else {
            // I am the helper, reviewing the requester
            revieweeName = listing.getRequesterName();
            revieweeId = "user_requester_placeholder";
        }

        if (revieweeName == null) {
            Toast.makeText(this, "Cannot review: No other party assigned", Toast.LENGTH_SHORT).show();
            return;
        }

        ReviewActivity.start(this, reviewerId, revieweeId, listing.getId(), revieweeName);
    }
}
