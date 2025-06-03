package com.calc.mui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView displayTextView;
    private CalculatorLogic calculatorLogic;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Set the custom toolbar as the app bar

        displayTextView = findViewById(R.id.display_text_view);
        calculatorLogic = new CalculatorLogic();

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Setup Navigation Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Set up click listeners for calculator buttons
        setupCalculatorButtons();

        // Initial display update
        updateDisplay();
    }

    private void setupCalculatorButtons() {
        // Number buttons
        findViewById(R.id.button_0).setOnClickListener(v -> calculatorLogic.appendDigit("0"));
        findViewById(R.id.button_1).setOnClickListener(v -> calculatorLogic.appendDigit("1"));
        findViewById(R.id.button_2).setOnClickListener(v -> calculatorLogic.appendDigit("2"));
        findViewById(R.id.button_3).setOnClickListener(v -> calculatorLogic.appendDigit("3"));
        findViewById(R.id.button_4).setOnClickListener(v -> calculatorLogic.appendDigit("4"));
        findViewById(R.id.button_5).setOnClickListener(v -> calculatorLogic.appendDigit("5"));
        findViewById(R.id.button_6).setOnClickListener(v -> calculatorLogic.appendDigit("6"));
        findViewById(R.id.button_7).setOnClickListener(v -> calculatorLogic.appendDigit("7"));
        findViewById(R.id.button_8).setOnClickListener(v -> calculatorLogic.appendDigit("8"));
        findViewById(R.id.button_9).setOnClickListener(v -> calculatorLogic.appendDigit("9"));
        findViewById(R.id.button_dot).setOnClickListener(v -> calculatorLogic.appendDigit("."));

        // Operation buttons
        findViewById(R.id.button_add).setOnClickListener(v -> calculatorLogic.setOperand("+"));
        findViewById(R.id.button_subtract).setOnClickListener(v -> calculatorLogic.setOperand("-"));
        findViewById(R.id.button_multiply).setOnClickListener(v -> calculatorLogic.setOperand("×")); // Use '×' as in layout
        findViewById(R.id.button_divide).setOnClickListener(v -> calculatorLogic.setOperand("÷"));   // Use '÷' as in layout

        // Special function buttons
        findViewById(R.id.button_equals).setOnClickListener(v -> calculatorLogic.calculateResult());
        findViewById(R.id.button_clear).setOnClickListener(v -> calculatorLogic.clear());
        findViewById(R.id.button_plus_minus).setOnClickListener(v -> calculatorLogic.toggleSign());
        findViewById(R.id.button_percentage).setOnClickListener(v -> calculatorLogic.calculatePercentage());

        // Attach a single listener to all buttons and use it to update display
        View.OnClickListener updateDisplayAfterClick = v -> updateDisplay();
        for (int id : new int[]{
                R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
                R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9,
                R.id.button_dot, R.id.button_add, R.id.button_subtract, R.id.button_multiply,
                R.id.button_divide, R.id.button_equals, R.id.button_clear, R.id.button_plus_minus,
                R.id.button_percentage
        }) {
            findViewById(id).setOnClickListener(view -> {
                // Call the specific logic first
                if (view.getId() == R.id.button_0) calculatorLogic.appendDigit("0");
                else if (view.getId() == R.id.button_1) calculatorLogic.appendDigit("1");
                else if (view.getId() == R.id.button_2) calculatorLogic.appendDigit("2");
                else if (view.getId() == R.id.button_3) calculatorLogic.appendDigit("3");
                else if (view.getId() == R.id.button_4) calculatorLogic.appendDigit("4");
                else if (view.getId() == R.id.button_5) calculatorLogic.appendDigit("5");
                else if (view.getId() == R.id.button_6) calculatorLogic.appendDigit("6");
                else if (view.getId() == R.id.button_7) calculatorLogic.appendDigit("7");
                else if (view.getId() == R.id.button_8) calculatorLogic.appendDigit("8");
                else if (view.getId() == R.id.button_9) calculatorLogic.appendDigit("9");
                else if (view.getId() == R.id.button_dot) calculatorLogic.appendDigit(".");
                else if (view.getId() == R.id.button_add) calculatorLogic.setOperand("+");
                else if (view.getId() == R.id.button_subtract) calculatorLogic.setOperand("-");
                else if (view.getId() == R.id.button_multiply) calculatorLogic.setOperand("×");
                else if (view.getId() == R.id.button_divide) calculatorLogic.setOperand("÷");
                else if (view.getId() == R.id.button_equals) calculatorLogic.calculateResult();
                else if (view.getId() == R.id.button_clear) calculatorLogic.clear();
                else if (view.getId() == R.id.button_plus_minus) calculatorLogic.toggleSign();
                else if (view.getId() == R.id.button_percentage) calculatorLogic.calculatePercentage();

                // Then update the display
                updateDisplay();
            });
        }
    }


    private void updateDisplay() {
        displayTextView.setText(calculatorLogic.getCurrentDisplay());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(findViewById(R.id.nav_view))) {
            drawer.closeDrawer(findViewById(R.id.nav_view));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_calculator) {
            // Already on calculator screen, just close drawer
            drawer.closeDrawer(findViewById(R.id.nav_view));
        } else if (id == R.id.nav_source_code) {
            openUrl(getString(R.string.dev_github_url)); // Assuming dev_github_url is the repo base
            Toast.makeText(this, "Opening Source Code in browser...", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_developed_by) {
            // This is the same as the header, perhaps show a dialog or just open the URL
            openUrl(getString(R.string.dev_github_url));
            Toast.makeText(this, "Opening developer's GitHub in browser...", Toast.LENGTH_SHORT).show();
        }

        drawer.closeDrawer(findViewById(R.id.nav_view));
        return true;
    }

    private void openUrl(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "No browser found or invalid URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}