package org.vontech.internalbilitytester;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SimpleExample extends AppCompatActivity {

    private TextView exampleText;
    private Typeface original;

    private final boolean shouldBeBad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_example);

        // Set re-used views and default values
        exampleText = findViewById(R.id.exampleText);
        original = exampleText.getTypeface();
        exampleText.setBackgroundColor(Color.GREEN);

        // Grab relevant views
        Button regularButton = findViewById(R.id.regularButton);
        Button boldButton = findViewById(R.id.boldButton);
        Button italicButton = findViewById(R.id.italicButton);

        final Context context = this;

        findViewById(R.id.dialogButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Test Dialog")
                        .setMessage("This is a test dialog that should be noticed during our run")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });

        // Now set listeners
        regularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGreenLabel();
            }
        });

        boldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRedLabel();
            }
        });

        italicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBlueLabel();
            }
        });

        if (shouldBeBad) {
            regularButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) { showGreenLabel(); }
                }
            });
        }

        regularButton.setVisibility(View.GONE);
        boldButton.setVisibility(View.GONE);

    }

    private void showGreenLabel() {
        exampleText.setTypeface(original, Typeface.NORMAL);
        exampleText.setBackgroundColor(Color.GREEN);
    }

    private void showRedLabel() {
        exampleText.setTypeface(exampleText.getTypeface(), Typeface.BOLD);
        exampleText.setBackgroundColor(Color.RED);
    }

    private void showBlueLabel() {
        exampleText.setTypeface(exampleText.getTypeface(), Typeface.ITALIC);
        exampleText.setBackgroundColor(Color.BLUE);
    }

}
