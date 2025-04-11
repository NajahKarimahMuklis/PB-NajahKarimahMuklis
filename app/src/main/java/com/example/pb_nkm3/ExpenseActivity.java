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

import com.example.pb_nkm3.expense.ExpenseAdapter;
import com.example.pb_nkm3.expense.ExpenseItem;
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

public class ExpenseActivity extends AppCompatActivity {

    private EditText editSource, editAmount;
    private Button btnAddExpense;
    private RecyclerView recyclerExpense;

    private ArrayList<ExpenseItem> ExpenseList;
    private ExpenseAdapter ExpenseAdapter;

    private DatabaseReference expenseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        editSource = findViewById(R.id.editSource);
        editAmount = findViewById(R.id.editAmount);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        recyclerExpense = findViewById(R.id.recyclerExpense);

        ExpenseList = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        expenseRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("expense");

        ExpenseAdapter = new ExpenseAdapter(ExpenseList, new ExpenseAdapter.OnItemActionListener() {
            @Override
            public void onEdit(int position, ExpenseItem item) {
                showEditDialog(item);
            }

            @Override
            public void onDelete(int position) {
                ExpenseItem item = ExpenseList.get(position);
                if (item.getKey() != null) {
                    expenseRef.child(item.getKey()).removeValue()
                            .addOnSuccessListener(unused -> Toast.makeText(ExpenseActivity.this, "Data dihapus", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(ExpenseActivity.this, "Gagal hapus: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });

        recyclerExpense.setLayoutManager(new LinearLayoutManager(this));
        recyclerExpense.setAdapter(ExpenseAdapter);

        setupexpenseListener();

        btnAddExpense.setOnClickListener(v -> {
            String source = editSource.getText().toString().trim();
            String amount = editAmount.getText().toString().trim();

            if (!source.isEmpty() && !amount.isEmpty()) {
                String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
                ExpenseItem newItem = new ExpenseItem(source, amount, currentDate);

                expenseRef.push().setValue(newItem)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ExpenseActivity.this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                            editSource.setText("");
                            editAmount.setText("");
                        })
                        .addOnFailureListener(e -> Toast.makeText(ExpenseActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupexpenseListener() {
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ExpenseList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ExpenseItem item = ds.getValue(ExpenseItem.class);
                    if (item != null) {
                        item.setKey(ds.getKey());
                        ExpenseList.add(item);
                    }
                }
                ExpenseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ExpenseActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(ExpenseItem item) {
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
                expenseRef.child(item.getKey()).setValue(item)
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


