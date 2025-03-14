package com.example.pb_nkm3;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView txtNama, txtEmail, txtNIM;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtNama = findViewById(R.id.txtNama);
        txtEmail = findViewById(R.id.txtEmail);
        txtNIM = findViewById(R.id.txtNIM);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nama = snapshot.child("username").getValue(String.class);
                        String email = snapshot.child("userEmail").getValue(String.class);
                        String nim = snapshot.child("userNIM").getValue(String.class);

                        // 🔥 Debugging log
                        Log.d("ProfileActivity", "Data dari Firebase: " + nama + ", " + email + ", " + nim);

                        txtNama.setText(nama != null ? nama : "Nama Tidak Ditemukan");
                        txtEmail.setText(email != null ? email : "Email Tidak Ditemukan");
                        txtNIM.setText(nim != null ? nim : "NIM Tidak Ditemukan");
                    } else {
                        Toast.makeText(ProfileActivity.this, "Data tidak ditemukan!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ProfileActivity", "Database error: " + error.getMessage());
                }
            });
        } else {
            Toast.makeText(this, "User tidak login!", Toast.LENGTH_SHORT).show();
        }
    }
}
