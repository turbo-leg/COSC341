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

        // Use default instance which reads from google-services.json
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        // Fetch from root
        DatabaseReference postingsRef = mDatabase; 
        postingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                displayList.clear();

                long count = snapshot.getChildrenCount();
                // Toast.makeText(HistoryActivity.this, "Found " + count + " items", Toast.LENGTH_SHORT).show();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    // Skip the Reviews node
                    if ("Reviews".equals(postSnapshot.getKey())) continue;

                    Listing listing = null;
                    try {
                        listing = postSnapshot.getValue(Listing.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayList.add("Error parsing " + postSnapshot.getKey() + ": " + e.getMessage());
                        continue;
                    }

                    if (listing != null) {
                        // Manually check 'complete' field
                        if (!listing.isComplete()) {
                            Object completeObj = postSnapshot.child("complete").getValue();
                            if (completeObj instanceof Boolean) {
                                listing.setComplete((Boolean) completeObj);
                            } else if (completeObj instanceof String) {
                                listing.setComplete("true".equalsIgnoreCase((String) completeObj));
                            }
                        }

                        // If title is null, try to infer or set default
                        if (listing.getTitle() == null) {
                             if (listing.getRequesterName() == null && listing.getDesc() == null) {
                                 // Likely not a listing object
                                 displayList.add("Skipping " + postSnapshot.getKey() + ": No title/requester/desc");
                                 continue;
                             }
                             listing.setTitle("Untitled Listing");
                        }
                        
                        listing.setId(postSnapshot.getKey());

                        // Normalize helper name
                        String helperName = listing.getHelperName();
                        if (helperName == null) helperName = listing.getHelper();
                        listing.setHelperName(helperName);

                        // Add ALL listings for debugging, mark status
                        historyList.add(listing);
                        String status = listing.isComplete() ? "[DONE]" : "[OPEN]";
                        String display = status + " " + listing.getTitle();
                        if (listing.getHelperName() != null) {
                            display += "\nHelper: " + listing.getHelperName();
                        }
                        displayList.add(display);
                    } else {
                        displayList.add("Null listing for " + postSnapshot.getKey());
                    }
                }

                if (displayList.isEmpty()) {
                    tvNoHistory.setText("No transactions found.\nScanned " + count + " items.");
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
                Toast.makeText(HistoryActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
