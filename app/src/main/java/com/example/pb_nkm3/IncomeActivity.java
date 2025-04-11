package com.example.pb_nkm3;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pb_nkm3.income.IncomeAdapter;
import com.example.pb_nkm3.income.IncomeItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class IncomeActivity extends AppCompatActivity {

    private EditText editSource, editAmount;
    private Button btnAddIncome;
    private RecyclerView recyclerIncome;

    private ArrayList<IncomeItem> incomeList;
    private IncomeAdapter incomeAdapter;

    private DatabaseReference incomeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        editSource = findViewById(R.id.editSource);
        editAmount = findViewById(R.id.editAmount);
        btnAddIncome = findViewById(R.id.btnAddIncome);
        recyclerIncome = findViewById(R.id.recyclerIncome);

        incomeList = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        incomeRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("income");

        incomeAdapter = new IncomeAdapter(incomeList, new IncomeAdapter.OnItemActionListener() {
            @Override
            public void onEdit(int position, IncomeItem item) {
                showEditDialog(item);
            }

            @Override
            public void onDelete(int position) {
                IncomeItem item = incomeList.get(position);
                if (item.getKey() != null) {
                    incomeRef.child(item.getKey()).removeValue()
                            .addOnSuccessListener(unused -> Toast.makeText(IncomeActivity.this, "Data dihapus", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(IncomeActivity.this, "Gagal hapus: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });

        recyclerIncome.setLayoutManager(new LinearLayoutManager(this));
        recyclerIncome.setAdapter(incomeAdapter);

        setupIncomeListener();

        btnAddIncome.setOnClickListener(v -> {
            String source = editSource.getText().toString().trim();
            String amount = editAmount.getText().toString().trim();

            if (!source.isEmpty() && !amount.isEmpty()) {
                String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
                IncomeItem newItem = new IncomeItem(source, amount, currentDate);

                incomeRef.push().setValue(newItem)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(IncomeActivity.this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                            editSource.setText("");
                            editAmount.setText("");
                        })
                        .addOnFailureListener(e -> Toast.makeText(IncomeActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupIncomeListener() {
        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                incomeList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    IncomeItem item = ds.getValue(IncomeItem.class);
                    if (item != null) {
                        item.setKey(ds.getKey());
                        incomeList.add(item);
                    }
                }
                incomeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(IncomeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(IncomeItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Data");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 10);

        final EditText inputSource = new EditText(this);
        inputSource.setHint("Sumber");
        inputSource.setText(item != null ? item.getSource() : "");
        layout.addView(inputSource);


        final EditText inputAmount = new EditText(this);
        inputAmount.setHint("Jumlah");
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputAmount.setText(item != null ? item.getAmount() : "");
        layout.addView(inputAmount);

        builder.setView(layout);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String newSource = inputSource.getText().toString().trim();
            String newAmount = inputAmount.getText().toString().trim();

            if (!newSource.isEmpty() && !newAmount.isEmpty()) {
                item.setSource(newSource);
                item.setAmount(newAmount);
                incomeRef.child(item.getKey()).setValue(item)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(this, "Data diperbarui", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Gagal update: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Field tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }
}
