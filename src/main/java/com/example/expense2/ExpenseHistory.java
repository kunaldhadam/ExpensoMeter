package com.example.expense2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expense2.adapter.RecyclerViewAdapter;
import com.example.expense2.data.MyDbHandler;
import com.example.expense2.model.transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.Objects;

public class ExpenseHistory extends AppCompatActivity implements RecyclerViewAdapter.methods{
    private ArrayList<transaction> transactionArrayList;
    private ArrayAdapter<String> arrayAdapter;
    StringBuilder csvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_history);

        //Home Button
        Button homeButton = findViewById(R.id.homebutton);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseHistory.this, MainActivity.class);
            startActivity(intent);
        });

        //import database
        MyDbHandler db = new MyDbHandler(ExpenseHistory.this);

        //Calender to select date
        EditText fromDate = findViewById(R.id.fromDate);
        EditText toDate = findViewById(R.id.toDate);
        Button submitButton = findViewById(R.id.submit);

        fromDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseHistory.this,
                    R.style.CustomDatePickerDialogTheme,
                    (datePicker, year1, month1, dayOfMonth) -> fromDate.setText(String.format("%02d-%02d-%d",dayOfMonth,month1+1,year1)),year,month,day);
            datePickerDialog.show();
        });
        toDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseHistory.this,
                    R.style.CustomDatePickerDialogTheme,
                    (datePicker, year1, month1, dayOfMonth) -> toDate.setText(String.format("%02d-%02d-%d",dayOfMonth,month1+1,year1)),year,month,day);
            datePickerDialog.show();
        });

        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //get data
        List<transaction> transactionList = db.getExpenseHistory();

        //initialize Recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ExpenseHistory.this));

        csvData = db.getExpenseHistoryCsv();

        submitButton.setOnClickListener(v -> {
            String userStartDateNormal = fromDate.getText().toString();
            String userEndDateNormal = toDate.getText().toString();

            //convert to yyyy-MM-dd
            try{LocalDate localDateStart = LocalDate.parse(userStartDateNormal, inputFormat);
            LocalDate localDateEnd = LocalDate.parse(userEndDateNormal, inputFormat);


            String startDate = localDateStart.format(outputFormat);
            String endDate = localDateEnd.format(outputFormat);

            transactionList.clear();
            transactionList.addAll(db.getExpenseHistory(startDate,endDate));

            //use recycler view
            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(ExpenseHistory.this, transactionList, this, db, this);
            recyclerView.setAdapter(recyclerViewAdapter);

            //Get Total Expenses
            List<transaction> dailyExpense = recyclerViewAdapter.transactionList;

            TextView totalDisplay = findViewById(R.id.totalDisplay);
            totalDisplay.setText(String.valueOf(db.getTotal(dailyExpense)));

            //Get Total Expenses
            dailyExpense.clear();
            dailyExpense.addAll(db.getExpenseHistory(startDate, endDate));
            totalDisplay.setText(String.valueOf(db.getTotal(dailyExpense)));

            //testing csv data
            StringBuilder csvData1 = new StringBuilder();
            csvData1 = db.getExpenseHistoryCsv(startDate, endDate);
            Button chatButton = findViewById(R.id.chatButton);
            StringBuilder finalCsvData = csvData1;
            chatButton.setOnClickListener(v1 -> {
                new Thread(()->{
                    String summary = getChatSummary(finalCsvData).toString();
                    new Handler(Looper.getMainLooper()).post(()->{
                        copyToClipboard(summary);
                        launchGPTwebsite();
                    });
                }).start();
            });
            //Launch Chart
            launchChart(db.getExpenseHistory(startDate, endDate));}
            catch (Exception e){
                Toast.makeText(this,"Select Both Range", Toast.LENGTH_SHORT).show();
            }
        });

        //use recycler view
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(ExpenseHistory.this, transactionList, this, db, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        //Get Total Expenses
        List<transaction> dailyExpense = recyclerViewAdapter.transactionList;
        getTotalDisplay(String.valueOf(db.getTotal(dailyExpense)));

        //Charts Begins
        launchChart(dailyExpense);
        int currentYear = LocalDate.now().getYear();
        List<Double> totalOfEachMonth = new ArrayList<>();

        // Iterate through each month
        for (Month month : Month.values()) {
            LocalDate firstDayOfMonth = LocalDate.of(currentYear, month, 1);
            LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
            String firstDayOfMonthString = String.valueOf(firstDayOfMonth);
            String lastDayOfMonthString = String.valueOf(lastDayOfMonth);
            totalOfEachMonth.add(db.getTotal(db.getExpenseHistory(firstDayOfMonthString,lastDayOfMonthString)));
        }
        launchBarChart(totalOfEachMonth);

        setUpUI();
    }
    private void setUpUI(){
        //chatButton
        Button chatButton = findViewById(R.id.chatButton);
        chatButton.setOnClickListener(v -> {
            new Thread(()->{
                String summary = getChatSummary(csvData).toString();
                new Handler(Looper.getMainLooper()).post(()->{
                    copyToClipboard(summary);
                    launchGPTwebsite();
                });
            }).start();
        });
    }
    private void copyToClipboard(String data){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("CSV Data", data);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Data is copied to clipboard", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void launchGPTwebsite(){
        String url = "https://chat.openai.com";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void getTotalDisplay(String string){
        TextView totalDisplay = findViewById(R.id.totalDisplay);
        totalDisplay.setText(string);
    }

    public void launchChart(List<transaction> transactionList){
        launchLineChart(transactionList);
        launchPieChart(transactionList);
    }

    public void launchLineChart(List<transaction> transactionList){
        List<Entry> amountList = new ArrayList<>();

        for(int i=0; i < transactionList.size(); i++){
            transaction transaction = transactionList.get(i);
            double amount = transaction.getTransaction_amount();
            amountList.add(new Entry( i , (float) amount));
        }
        LineChart lineChart = findViewById(R.id.LineChart);
        LineDataSet dataSet = new LineDataSet(amountList, "Expense Record");
        LineData lineData = new LineData(dataSet);
        dataSet.setColor(Color.GREEN);
        dataSet.setCircleColor(Color.GREEN);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    public void launchPieChart(List<transaction> transactionList){
        List<PieEntry> typeList = new ArrayList<>();
        int countUPI = 0;
        int countCash = 0;
        int countCard = 0;

        for(int i=0; i<transactionList.size(); i++){
            transaction transaction = transactionList.get(i);
            if(Objects.equals(transaction.getTransaction_type(), "Cash")){
                countCash = countCash + 1;
            }
            else if(Objects.equals(transaction.getTransaction_type(), "Card")){
                countCard = countCard + 1;
            }
            else if(Objects.equals(transaction.getTransaction_type(), "UPI")){
                countUPI = countUPI + 1;
            }
        }

        typeList.add(new PieEntry(countCash,"Cash"));
        typeList.add(new PieEntry(countCard,"Card"));
        typeList.add(new PieEntry(countUPI,"UPI"));

        PieDataSet dataSet = new PieDataSet(typeList,"Expense Type Analysis");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setValueTextSize(13);
        PieData pieData = new PieData(dataSet);
        PieChart pieChart = findViewById(R.id.PieChart);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    public void launchBarChart(List<Double> totalOfEachMonth){
        List<BarEntry> totalList = new ArrayList<>();

        for(int i = 0; i<totalOfEachMonth.size() ; i++){
            double total = totalOfEachMonth.get(i);
            totalList.add(new BarEntry((float) (i+1), (float) total));
        }

        BarDataSet dataSet = new BarDataSet(totalList, "Monthly Total");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        BarData barData = new BarData(dataSet);
        BarChart barChart = findViewById(R.id.BarChart);
        barChart.setData(barData);
        barChart.getXAxis().setGranularity(1f);
        barChart.invalidate();
    }

    public StringBuilder getChatSummary(StringBuilder csvData){
        StringBuilder chat = new StringBuilder();
        chat.append("This is my Expense History Database, \n " +
                "I want you to help me analyze this for me,\n" +
                "Currency : INR\n\n" +
                "column 1 : expense id\n" +
                "column 2 : expense name\n" +
                "column 3 : expense type\n" +
                "column 4 : expense amount\n" +
                "column 5 : expense date\n\n").append(csvData).append("\n\n").append("Give me Summarized analysis first");
        return chat;
    }
}