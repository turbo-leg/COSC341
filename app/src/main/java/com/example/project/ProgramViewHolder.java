package com.example.project;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProgramViewHolder {

    ImageView itemImage;
    TextView categoryName;
    TextView categoryDesc;
    ProgramViewHolder(View v){
        itemImage = v.findViewById(R.id.imageView);
        categoryName = v.findViewById(R.id.textView1);
        categoryDesc = v.findViewById(R.id.textView2);
    }
}
