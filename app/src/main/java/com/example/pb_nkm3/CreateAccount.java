package com.example.pb_nkm3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pb_nkm3.Moduls.UserDetails;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {
    TextInputEditText namapengguna, email, katasandi, nimpengguna;
    Button btnSignUp;
    FirebaseAuth mAuth;
    private static final String TAG = "CreateAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        namapengguna = findViewById(R.id.namapengguna);
        email = findViewById(R.id.email);
        katasandi = findViewById(R.id.katasandi);
        nimpengguna = findViewById(R.id.nimpengguna);
        btnSignUp = findViewById(R.id.btnSignUp);
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(view -> {
            String username = namapengguna.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String password = katasandi.getText().toString().trim();
            String nim = nimpengguna.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                namapengguna.setError("Masukkan nama pengguna!");
                namapengguna.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                email.setError("Masukkan email yang valid!");
                email.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                katasandi.setError("Masukkan kata sandi!");
                katasandi.requestFocus();
                return;
            }
            if (password.length() < 6) {
                katasandi.setError("Kata sandi minimal 6 karakter!");
                katasandi.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(nim)) {
                nimpengguna.setError("Masukkan NIM!");
                nimpengguna.requestFocus();
                return;
            }

            registerUser(username, userEmail, password, nim);
        });
    }

    private void registerUser(String username, String email, String password, String nim) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser fUser = mAuth.getCurrentUser();
                if (fUser != null) {
                    // ✅ Kirim email verifikasi
                    fUser.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                        if (verifyTask.isSuccessful()) {
                            // ✅ Simpan data ke database tanpa menyimpan password
                            String uid = fUser.getUid();
                            UserDetails userDetails = new UserDetails(uid, username, email, password,nim);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(uid).setValue(userDetails).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(CreateAccount.this, "Akun berhasil dibuat. Silakan verifikasi email Anda!", Toast.LENGTH_LONG).show();
                                    mAuth.signOut(); // ✅ Logout setelah registrasi agar user harus verifikasi dulu

                                    // Pindah ke halaman login
                                    Intent intent = new Intent(CreateAccount.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(CreateAccount.this, "Gagal menyimpan data pengguna. Coba lagi.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(CreateAccount.this, "Gagal mengirim email verifikasi. Coba lagi.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(CreateAccount.this, "Pendaftaran gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
