package com.example.project;

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
    String[][] postingTitleAndUser = {  {"Catering for Small Family Gathering", "John Doe", "Cooking"},
                                        {"Help Weeding Garden", "John Doe", "Gardening"},
                                        {"Help Moving Couch", "Rose Gale","Moving"},
                                        {"Babysitting for Wednesday","Jim Rolland","Babysitting"},
                                        {"Dog Walking for One Hour", "Cathy Franks","Pet Care"}};

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
        lvCategory = findViewById(R.id.listView);
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, postingTitleAndUser);
        lvCategory.setAdapter(categoryAdapter);
    }

}