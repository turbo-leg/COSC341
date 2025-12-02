package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messages_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SearchView search = findViewById(R.id.searchView);
        ListView list = findViewById(R.id.listView);
        ArrayList<String> citylist;
        ArrayAdapter<String> adapter;

        citylist = new ArrayList<>();
        citylist.add("John Doe");
        citylist.add("Rose Gale");
        citylist.add("Jim Rolland");
        citylist.add("Cathy Frank");
        citylist.add("Cohen Kucher");


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, citylist);
        list.setAdapter(adapter);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                Intent toDM = new Intent(MessagesListActivity.this, SendingReceivingMessagesActivity.class);
                Bundle nameBundle = new Bundle();
                nameBundle.putString("KEY", name);
                toDM.putExtras(nameBundle);
                startActivity(toDM);

            }
        });

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
                    Intent intent = new Intent(MessagesListActivity.this, CreateHelpRequestActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamBrowse) {
                    Intent intent = new Intent(MessagesListActivity.this, BrowseHelpRequestsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamReview) {
                    Intent intent = new Intent(MessagesListActivity.this, HistoryActivity.class);
                    startActivity(intent);
                } else if (id == R.id.hamMessage) {
                    // Already here
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (id == R.id.hamStats) {
                    Intent intent = new Intent(MessagesListActivity.this, ViewStatistics.class);
                    startActivity(intent);
                }

                return true;
            }
        });
    }
}