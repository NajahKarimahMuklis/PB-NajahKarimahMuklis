package com.example.pb_nkm3.expense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pb_nkm3.R;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<ExpenseItem> expenseList;

    public interface OnItemActionListener {
        void onEdit(int position, ExpenseItem item);
        void onDelete(int position);
    }

    private final OnItemActionListener listener;

    public ExpenseAdapter(List<ExpenseItem> ExpenseList, OnItemActionListener listener) {
        this.expenseList = ExpenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseItem item = expenseList.get(position);
        holder.txtNo.setText(String.valueOf(position + 1) + ".");
        holder.txtSource.setText(item.getCategory());
        holder.txtAmount.setText("Rp " + item.getAmount());
        holder.txtDate.setText(item.getDate());

        holder.btnOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(holder.itemView.getContext(), holder.btnOptions);
            popup.inflate(R.menu.item_menu); // <- bikin ini kalau belum ada
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.action_edit) {
                    listener.onEdit(position, item);
                    return true;
                } else if (menuItem.getItemId() == R.id.action_delete) {
                    listener.onDelete(position);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView txtNo, txtSource, txtAmount, txtDate;
        ImageButton btnOptions;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNo = itemView.findViewById(R.id.txtNo);
            txtSource = itemView.findViewById(R.id.txtSource);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnOptions = itemView.findViewById(R.id.btnOptions);
        }
    }
}
