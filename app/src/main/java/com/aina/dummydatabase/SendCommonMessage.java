package com.aina.dummydatabase;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SendCommonMessage extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    Boolean isSeviceMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_common_message);

        final EditText fromField = (EditText) findViewById(R.id.fromdate);
        final Calendar myCalendar2 = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                fromField.setText(sdf.format(myCalendar2.getTime()));
            }

        };

        fromField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SendCommonMessage.this, date2, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });




        final EditText toField = (EditText) findViewById(R.id.todate);

        final Calendar myCalendar3 = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date3 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar3.set(Calendar.YEAR, year);
                myCalendar3.set(Calendar.MONTH, monthOfYear);
                myCalendar3.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                toField.setText(sdf.format(myCalendar3.getTime()));
            }

        };

        toField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SendCommonMessage.this, date3, myCalendar3
                        .get(Calendar.YEAR), myCalendar3.get(Calendar.MONTH),
                        myCalendar3.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        isSeviceMessage = sharedpreferences.getBoolean("serviceMessage", false);


    }

    public void sendCommonMessage(View v){
        EditText reasonField = (EditText) findViewById(R.id.reason);
        EditText customMessageField = (EditText) findViewById(R.id.custommessage);
        EditText fromField = (EditText) findViewById(R.id.fromdate);
        EditText toField = (EditText) findViewById(R.id.todate);
        SQLiteDatabase mydb = openOrCreateDatabase("CustomerDB", Context.MODE_PRIVATE, null);
        String query_active = "SELECT name,mobile FROM customer WHERE activestat=1";
        Cursor c=mydb.rawQuery(query_active, null);

        if ((customMessageField.getText().toString().length() > 0) && (reasonField.getText().toString().length() > 0)){
            Toast.makeText(getBaseContext(),
                    "Error: Either Reason or Custom Message should be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        while(c.moveToNext()) {
            String nameCustomer = c.getString(0);
            String phoneNumber = c.getString(1);
            String message = null;
            if (reasonField.getText().toString().length() > 0){
                if (fromField.getText().toString().equals(toField.getText().toString())){
                    message = "Dear " + nameCustomer + ",\nDue to " + reasonField.getText().toString() +", gym will be closed on " + fromField.getText().toString() + ".\nSorry for the inconvenience :(\nThanks,\nBody Fitness Zone";
                }
                else{
                    message = "Dear " + nameCustomer + ",\nDue to " + reasonField.getText().toString() +", gym will be closed from " + fromField.getText().toString() + " to " + toField.getText().toString() + ".\nSorry for the inconvenience :(\nThanks,\nBody Fitness Zone";
                }
            }
            else{
                message = "Dear " + nameCustomer + ",\n" + customMessageField.getText().toString() + "\nThanks,\nBody Fitness Zone";
            }

            if (phoneNumber.length() > 0 && message.length() > 0) {
                Log.d("here","here");
                if(isSeviceMessage){
                    SendMessage sendSM = new SendMessage();
                    sendSM.execute(phoneNumber, message);
                }
                else{
                    sendSms(phoneNumber, message);
                }

            } else
                Toast.makeText(getBaseContext(),
                        "Please enter both phone number and message.",
                        Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getBaseContext(),
                "Message Sent Successfully",
                Toast.LENGTH_SHORT).show();
    }


    private void sendSms(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}