package com.example.project;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class CreateHelpRequestActivity extends AppCompatActivity {

    private TextInputEditText etRequestTitle, etAddress, etTime, etDescription;
    private AutoCompleteTextView actvCategory;
    private Button btnCreateRequest;
    private static final String CURRENT_USER_NAME = "John Doe";
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_help_request);

        // Initialize Firebase Database reference under a "Listings" node
        databaseReference = FirebaseDatabase
                .getInstance("https://neighborhood-help-exchange-default-rtdb.firebaseio.com/")
                .getReference("Listings");

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
        String requesterName = CURRENT_USER_NAME; //TODO: Replace with actual user name when User Authentication is implemented.

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
