package com.example.music_stream_application.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.example.music_stream_application.Fragments.Home_Fragment;
import com.example.music_stream_application.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText userName,email,password,conPassword;
    private MaterialButton singUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activty);

        userName = findViewById(R.id.userNameEd);
        email = findViewById(R.id.EmailEd);
        password = findViewById(R.id.passwordEd);
        conPassword = findViewById(R.id.ConfirmPasswordEd);
        singUpBtn = findViewById(R.id.signUpBtn);

        singUpBtn.setOnClickListener(v -> {
            String name = userName.getText().toString();
            String em = email.getText().toString();
            String pass = password.getText().toString();
            String conPass = conPassword.getText().toString();
            if (name.isEmpty() && em.isEmpty() && pass.isEmpty() && conPass.isEmpty() ){
                Toast.makeText(this, "one of the field is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(conPass)){
                Toast.makeText(this, "Password is not equal to confirm password", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "User Not Created\n" +task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

    }
}