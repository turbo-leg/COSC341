package com.example.project;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class CreateHelpRequestActivity extends AppCompatActivity {

    private TextInputEditText etRequestTitle, etAddress, etTime, etDescription;
    private AutoCompleteTextView actvCategory;
    private Button btnCreateRequest;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_help_request);

        // Initialize Firebase Database reference under a "listings" node
        databaseReference = FirebaseDatabase.getInstance().getReference("listings");

        // Initialize UI components
        etRequestTitle = findViewById(R.id.etRequestTitle);
        etAddress = findViewById(R.id.etAddress);
        etTime = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);
        actvCategory = findViewById(R.id.actvCategory);
        btnCreateRequest = findViewById(R.id.btnCreateRequest);

        // Setup UI helpers
        setupCategoryDropdown();
        setupTimePicker();

        // Set listener for the create button
        btnCreateRequest.setOnClickListener(v -> createHelpRequest());

        // Setup Navigation Drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton hamButton = findViewById(R.id.hamButton);
        NavigationView navView = findViewById(R.id.nav_view);

        hamButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.closeHam) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (id == R.id.hamNewReq) {
                    // Already here
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (id == R.id.hamBrowse) {
                    Intent intent = new Intent(CreateHelpRequestActivity.this, BrowseHelpRequestsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamReview) {
                    Intent intent = new Intent(CreateHelpRequestActivity.this, HistoryActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamMessage) {
                    Intent intent = new Intent(CreateHelpRequestActivity.this, MessagesListActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamStats) {
                    Intent intent = new Intent(CreateHelpRequestActivity.this, ViewStatistics.class);
                    startActivity(intent);
                }

                return true;
            }
        });
    }

    private void setupCategoryDropdown() {
        String[] categories = {"Cooking", "Gardening", "Moving", "Babysitting", "Pet Care", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);
    }

    private void setupTimePicker() {
        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
                // Using a simpler date/time format for this example
                String date = String.format(Locale.getDefault(), "%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                etTime.setText(date + " " + time);
            }, hour, minute, false); // false for 24-hour format

            timePickerDialog.show();
        });
    }

    private void createHelpRequest() {
        String title = etRequestTitle.getText().toString().trim();
        String category = actvCategory.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String startDateTime = etTime.getText().toString().trim();
        String requesterName = " "; //TODO: Replace with actual user name when User has logic


        // Validation
        if (title.isEmpty() || category.isEmpty() || address.isEmpty() || startDateTime.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Firebase Data Handling Logic ---

        // Generate a unique ID using push()
        String listingId = databaseReference.push().getKey();
        if (listingId == null) {
            Toast.makeText(this, "Could not generate a listing ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Listing object
        Listing newListing = new Listing(title, requesterName, category, description, startDateTime, address);
        // Set the generated ID on the object
        newListing.setId(listingId);

        // Save the Listing object to Firebase
        databaseReference.child(listingId).setValue(newListing).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Help request posted successfully!", Toast.LENGTH_LONG).show();
                finish(); // Go back to the previous screen on success
            } else {
                String errorMessage = "Failed to post request.";
                if (task.getException() != null) {
                    errorMessage += " " + task.getException().getMessage();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
