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
import androidx.core.view.GravityCompat;

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
    private HistoryAdapter adapter;

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
        adapter = new HistoryAdapter(this, historyList);
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
        
        // Setup Navigation Drawer (Basic)
        androidx.drawerlayout.widget.DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        android.widget.ImageButton hamButton = findViewById(R.id.hamButton);
        hamButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void fetchHistory() {
        // Fetch from root
        DatabaseReference postingsRef = mDatabase; 
        postingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();

                long count = snapshot.getChildrenCount();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    // Skip the Reviews node
                    if ("Reviews".equals(key)) continue;

                    // Check if this is a container node
                    if ("Listings".equals(key) || "Postings".equals(key)) {
                        for (DataSnapshot innerSnapshot : postSnapshot.getChildren()) {
                            processListingSnapshot(innerSnapshot);
                        }
                        continue;
                    }

                    // Otherwise try to process as a listing directly
                    processListingSnapshot(postSnapshot);
                }

                if (historyList.isEmpty()) {
                    tvNoHistory.setText("No transactions found.\nScanned " + count + " root items.");
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

    private void processListingSnapshot(DataSnapshot postSnapshot) {
        Listing listing = null;
        try {
            listing = postSnapshot.getValue(Listing.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
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
                     return;
                 }
                 listing.setTitle("Untitled Listing");
            }
            
            listing.setId(postSnapshot.getKey());

            // Normalize helper name
            String helperName = listing.getHelperName();
            if (helperName == null) helperName = listing.getHelper();
            listing.setHelperName(helperName);

            // Only show completed listings for the history
            if (listing.isComplete()) {
                historyList.add(listing);
            }
        }
    }

    private void openReviewPage(Listing listing) {
        // Simple demo: always allow review of the helper
        String helperName = listing.getHelperName();
        if (helperName == null) {
             // For demo purposes, if no helper is assigned, use a placeholder
             helperName = "Demo Helper";
        }
        ReviewActivity.start(this, listing.getId(), helperName);
    }

    private class HistoryAdapter extends ArrayAdapter<Listing> {
        public HistoryAdapter(android.content.Context context, List<Listing> listings) {
            super(context, 0, listings);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull android.view.ViewGroup parent) {
            if (convertView == null) {
                convertView = android.view.LayoutInflater.from(getContext()).inflate(R.layout.single_item, parent, false);
            }

            Listing listing = getItem(position);
            if (listing != null) {
                TextView tvTitle = convertView.findViewById(R.id.textView1);
                TextView tvUser = convertView.findViewById(R.id.textView2);
                android.widget.ImageView imageView = convertView.findViewById(R.id.imageView);

                tvTitle.setText(listing.getTitle());
                
                String helperName = listing.getHelperName();
                if (helperName == null) helperName = "Demo Helper";
                tvUser.setText("Helper: " + helperName);
                
                // Set icon based on category if possible, or default
                // For now, just keep the default or set a generic one
                // imageView.setImageResource(R.drawable.ic_history); // If we had one
            }

            return convertView;
        }
    }
}
