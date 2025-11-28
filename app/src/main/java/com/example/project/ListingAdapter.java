package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class ListingAdapter extends ArrayAdapter<String> {
    Context context;
    int[] images = {R.drawable.gardening, R.drawable.carmaintenance, R.drawable.babysitting, R.drawable.cooking,R.drawable.petcare,R.drawable.moving};;
    String[][] postingTitleAndUser;

    public ListingAdapter(Context context, String[][] postingTitleAndUser) {
        super(context, R.layout.single_item, R.id.textView1, new String[postingTitleAndUser.length]);
        this.context = context;
        this.postingTitleAndUser = postingTitleAndUser;
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
        int category = 0;
        switch(postingTitleAndUser[position][2]){
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
        }
        holder.itemImage.setImageResource(images[category]);
        holder.postingTitle.setText(postingTitleAndUser[position][0]);
        holder.postingUser.setText(postingTitleAndUser[position][1]);
        singleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You clicked: " + postingTitleAndUser[position][0], Toast.LENGTH_SHORT).show();
            }
        });
        return singleItem;
    }
}
