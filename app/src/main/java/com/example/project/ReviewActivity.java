package com.example.project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewActivity extends AppCompatActivity {

    private TextView tvRevieweeName;
    private RatingBar ratingBar;
    private Button btnSubmitReview;

    private String listingId;
    private String revieweeName; // This is the helper's name

    private DatabaseReference mDatabase;

    public static void start(android.content.Context context, String listingId, String revieweeName) {
        android.content.Intent intent = new android.content.Intent(context, ReviewActivity.class);
        intent.putExtra("LISTING_ID", listingId);
        intent.putExtra("REVIEWEE_NAME", revieweeName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get data from Intent
        if (getIntent() != null) {
            listingId = getIntent().getStringExtra("LISTING_ID");
            revieweeName = getIntent().getStringExtra("REVIEWEE_NAME");
        }

        // Initialize Views
        tvRevieweeName = findViewById(R.id.tvRevieweeName);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);

        if (revieweeName != null) {
            tvRevieweeName.setText("Reviewing: " + revieweeName);
        }

        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });
    }

    private void submitReview() {
        float rating = ratingBar.getRating();

        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Review object
        // helper is revieweeName, listing is listingId
        Review review = new Review(revieweeName, listingId, rating);

        // Save to Firebase
        DatabaseReference reviewsRef = mDatabase.child("Reviews"); // Capitalized as per user snippet
        String reviewId = reviewsRef.push().getKey();

        if (reviewId != null) {
            reviewsRef.child(reviewId).setValue(review)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ReviewActivity.this, "Failed to submit review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}