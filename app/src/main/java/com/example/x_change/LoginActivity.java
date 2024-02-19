package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private TextView pageHeading, otpHeading, resendOtp;
    private EditText contactInput, otpInput;
    private Button submit;
    private FirebaseAuth mAuth;
    private String varificationId;
    private boolean otpSent = false;
    String s;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode(); // this is the otp
            if (code != null) {
                varifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(LoginActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String varId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            varificationId = varId;
            super.onCodeSent(varificationId, token);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pageHeading = findViewById(R.id.login_pageTitle);
        otpHeading = findViewById(R.id.login_otpHeading);
        contactInput = findViewById(R.id.login_contact);
        otpInput = findViewById(R.id.login_OTP);
        submit = findViewById(R.id.loginBtn);
        resendOtp = findViewById(R.id.login_resend);

        mAuth = FirebaseAuth.getInstance();

        otpHeading.setVisibility(View.INVISIBLE);
        otpInput.setVisibility(View.INVISIBLE);
        resendOtp.setVisibility(View.INVISIBLE);
        submit.setText("Send OTP");

        s = getIntent().getStringExtra("title");
        if (s.equals("login")) {
            pageHeading.setText("Login");
        } else {
            pageHeading.setText("Contact");
        }

        submit.setOnClickListener(view -> {
            if (otpSent) {
                String code = otpInput.getText().toString();
                if (code.equals("")) {
                    Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    varifyCode(code);
                }
            } else {
                String phoneNumber = contactInput.getText().toString();
                if (phoneNumber.equals("")) {
                    Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                } else {
                    sendOTPCode(phoneNumber);
                }
                submit.setText("Verify");
                otpSent = true;
                otpHeading.setVisibility(View.VISIBLE);
                otpInput.setVisibility(View.VISIBLE);
                resendOtp.setVisibility(View.VISIBLE);
            }
        });

        resendOtp.setOnClickListener(view -> {
            String phoneNumber = contactInput.getText().toString();
            if (phoneNumber.equals("")) {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            } else {
                sendOTPCode(phoneNumber);
            }
        });
    }

    private void sendOTPCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void varifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(varificationId, code);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (s.equals("login")) {
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(this, CreateProfileActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
}