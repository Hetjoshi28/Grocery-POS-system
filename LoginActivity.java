package com.berry.groceryapp;

import static com.berry.groceryapp.CommonUtils.PREFERENCES;
import static com.berry.groceryapp.CommonUtils.PREF_USERNAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.berry.groceryapp.databinding.ActivityLoginBinding;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    final static int REGISTER_REQUEST_CODE = 1;
    DataBaseHelper dataBaseHelper;
    private ActivityLoginBinding binding;

    private static final String LANGUAGE_PREF = "selected_language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();  // 🔁 Load saved language before UI
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();

        dataBaseHelper = new DataBaseHelper(LoginActivity.this);

        // 🔓 Auto login if already logged in
        if (isLoggedIn()) {
            String userName = CommonUtils.getStringPref(PREF_USERNAME, LoginActivity.this);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
            finish();
        }

        // 🔘 Login button click
        binding.buttonLogin.setOnClickListener(view1 -> {
            CommonUtils.hideKeyboard(this);
            if (isLoginCredentialsValid(view1)) {
                String userName = binding.editTextUsername.getText().toString().trim();
                String password = binding.editTextPassword.getText().toString().trim();

                if (dataBaseHelper.checkUserCredentials(userName, password)) {
                    saveLoginStatus(true);
                    CommonUtils.setStringPref(PREF_USERNAME, userName, LoginActivity.this);
                    Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
                    homeIntent.putExtra("USERNAME", userName);
                    startActivity(homeIntent);
                    finish();
                } else {
                    CommonUtils.showMaterialDialog(LoginActivity.this, this.getString(R.string.invalid_username_or_password), new android.content.DialogInterface.OnClickListener() { @Override public void onClick(android.content.DialogInterface dialogInterface, int i) { /* Do nothing */ } });
                }

            }
        });

        // 📝 Sign Up button click
        binding.buttonSignUp.setOnClickListener(view1 -> {
            CommonUtils.hideKeyboard(this);
            Intent registerIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivityForResult(registerIntent, REGISTER_REQUEST_CODE);
        });

        // 🌐 Language Switch
        binding.btnEnglish.setOnClickListener(v -> switchLanguage("en"));
        binding.btnHindi.setOnClickListener(v -> switchLanguage("hi"));
    }

    // ✅ Validate input
    protected boolean isLoginCredentialsValid(View view) {
        String userName = binding.editTextUsername.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            CommonUtils.showCustomSnackBar(view,
                    getString(R.string.please_enter_username_and_password));
            return false;
        }
        return true;
    }

    // ✅ Save login status
    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences preferences = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    // ✅ Check if already logged in
    private boolean isLoggedIn() {
        SharedPreferences preferences = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
    }

    // 🌐 Change app language and save
    private void switchLanguage(String languageCode) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LANGUAGE_PREF, languageCode);
        editor.apply();

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        recreate();  // Restart activity to apply new language
    }

    // 📦 Load saved language preference
    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String language = prefs.getString(LANGUAGE_PREF, "en"); // Default to English
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}
