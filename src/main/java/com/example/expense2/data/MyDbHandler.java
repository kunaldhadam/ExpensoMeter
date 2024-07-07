package com.example.expense2.data;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.expense2.model.transaction;
import com.example.expense2.params.params;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyDbHandler extends SQLiteOpenHelper {

    public MyDbHandler(Context context) {
        super(context, params.DB_NAME, null, params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create= "CREATE TABLE "+ params.TABLE_NAME +
                "(" +
                params.transaction_id + " INTEGER PRIMARY KEY, " +
                params.transaction_name+ " TEXT, "+
                params.transaction_type + " TEXT, "+
                params.transaction_amount+ " DOUBLE, "+
                params.date+ " DATE DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Adding Transactions
    @SuppressLint("SimpleDateFormat")
    public void addTransaction(Context context, String transaction_name, String transaction_type, double transaction_amount){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();

        values.put(params.transaction_name, transaction_name);
        values.put(params.transaction_type, transaction_type);
        values.put(params.transaction_amount, transaction_amount);
        values.put(params.date, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        db.insert(params.TABLE_NAME, null, values);
        makeText(context, "Transaction Added", Toast.LENGTH_SHORT).show();
        db.close();
    }
    public void addTransaction(Context context, String transaction_name, String transaction_type, double transaction_amount, String t_date){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();

        values.put(params.transaction_name, transaction_name);
        values.put(params.transaction_type, transaction_type);
        values.put(params.transaction_amount, transaction_amount);
        values.put(params.date, t_date);

        db.insert(params.TABLE_NAME, null, values);
        makeText(context, "Transaction Added", Toast.LENGTH_SHORT).show();
        db.close();
    }

    //Updating Transactions
    public int updateTransaction(transaction transaction){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(params.transaction_name, transaction.getTransaction_name());
        values.put(params.transaction_type, transaction.getTransaction_type());
        values.put(params.transaction_amount, transaction.getTransaction_amount());

        //Updation
        return db.update(params.TABLE_NAME, values, params.transaction_id + "=?",
                new String[]{
                        String.valueOf(transaction.getId())
                });
    }

    //Deleting Transactions
    public void deleteTransaction(Context context, int id){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(params.TABLE_NAME,params.transaction_id + "=?", new String[]{String.valueOf(id)});
        Toast.makeText(context, "Record Deleted", Toast.LENGTH_SHORT).show();
        db.close();
    }

    public void deleteAllTransaction(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + params.TABLE_NAME;
        db.execSQL(query);
    }

    //Reading Transactions
    String startDate;
    String endDate;

    public String queryForDate(String startDate, String endDate){
        //user defined date
        String userStartDate = "datetime('" + startDate +" 00:00:00')";
        String userEndDate = "datetime('" + endDate + " 23:59:59')";
        //Generate Query
        return "SELECT * FROM " + params.TABLE_NAME + " WHERE " + params.date + " BETWEEN " + userStartDate +" AND "+ userEndDate;
    }
    public List<transaction> getExpenseHistory(){
        List<transaction> transactionList= new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        //default date
        LocalDate dateNow = LocalDate.now();
        String date = dateNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String defaultStartDate = "datetime('"+ date +" 00:00:00')";
        String defaultEndDate = "datetime('"+ date +" 23:59:59')";
        String select = "SELECT * FROM " + params.TABLE_NAME + " WHERE " + params.date + " BETWEEN " + defaultStartDate +" AND "+ defaultEndDate;
        Cursor cursor = db.rawQuery(select, null);
        //Loop for cursor
        if(cursor.moveToFirst()){
            do{
                transaction transaction = new transaction();
                transaction.setId(Integer.parseInt(cursor.getString(0)));
                transaction.setTransaction_name(cursor.getString(1));
                transaction.setTransaction_type(cursor.getString(2));
                transaction.setTransaction_amount(Double.parseDouble(cursor.getString(3)));
                transaction.setDate(cursor.getString(4));
                transactionList.add(transaction);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return transactionList;
    }
    public List<transaction> getExpenseHistory(String startDate1, String endDate1){
        startDate = startDate1;
        endDate = endDate1;
        List<transaction> transactionList= new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //default date
        String select = queryForDate(startDate, endDate);
        Cursor cursor = db.rawQuery(select, null);

        //Loop for cursor
        if(cursor.moveToFirst()){
            do{
                transaction transaction = new transaction();
                transaction.setId(Integer.parseInt(cursor.getString(0)));
                transaction.setTransaction_name(cursor.getString(1));
                transaction.setTransaction_type(cursor.getString(2));
                transaction.setTransaction_amount(Double.parseDouble(cursor.getString(3)));
                transaction.setDate(cursor.getString(4));
                transactionList.add(transaction);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return transactionList;
    }
    public List<transaction> getTotalExpenseHistory(){
        List<transaction> transactionList= new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + params.TABLE_NAME;
        Cursor cursor = db.rawQuery(select, null);
        //Loop for cursor
        if(cursor.moveToFirst()){
            do{
                transaction transaction = new transaction();
                transaction.setId(Integer.parseInt(cursor.getString(0)));
                transaction.setTransaction_name(cursor.getString(1));
                transaction.setTransaction_type(cursor.getString(2));
                transaction.setTransaction_amount(Double.parseDouble(cursor.getString(3)));
                transaction.setDate(cursor.getString(4));
                transactionList.add(transaction);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return transactionList;
    }

    public double getTotal(List<transaction> someList){
        double totalexpenseday = 0.00;

        for(transaction transaction : someList){
            totalexpenseday = transaction.getTransaction_amount() + totalexpenseday;
        }
        return totalexpenseday;
    }


    //ChatGPT
    public StringBuilder getExpenseHistoryCsv(){
        StringBuilder csvData = new StringBuilder();

        SQLiteDatabase db = this.getReadableDatabase();
        //default date
        LocalDate dateNow = LocalDate.now();
        String date = dateNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String defaultStartDate = "datetime('"+ date +" 00:00:00')";
        String defaultEndDate = "datetime('"+ date +" 23:59:59')";
        String select = "SELECT * FROM " + params.TABLE_NAME + " WHERE " + params.date + " BETWEEN " + defaultStartDate +" AND "+ defaultEndDate;
        Cursor cursor = db.rawQuery(select, null);
        //Loop for cursor
        if(cursor.moveToFirst()){
            do{
                String transaction_id = cursor.getString(0);
                String transaction_name = cursor.getString(1);
                String transaction_type = cursor.getString(2);
                String transaction_amount = cursor.getString(3);
                String transaction_time = cursor.getString(4);

                //Make CSV data
                csvData.append(transaction_id).append(",\t").append(transaction_name).append(",\t").append(transaction_type).append(",\t").append(transaction_amount).append(",\t").append(transaction_time).append("\n");
            }while(cursor.moveToNext());
        }
        cursor.close();
        return csvData;
    }
    public StringBuilder getExpenseHistoryCsv(String startDate1, String endDate1){
        startDate = startDate1;
        endDate = endDate1;
        StringBuilder csvData = new StringBuilder();
        SQLiteDatabase db = this.getReadableDatabase();

        //default date
        String select = queryForDate(startDate, endDate);
        Cursor cursor = db.rawQuery(select, null);

        //Loop for cursor
        if(cursor.moveToFirst()){
            do{
                String transaction_id = cursor.getString(0);
                String transaction_name = cursor.getString(1);
                String transaction_type = cursor.getString(2);
                String transaction_amount = cursor.getString(3);
                String transaction_time = cursor.getString(4);

                //Make CSV data
                csvData.append(transaction_id).append(",\t").append(transaction_name).append(",\t").append(transaction_type).append(",\t").append(transaction_amount).append(",\t").append(transaction_time).append("\n");
            }while(cursor.moveToNext());
        }
        cursor.close();
        return csvData;
    }
}
