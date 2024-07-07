package com.example.expense2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense2.ExpenseHistory;
import com.example.expense2.R;
import com.example.expense2.data.MyDbHandler;
import com.example.expense2.model.transaction;

import java.text.DecimalFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public Context context;
    public List<transaction> transactionList;
    public Activity eActivity;
    public MyDbHandler tdb;
    private methods methods;

    public interface methods{
        void getTotalDisplay(String string);
        void launchChart(List<transaction> transactionList);
    }

    public RecyclerViewAdapter(Context context, List<transaction> transactionList, Activity activity, MyDbHandler db, methods md) {
        this.context = context;
        this.transactionList = transactionList;
        eActivity = activity;
        tdb = db;
        this.methods = md;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_history_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        transaction transaction = transactionList.get(position);

        holder.position = position;
        holder.transactionId = transaction.getId();
        holder.transactionName.setText(transaction.getTransaction_name());
        holder.transactionAmount.setText(new DecimalFormat("#.00").format(transaction.getTransaction_amount()));

    }

    @Override
    public int getItemCount() {
        if(transactionList != null){
            return transactionList.size();
        }
        else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView transactionName;
        public TextView transactionAmount;
        public int transactionId;
        public int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            transactionName = itemView.findViewById(R.id.transaction_name_textview);
            transactionAmount = itemView.findViewById(R.id.amount_textview);
        }

        @Override
        public void onClick(View view) {
            showDeleteDialogue(context, transactionId, position);
        }
    }
    public void showDeleteDialogue(Context context, int transaction_id, int position){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = eActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete_confirmation, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tdb.deleteTransaction(context, transaction_id);
                transactionList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, transactionList.size());
                methods.getTotalDisplay(new DecimalFormat("#.00").format(tdb.getTotal(transactionList)));
                methods.launchChart(transactionList);
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
