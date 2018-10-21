package org.vontech.internalbilitytester;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SimpleExample extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_example);

        final TextView exampleText = findViewById(R.id.exampleText);
        final Typeface original = exampleText.getTypeface();

        findViewById(R.id.regularButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exampleText.setTypeface(original, Typeface.NORMAL);
            }
        });

        findViewById(R.id.boldButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exampleText.setTypeface(exampleText.getTypeface(), Typeface.BOLD);
            }
        });

        findViewById(R.id.italicButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exampleText.setTypeface(exampleText.getTypeface(), Typeface.ITALIC);
            }
        });

    }
}
