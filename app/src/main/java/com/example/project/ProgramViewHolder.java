package com.example.project;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProgramViewHolder {

    ImageView itemImage;
    TextView postingTitle;
    TextView postingUser;
    TextView rating;
    ProgramViewHolder(View v){
        itemImage = v.findViewById(R.id.imageView);
        postingTitle = v.findViewById(R.id.textView1);
        postingUser = v.findViewById(R.id.textView2);
        rating = v.findViewById(R.id.textView4);
    }
}
