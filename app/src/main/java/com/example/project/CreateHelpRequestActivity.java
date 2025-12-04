package com.example.project;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
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
        ImageButton backButton = findViewById(R.id.backButton);
        // Setup UI helpers
        setupCategoryDropdown();
        setupTimePicker();

        // Set listener for the create button
        btnCreateRequest.setOnClickListener(v -> createHelpRequest());
        // Set listener for the back button
        backButton.setOnClickListener(v -> finish());
    }

    private void setupCategoryDropdown() {
        String[] categories = {"Cooking", "Gardening", "Moving", "Babysitting", "Pet Care", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);
    }

    private void setupTimePicker() {
        // Get the parent layout for the time field
        com.google.android.material.textfield.TextInputLayout tilTime = findViewById(R.id.tilTime);

        // This is the main listener. Set it on the parent layout.
        tilTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // First, show a DatePickerDialog
            new android.app.DatePickerDialog(this, (dateView, selectedYear, selectedMonth, selectedDay) -> {
                // After a date is picked, show a TimePickerDialog
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(this, (timeView, selectedHour, selectedMinute) -> {
                    // Format and set the final date and time string
                    String dateTime = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d",
                            selectedYear, selectedMonth + 1, selectedDay, selectedHour, selectedMinute);
                    etTime.setText(dateTime);
                }, hour, minute, true).show(); // true for 24-hour format
            }, year, month, day).show();
        });

        // Also set a listener on the EditText itself to pass the click to its parent.
        // This makes the click behavior more reliable.
        etTime.setOnClickListener(v -> tilTime.performClick());
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
