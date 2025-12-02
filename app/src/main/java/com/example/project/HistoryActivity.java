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
                        e.printStackTrace();
                        continue;
                    }

                    if (listing != null) {
                        // Manually check 'complete' field to handle Boolean or String
                        if (!listing.isComplete()) {
                            Object completeObj = postSnapshot.child("complete").getValue();
                            if (completeObj instanceof Boolean) {
                                listing.setComplete((Boolean) completeObj);
                            } else if (completeObj instanceof String) {
                                listing.setComplete("true".equalsIgnoreCase((String) completeObj));
                            }
                        }

                        // If title is null, it might not be a valid listing, but let's try to use it if it has other fields
                        if (listing.getTitle() == null) {
                             // Try to see if it's a valid object
                             if (listing.getRequesterName() == null && listing.getDesc() == null) continue;
                             listing.setTitle("Untitled Listing");
                        }
                        
                        listing.setId(postSnapshot.getKey()); // Ensure ID is set

                        // Normalize helper name
                        String helperName = listing.getHelperName();
                        if (helperName == null) helperName = listing.getHelper();
                        listing.setHelperName(helperName);

                        // Include if the listing is complete
                        if (listing.isComplete()) {
                            historyList.add(listing);
                            String display = listing.getTitle();
                            if (listing.getHelperName() != null) {
                                display += "\nHelper: " + listing.getHelperName();
                            }
                            displayList.add(display);
                        }
                    }
                }

                if (historyList.isEmpty()) {
                    tvNoHistory.setText("No completed transactions found.\nScanned " + snapshot.getChildrenCount() + " items.");
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
        // Simple demo: always allow review of the helper
        String helperName = listing.getHelperName();
        if (helperName == null) {
             Toast.makeText(this, "No helper to review", Toast.LENGTH_SHORT).show();
             return;
        }
        ReviewActivity.start(this, listing.getId(), helperName);
    }
}
