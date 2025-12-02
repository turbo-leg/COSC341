package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SendingReceivingMessagesActivity extends AppCompatActivity {
    int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sending_receiving_messages);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        Button send = findViewById(R.id.sendButton);
        Button back = findViewById(R.id.backButton);
        TextView nameView = findViewById(R.id.textView);
        String name = bundle.getString("KEY");
        nameView.setText(name);


        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView text1 = findViewById(R.id.firstText);
                TextView text2 = findViewById(R.id.secondText);
                TextView text3 = findViewById(R.id.thirdText);
                TextView text4 = findViewById(R.id.fourthText);
                TextView sentText = findViewById(R.id.sendText);

                switch (counter) {
                    case 0:
                        text1.setText(sentText.getText());
                        sentText.setText("");
                        new android.os.Handler().postDelayed(() -> {
                            text2.setText("Im pretty good how are you?");
                        }, 3000);
                        break;
                    case 1:
                        text3.setText(sentText.getText());
                        sentText.setText("");
                        new android.os.Handler().postDelayed(() -> {
                            text4.setText("Sounds Great I'll see you there!");
                        }, 3000);
                        break;
                }
                counter++;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}