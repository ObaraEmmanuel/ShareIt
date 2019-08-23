package com.triedcoders.shareit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    protected EditText phoneNumber;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button signInButton = findViewById(R.id.signInButton);
        phoneNumber = findViewById(R.id.loginPhoneNumber);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        signInButton.setOnClickListener(
                v -> {
                    boolean isValid = validateInputs();
                    if(isValid){
                        Intent verifyScreen = new Intent(getApplicationContext(), AuthVerificationActivity.class);
                        verifyScreen.putExtra("phone", phoneNumber.getText().toString().trim());
                        startActivity(verifyScreen);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                }
        );
    }

    protected boolean validateInputs(){
        boolean validation = true;
        if(TextUtils.isEmpty(phoneNumber.getText())) {
            phoneNumber.setError("This field cannot be empty");
            validation = false;
        }else {
            phoneNumber.setError(null);
        }
        /*if(TextUtils.isEmpty(password.getText())) {
            password.setError("This field cannot be empty");
            validation = false;
        }else {
            password.setError(null);
        }*/
        return validation;
    }
}
