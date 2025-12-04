package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class ListingAdapter extends ArrayAdapter<String> {
    Context context;
    int[] images = {R.drawable.gardening, R.drawable.carmaintenance, R.drawable.babysitting, R.drawable.cooking,R.drawable.petcare,R.drawable.moving,R.drawable.miscellaneous};;
    ArrayList<Listing> listings;

    public ListingAdapter(Context context, ArrayList<Listing> listings) {
        super(context, R.layout.single_item, R.id.textView1, new String[listings.size()]);
        this.context = context;
        this.listings = listings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View singleItem = convertView;
        ProgramViewHolder holder = null;
        if(singleItem == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_item, parent, false);
            holder = new ProgramViewHolder(singleItem);
            singleItem.setTag(holder);
        }else{
            holder = (ProgramViewHolder) singleItem.getTag();
        }
        if(listings.size() <= position)
            return null;
        Listing l = listings.get(position);
        int category = 0;
        String cat = l.getCategory();
        switch(cat){
            case("Gardening"):
                category = 0;
                break;
            case("Car Maintenance"):
                category = 1;
                break;
            case("Babysitting"):
                category = 2;
                break;
            case("Cooking"):
                category = 3;
                break;
            case("Pet Care"):
                category = 4;
                break;
            case("Moving"):
                category = 5;
                break;
            case("Miscellaneous"):
                category = 6;
                break;
        }
        holder.itemImage.setImageResource(images[category]);
        holder.postingTitle.setText(l.getTitle());
        holder.postingUser.setText(l.getRequesterName());
        String rating = "";
        int theRating = hashCharToRange1to5(l.getRequesterName().charAt(0));
        for(int i = 0; i < theRating; i++)
            rating+="★";
        for(int i = 0; i < 5 - theRating; i++)
            rating+="☆";
        holder.rating.setText(rating);
        return singleItem;
    }
    @Override
    public int getCount() {
        // This is correct IF 'listings' is the variable you updated in setListings()
        return listings.size();
    }
    public static int hashCharToRange1to5(char c) {
        int hashCode = (int) c;
        hashCode = (hashCode * 31) ^ hashCode;
        int range0to4 = Math.abs(hashCode % 5);
        return range0to4 + 1;
    }
}
