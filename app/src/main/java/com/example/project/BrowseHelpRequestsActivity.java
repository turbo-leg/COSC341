package com.example.project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class BrowseHelpRequestsActivity extends AppCompatActivity{
    ListView lvCategory;
    DatabaseReference listingRef;
    ArrayList<Listing> allListings = new ArrayList<>();
    ArrayList<Listing> filteredListings = new ArrayList<>();
    String[] filters;
    ArrayList<String> selectedCategories = new ArrayList<>();

    final LocalDateTime MIN_DATE_TIME = LocalDateTime.now();
    final LocalDateTime MAX_DATE_TIME = MIN_DATE_TIME.plusMonths(4);
    LocalDateTime minStartDateTime;

    private DrawerLayout drawerLayout;
    LocalDateTime maxStartDateTime;
    Spinner sortSpin;
    SearchView sv;


    ListingAdapter listingAdapter;
    String searchedText;
    FloatingActionButton fabAddRequest;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

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
        minStartDateTime = MIN_DATE_TIME;
        maxStartDateTime = MAX_DATE_TIME;
        filters = getResources().getStringArray(R.array.sort_options);
        selectedCategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.category_names)));
        listingRef = FirebaseDatabase.getInstance("https://neighborhood-help-exchange-default-rtdb.firebaseio.com/").getReference("Listings");

