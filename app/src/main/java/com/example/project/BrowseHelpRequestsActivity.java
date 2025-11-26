package com.example.project;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BrowseHelpRequestsActivity extends AppCompatActivity {
    ListView lvCategory;
    String[] categoryName;
    String[] categoryDesc = {"Gardening", "Car Maintenance", "Babysitting", "Cooking","Pet Care", "Moving"};
    int[] categoryImages = {R.drawable.gardening, R.drawable.carmaintenance, R.drawable.babysitting, R.drawable.cooking,R.drawable.petcare,R.drawable.moving};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_help_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Context context = this;
        categoryName = this.getResources().getStringArray(R.array.category_names);
        lvCategory = findViewById(R.id.listView);
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categoryName, categoryImages, categoryDesc);
        lvCategory.setAdapter(categoryAdapter);
    }

}