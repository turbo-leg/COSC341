package com.example.project;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class CreateHelpRequestActivity extends AppCompatActivity {

    private TextInputEditText etRequestTitle, etAddress, etTime, etDescription;
    private AutoCompleteTextView actvCategory;
    private Button btnCreateRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_help_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        etRequestTitle = findViewById(R.id.etRequestTitle);
        etAddress = findViewById(R.id.etAddress);
        etTime = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);
        actvCategory = findViewById(R.id.actvCategory);
        btnCreateRequest = findViewById(R.id.btnCreateRequest);

        // Set up the category dropdown menu
        setupCategoryDropdown();

        // Set up the time picker dialog
        setupTimePicker();

        // Set listener for the create button
        btnCreateRequest.setOnClickListener(v -> createHelpRequest());
    }

    private void setupCategoryDropdown() {
        // Define the categories
        String[] categories = {"Cooking", "Gardening", "Moving", "Babysitting", "Pet Care", "Other"};
        // Create an adapter for the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);
    }

    private void setupTimePicker() {
        etTime.setOnClickListener(v -> {
            // Get current time to pre-fill the picker
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Create a new TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
                // Format the time and set it to the EditText
                String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                etTime.setText(time);
            }, hour, minute, false); // Use 'false' for 24-hour format, 'true' for AM/PM

            timePickerDialog.show();
        });
    }

    private void createHelpRequest() {
        // Get text from all fields
        String title = etRequestTitle.getText().toString().trim();
        String category = actvCategory.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Simple validation
        if (title.isEmpty() || category.isEmpty() || address.isEmpty() || time.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Data Handling Logic Goes Here ---
        // At this point, you have all the data. You can now:
        // 1. Create a "HelpRequest" model object.
        // 2. Save it to a database (like Firebase) or pass it back to a previous activity.
        // For now, we'll just show a success message.

        Toast.makeText(this, "Help request created successfully!", Toast.LENGTH_LONG).show();

        // Optional: finish this activity to return to the previous screen
        finish();
    }
}