//        Listing l1 = new Listing(
//                "Catering for Small Family Gathering",
//                "John Doe",
//                "Cooking",
//                "Need some help assembling charcuterie boards for a small family gathering.",
//                "12/12/2025 09:00",
//                "1234 Button Road");
//        listingRef.child("l1").setValue(l1);
//        Listing l2 = new Listing(
//                "Help Weeding Garden",
//                "John Doe",
//                "Gardening",
//                "Need some help weeding my garden.",
//                "31/12/2025 11:00",
//                "1234 Button Road");
//        listingRef.child("l2").setValue(l2);
//        Listing l3 = new Listing(
//                "Help Moving Couch",
//                "Rose Gale",
//                "Moving",
//                "Giving away my couch to a family member and need help getting it to them.",
//                "09/12/2025 09:00",
//                "5678 Button Road");
//        listingRef.child("l3").setValue(l3);
//        Listing l4 = new Listing(
//                "Babysitting for Wednesday",
//                "Jim Rolland",
//                "Babysitting",
//                "Need someone to babysit my 2 kids while I'm out for dinner Wednesday night.",
//                "23/12/2025 16:00",
//                "2222 Button Road");
//        listingRef.child("l4").setValue(l4);
//        Listing l5 = new Listing(
//                "Dog Walking for One Hour",
//                "Cathy Franks",
//                "Pet Care",
//                "My dog needs its daily walk and I will not be home to do it.",
//                "07/12/2025 11:00",
//                "1111 Button Road");
//        listingRef.child("l5").setValue(l5);
        searchedText = "";
        lvCategory = findViewById(R.id.listView);
        sortSpin = findViewById(R.id.spinner);
        sv = findViewById(R.id.searchView);
        fabAddRequest = findViewById(R.id.fabAddRequest);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                searchedText = newText;
                filterThroughListings();
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        Log.e("COSC341", "Before intializeList()");
        intializeList();
        setupSortListener();
        setupListListener();
        fabAddRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to launch CreateHelpRequestActivity
                Intent intent = new Intent(BrowseHelpRequestsActivity.this, CreateHelpRequestActivity.class);
                startActivity(intent);
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton hamButton = findViewById(R.id.hamButton);
        NavigationView navView = findViewById(R.id.nav_view);

        // Open drawer on button click
        hamButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Optional: handle menu item clicks
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId(); // Get the clicked item's ID
            sendToChoice(id);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    public void setupSortListener(){
        ArrayAdapter sortAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, filters);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpin.setAdapter(sortAdapter);

        sortSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = sortSpin.getSelectedItem().toString();
                filterPopup(selectedFilter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void intializeList(){
        allListings.clear();
        listingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allListings.clear();
                if (dataSnapshot.exists()) {

                    for(DataSnapshot listingSnapshot : dataSnapshot.getChildren()){
                        Listing listing = listingSnapshot.getValue(Listing.class);
                        if(listing != null)
                            if (listing.helperName == null && !listing.isComplete())
                                allListings.add(listing);
                    }
                }
                if(!allListings.isEmpty()){
                    filteredListings = new ArrayList<>(allListings);
                    listingAdapter = new ListingAdapter(BrowseHelpRequestsActivity.this, filteredListings);
                    lvCategory.setAdapter(listingAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("COSC341", "HELLO4");
            }
        });

    }
    public void setupListListener(){
        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Listing l = filteredListings.get(position);
                String rating = "";
                int theRating = hashCharToRange1to5(l.getRequesterName().charAt(0));
                for (int i = 0; i < theRating; i++)
                    rating += "★";
                for (int i = 0; i < 5 - theRating; i++)
                    rating += "☆";
                Dialog dialog = new Dialog(BrowseHelpRequestsActivity.this);
                dialog.setContentView(R.layout.accept_request_dialog_layout);
                dialog.show();
                TextView titleText = dialog.findViewById(R.id.titleTV);
                TextView requesterText = dialog.findViewById(R.id.requesterTV);
                TextView ratingText = dialog.findViewById(R.id.ratingTV);
                TextView catText = dialog.findViewById(R.id.catTV);
                TextView timeText = dialog.findViewById(R.id.timeTV);
                TextView descText = dialog.findViewById(R.id.descTV);
                TextView addressText = dialog.findViewById(R.id.addressTV);

                titleText.setText(l.getTitle());
                requesterText.setText(l.getRequesterName());
                ratingText.setText(rating);
                catText.append(l.getCategory());
                timeText.append(l.getStartDateTime());
                descText.append(l.getDesc());
                addressText.append(l.getAddress());

                ImageView iv = dialog.findViewById(R.id.imageView6);
                iv.setImageResource(R.drawable.user);

                Button accept = dialog.findViewById(R.id.acceptBtn);
                Button back = dialog.findViewById(R.id.backBtn);

                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BrowseHelpRequestsActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Are You Sure?");
                        builder.setMessage("Do you want to accept this listing?");
                        builder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface alertDialog, int which) {
                                        alertDialog.dismiss();
                                        listingRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for(DataSnapshot listingSnapshot : snapshot.getChildren()){
                                                        Listing listing = listingSnapshot.getValue(Listing.class);
                                                        String key = listingSnapshot.getKey();
                                                        if(listing != null)
                                                            if (listing.getTitle().equals(l.getTitle())) {
                                                                l.setHelperName("Me");
                                                                listingRef.child(key).setValue(l);
                                                                Toast.makeText(BrowseHelpRequestsActivity.this, "Listing successfully accpeted!", Toast.LENGTH_SHORT).show();
                                                            }
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface alertDialog, int which) {
                                alertDialog.dismiss();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    public void filterPopup(String selectedFilter){
        switch(selectedFilter){
            case "Category":
                categoryPopup();
                break;
            case "Start Date":
                datePopup();
                break;
            default:
                break;
        }
        sortSpin.setSelection(0);
    }
    public void categoryPopup(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.category_dialog_layout);
        dialog.show();

        Button submit = dialog.findViewById(R.id.submitBtn);
        Button cancel = dialog.findViewById(R.id.cancelBtn);
        Button clear = dialog.findViewById(R.id.clearBtn);

        CheckBox c1 = dialog.findViewById(R.id.check1);
        CheckBox c2 = dialog.findViewById(R.id.check2);
        CheckBox c3 = dialog.findViewById(R.id.check3);
        CheckBox c4 = dialog.findViewById(R.id.check4);
        CheckBox c5 = dialog.findViewById(R.id.check5);
        CheckBox c6 = dialog.findViewById(R.id.check6);
        CheckBox c7 = dialog.findViewById(R.id.check7);

        for(String s : selectedCategories)
            switch (s){
                case "Gardening":
                    c1.setChecked(true);
                    break;
                case "Car Maintenance":
                    c2.setChecked(true);
                    break;
                case "Babysitting":
                    c3.setChecked(true);
                    break;
                case "Cooking":
                    c4.setChecked(true);
                    break;
                case "Pet Care":
                    c5.setChecked(true);
                    break;
                case "Moving":
                    c6.setChecked(true);
                    break;
                case "Miscellaneous":
                    c7.setChecked(true);
                    break;
            }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategories.clear();
                boolean valid = false;
                if(     seeIfChecked(c1) | seeIfChecked(c2) | seeIfChecked(c3) | seeIfChecked(c4) |
                        seeIfChecked(c5) | seeIfChecked(c6) | seeIfChecked(c7)
                )
                    valid = true;
                if(valid){
                    dialog.dismiss();
                    filterThroughListings();
                }else
                    Toast.makeText(getApplicationContext(), "Please select at least one category.", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.category_names)));
                filterThroughListings();
                dialog.dismiss();
            }
        });
    }
    public void datePopup(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.date_dialog_layout);
        dialog.show();

        Button submit = dialog.findViewById(R.id.submitBtn2);
        Button cancel = dialog.findViewById(R.id.cancelBtn2);
        Button clear = dialog.findViewById(R.id.clearBtn2);

        EditText etMinDate = dialog.findViewById(R.id.editTextMinDate);
        Spinner spinMinTime = dialog.findViewById(R.id.timeSpin);
        Spinner spinMinMerid = dialog.findViewById(R.id.timeSpin1);
        EditText etMaxDate = dialog.findViewById(R.id.editTextMaxDate);
        Spinner spinMaxTime = dialog.findViewById(R.id.timeSpin2);
        Spinner spinMaxMerid = dialog.findViewById(R.id.timeSpin3);


        if(!minStartDateTime.isEqual(MIN_DATE_TIME)){
            String minDay = String.valueOf(minStartDateTime.getDayOfMonth());
            String minMonth = String.valueOf(minStartDateTime.getMonthValue());
            if(minDay.length() == 1)
                minDay = "0" + minDay;
            if(minMonth.length() == 1)
                minMonth = "0" + minMonth;
            etMinDate.setText(minDay+"/"+minMonth+"/"+minStartDateTime.getYear());
        }
        if(!maxStartDateTime.isEqual(MAX_DATE_TIME)) {
            String maxDay = String.valueOf(maxStartDateTime.getDayOfMonth());
            String maxMonth = String.valueOf(maxStartDateTime.getMonthValue());
            if(maxDay.length() == 1)
                maxDay = "0" + maxDay;
            if(maxMonth.length() == 1)
                maxMonth = "0" + maxMonth;
            etMaxDate.setText(maxDay+"/"+maxMonth+"/"+maxStartDateTime.getYear());
        }
        etMinDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialog(etMinDate);
            }
        });
        etMaxDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialog(etMaxDate);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minStartDateTime = MIN_DATE_TIME;
                maxStartDateTime = MAX_DATE_TIME;
                dialog.dismiss();
                filterThroughListings();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String minDate = etMinDate.getText().toString();
                String minTime = spinMinTime.getSelectedItem().toString();
                String minMerid = spinMinMerid.getSelectedItem().toString();

                String maxDate = etMaxDate.getText().toString();
                String maxTime = spinMaxTime.getSelectedItem().toString();
                String maxMerid = spinMaxMerid.getSelectedItem().toString();
                if(minDate.isEmpty()){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a minimum date", Toast.LENGTH_SHORT);
                    toast.show();
                }else if(maxDate.isEmpty()){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a maximum date", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    int minValueTime;
                    if(minTime.length() == 4)
                        minValueTime = Integer.parseInt(minTime.substring(0, 1));
                    else
                        minValueTime = Integer.parseInt(minTime.substring(0, 2));
                    if (minMerid.equals("AM")) {
                        if (minValueTime == 12)
                            minValueTime = 0;
                    }else if (minValueTime != 12)
                        minValueTime += 12;
                    if(minValueTime < 10)
                        minTime = "0" + minValueTime + ":00";
                    else
                        minTime = minValueTime + ":00";

                    int maxValueTime;
                    if(maxTime.length() == 4)
                        maxValueTime = Integer.parseInt(maxTime.substring(0, 1));
                    else
                        maxValueTime = Integer.parseInt(maxTime.substring(0, 2));
                    if (maxMerid.equals("AM")) {
                        if (maxValueTime == 12)
                            maxValueTime = 0;
                    }else if (maxValueTime != 12)
                            maxValueTime += 12;
                    if(maxValueTime < 10)
                        maxTime = "0" + maxValueTime + ":00";
                    else
                        maxTime = maxValueTime + ":00";

                    Log.e("COSC341", "MinDateTime: " + minDate + " " + minTime);
                    Log.e("COSC341", "MaxDateTime: " + maxDate + " " + maxTime);

                    dialog.dismiss();
                    minStartDateTime = LocalDateTime.parse(minDate + " " + minTime, FORMATTER);
                    maxStartDateTime = LocalDateTime.parse(maxDate + " " + maxTime, FORMATTER);
                    filterThroughListings();
                }
            }
        });
    }

    public boolean seeIfChecked(CheckBox checkBox){
        if(checkBox.isChecked()) {
            selectedCategories.add(checkBox.getText().toString());
            return true;
        }else
            return false;
    }
    public void filterThroughListings(){
        filteredListings.clear();
        Iterator<Listing> iter = allListings.iterator();
        while(iter.hasNext()){
            Listing l = iter.next();
            if(selectedCategories.contains(l.getCategory())
                    && minStartDateTime.isBefore(LocalDateTime.parse(l.getStartDateTime(), FORMATTER))
                    && maxStartDateTime.isAfter(LocalDateTime.parse(l.getStartDateTime(), FORMATTER))
                    && (l.getTitle().toLowerCase().contains(searchedText.toLowerCase()) || l.getRequesterName().toLowerCase().contains(searchedText.toLowerCase()))){
                filteredListings.add(l);
            }
        }
        listingAdapter.notifyDataSetChanged();
    }

    public void openDateDialog(EditText et) {
        int currentYear = MIN_DATE_TIME.getYear();
        int currentMonth = MIN_DATE_TIME.getMonthValue() - 1;
        int currentDay = MIN_DATE_TIME.getDayOfMonth();
        DatePickerDialog theDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String theDay = String.valueOf(dayOfMonth);
                String theMonth = String.valueOf(month + 1);
                if(theDay.length() == 1)
                    theDay = "0" + theDay;
                if(theMonth.length() == 1)
                    theMonth = "0" + theMonth;
                et.setText(theDay+"/"+theMonth+"/"+String.valueOf(year));
            }
        }, currentYear,currentMonth,currentDay);

        theDialog.show();
    }

    public static int hashCharToRange1to5(char c) {
        int hashCode = (int) c;
        hashCode = (hashCode * 31) ^ hashCode;
        int range0to4 = Math.abs(hashCode % 5);
        return range0to4 + 1;
    }

    public void sendToChoice(int id){
        if (id == R.id.closeHam) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.hamNewReq) {
            startActivity(new Intent(this, CreateHelpRequestActivity.class));
        } else if (id == R.id.hamBrowse) {
            startActivity(new Intent(this, BrowseHelpRequestsActivity.class));
        } else if (id == R.id.hamReview) {
            startActivity(new Intent(this, ReviewActivity.class));
        } else if (id == R.id.hamMessage) {
            startActivity(new Intent(this, MessagesListActivity.class));
        } else if (id == R.id.hamStats) {
            startActivity(new Intent(this, ViewStatistics.class));
        }
    }
}