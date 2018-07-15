package io.github.ama_csail.amaexampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.github.ama_csail.ama.AccessibleActivity;
import io.github.ama_csail.ama.menu.OnAccessibleMenuConnectedListener;
import io.github.ama_csail.ama.menu.OnInstructionsLoadedListener;

public class AboutActivity extends AccessibleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        enableMenu();

        setOnAccessibleMenuConnectedListener(new OnAccessibleMenuConnectedListener() {
            @Override
            public void configureMenu() {
                enableLanguageSettings();
            }
        });

    }
}
