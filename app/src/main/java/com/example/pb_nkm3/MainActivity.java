package com.example.pb_nkm3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextInputEditText username, password;
    CheckBox checkBoxes;
    Button btnLogin;
    TextView forgetPass, createAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        checkBoxes = findViewById(R.id.checkboxes);
        btnLogin = findViewById(R.id.btnLogin);
        forgetPass = findViewById(R.id.forgetpass);
        createAccount = findViewById(R.id.createaccount);

        btnLogin.setOnClickListener(view -> loginUser());

        forgetPass.setOnClickListener(view -> {
            Intent forget = new Intent(getApplicationContext(), ForgetPass.class);
            startActivity(forget);
        });

        createAccount.setOnClickListener(view -> {
            Intent create = new Intent(getApplicationContext(), CreateAccount.class);
            startActivity(create);
        });
    }

    private void loginUser() {
        String email = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // Validasi input kosong
        if (TextUtils.isEmpty(email)) {
            username.setError("Email tidak boleh kosong!");
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            password.setError("Password tidak boleh kosong!");
            return;
        }

        // Login dengan Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login berhasil, pindah ke halaman utama
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(home);
                        finish();
                    } else {
                        // Login gagal
                        Toast.makeText(MainActivity.this, "Login Gagal! Periksa kembali email dan password.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
