package com.example.expense2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expense2.data.MyDbHandler
import java.io.IOException
import java.text.DecimalFormat


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // the following code is to hide the default action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        setContentView(R.layout.activity_main)
        // this makes status bar visible
        val statusBarHeight = resources.getDimensionPixelSize(
            resources.getIdentifier("status_bar_height", "dimen", "android")
        )
        findViewById<View>(R.id.mainbar).setPadding(0, statusBarHeight, 0, 0)



        //instantiating database
        val db = MyDbHandler(this)

//        db.addTransaction(this,"Vadapaw","UPI", 15.00, "2023-08-19 02:00:00");
//        db.addTransaction(this,"Auto","UPI", 15.00, "2023-08-20 02:00:00");
//        db.addTransaction(this,"Train pass","UPI", 135.00, "2023-08-20 02:00:00");
//        db.addTransaction(this,"Frankie","UPI", 30.00, "2023-08-20 02:00:00");
//        db.addTransaction(this,"Auto","UPI", 15.00, "2023-08-20 02:00:00");
//
//        db.addTransaction(this,"College Project","Cash", 5.00, "2023-08-19 02:00:00");
//        db.addTransaction(this,"Stationary","UPI", 76.85, "2023-08-22 02:00:00");
//        db.addTransaction(this,"Netflix","Card", 300.50, "2023-08-22 02:00:00");
//        db.addTransaction(this,"Books","UPI", 150.00, "2023-09-22 02:00:00");
//        db.addTransaction(this,"Recharge","UPI", 229.00, "2023-10-22 02:00:00");
//        db.addTransaction(this,"Trip","UPI", 100.00, "2023-11-22 02:00:00");
//        db.addTransaction(this,"Lunch","UPI", 70.00, "2023-12-22 02:00:00");
//
//        db.addTransaction(this,"Gift","Cash", 150.00, "2023-01-22 02:00:00");
//        db.addTransaction(this,"Auto","UPI", 15.85, "2023-02-19 02:00:00");
//        db.addTransaction(this,"Shaurma","Card", 80.50, "2023-03-19 02:00:00");
//        db.addTransaction(this,"Recharge","UPI", 229.00, "2023-04-19 02:00:00");
//        db.addTransaction(this,"Topup","UPI", 15.00, "2023-05-19 02:00:00");
//        db.addTransaction(this,"Bill","UPI", 50.00, "2023-06-19 02:00:00");
//        db.addTransaction(this,"Gym","UPI", 250.00, "2023-07-19 02:00:00");
//
//        db.addTransaction(this,"auto","UPI", 15.00);
//        db.addTransaction(this,"auto","UPI", 15.00);
//        db.addTransaction(this,"vadapaw","UPI", 15.00);
//        db.addTransaction(this,"stationary","UPI", 15.00);
//        db.addTransaction(this,"Shopping","UPI", 700.00);
//        db.addTransaction(this,"Cosmetics","UPI", 500.00);
//        db.addTransaction(this,"Medicines","UPI", 500.00);
//------------------------------Form Submission--------------------------------------------------------------------------

        val submitButton: Button = findViewById(R.id.submitButton)
        val expenseHistoryButton: Button = findViewById(R.id.expenseHistory)
        val transactionNameEditText: EditText =findViewById(R.id.transaction_name_entered)

        val transactionAmountEditText: EditText =findViewById(R.id.amount_entered)

        submitButton.setOnClickListener{
            val radioGroup: RadioGroup = findViewById(R.id.transaction_type)

            try {
                val transactionName: String = transactionNameEditText.text.toString()
                val transactionAmountDisplay = DecimalFormat("#.00").format(transactionAmountEditText.text.toString().toDouble())
                val transactionAmount = transactionAmountDisplay.toDouble()

                val typeId: Int = radioGroup.checkedRadioButtonId
                var transactionType = ""
                if(typeId > 0){
                    val selectedRadioButton: RadioButton = findViewById(typeId)
                    transactionType = selectedRadioButton.text.toString()
                }
                if(transactionType.isEmpty()){
                    throw IOException()
                }
                db.addTransaction(this, transactionName,transactionType,transactionAmount)
            }
            catch (e: IOException){
                Toast.makeText(this,"Fill Every Field", Toast.LENGTH_SHORT).show()
                transactionNameEditText.requestFocus()
            }
            catch (e: NumberFormatException){
                Toast.makeText(this,"Fill Every Field", Toast.LENGTH_SHORT).show()
                transactionNameEditText.requestFocus()
            }

            transactionNameEditText.setText("")
            radioGroup.clearCheck()
            transactionAmountEditText.setText("")
        }
        //Open ExpenseHistory
        expenseHistoryButton.setOnClickListener{
            val intent = Intent(this@MainActivity, ExpenseHistory::class.java)
            startActivity(intent)
        }
    }
}