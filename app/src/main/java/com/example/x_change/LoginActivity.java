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

import com.example.x_change.utility.Profile;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private TextView pageHeading, otpHeading, resendOtp;
    private EditText contactInput, otpInput;
    private Button submit;
    private FirebaseAuth mAuth;
    private String varificationId;
    private boolean otpSent = false;
    private boolean userExists = false;
    String s;
    private DatabaseReference reference;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode(); // this is the otp
            if (code != null) {
                verifyCode(code);
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

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(this, WelcomeActivity.class);
//        startActivity(intent);
//        finish();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        reference = FirebaseDatabase.getInstance().getReference().child("people");

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
                    verifyCode(code);
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
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Profile p = dataSnapshot.getValue(Profile.class);
                    if (p != null && p.contact.equals(contactInput.getText().toString())) {
                        userExists = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(varificationId, code);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (s.equals("login")) {
                            if (!userExists) {
                                Toast.makeText(this, "User not found, Signing Up", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, CreateProfileActivity.class);
                                intent.putExtra("contact", contactInput.getText().toString());
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else if (userExists) {
                            Toast.makeText(this, "User already exists, Logging in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(this, CreateProfileActivity.class);
                            intent.putExtra("contact", contactInput.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}
