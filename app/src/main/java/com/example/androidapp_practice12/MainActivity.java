package com.example.androidapp_practice12;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.Telephony;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView callLogTextView;
    private TextView smsLogTextView;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        callLogTextView = findViewById(R.id.callLogHistory);
        smsLogTextView = findViewById(R.id.smsLogHistory);

        if (ContextCompat.checkSelfPermission
                (this,
                        Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED
        ) {
            testCallLogs();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR}, PERMISSION_REQUEST_CODE);
        } else {
            loadCallLogs();
            loadSMSLogs();
            addEventToCalendar();
        }

//        checkCameraHardware(this);
    }

    @SuppressLint("Range")
    private void loadCallLogs() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI, null, null, null);
        if(cursor!=null && cursor.moveToFirst()) {
            StringBuilder callLogs = new StringBuilder();
            do{
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                String callDate = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                String callDuration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                callLogs.append("Number: ").append(number)
                        .append(", Type: ").append(type)
                        .append(", Call Date: ").append(callDate)
                        .append(", Call Duration: ").append(callDuration)
                        .append(" sec. \n\n");
            } while(cursor.moveToNext());
            cursor.close();
            callLogTextView.setText(callLogs.toString());
        }
    }

    @SuppressLint("Range")
    private void loadSMSLogs() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                null,
                null,
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()){
            StringBuilder smsLogs = new StringBuilder();
            do {
                String sender = cursor.
                        getString(cursor.getColumnIndex
                                (Telephony.Sms.ADDRESS));
                String body = cursor.
                        getString(cursor.getColumnIndex
                                (Telephony.Sms.BODY));

                smsLogs.append("From: ").append(sender).
                        append(", Message: ").append(body).append("\n\n");

            } while (cursor.moveToNext());
            cursor.close();
            smsLogTextView.setText(smsLogs.toString());
        }
    }

    private void addEventToCalendar() {
        long callId = 1;
        long startMillis;
        long endMillis;

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2024, Calendar.OCTOBER, 20, 9, 13);
        startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(2024, Calendar.OCTOBER, 20, 10, 30);
        endMillis = endTime.getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, callId);
        values.put(CalendarContract.Events.TITLE, "Exam");
        values.put(CalendarContract.Events.DESCRIPTION, "Exam Project");
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Almaty");

        Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
        long eventId = ContentUris.parseId(uri);
        ContentValues remindereValues = new ContentValues();
        remindereValues.put(CalendarContract.Reminders.EVENT_ID, eventId);
        remindereValues.put(CalendarContract.Reminders.MINUTES, 10);
        remindereValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Toast.makeText(this, "Event added to calendar", Toast.LENGTH_SHORT).show();
    }

    private void testCallLogs() {
        StringBuilder callLogs = new StringBuilder();
        callLogs.append("Number: 88003555535, Type: Incoming, Date: 20/10/2024, Duration: 10sec \n\n");
        callLogs.append("Number: 88003555536, Type: Incoming, Date: 20/10/2024, Duration: 10sec \n\n");
        callLogs.append("Number: 88003555537, Type: Incoming, Date: 20/10/2024, Duration: 10sec \n\n");
        callLogs.append("Number: 88003555538, Type: Incoming, Date: 20/10/2024, Duration: 10sec \n\n");
    }

//    /** Check if this device has a camera */
//    private boolean checkCameraHardware(Context context) {
//        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
//            // this device has a camera
//            return true;
//        } else {
//            // no camera on this device
//            return false;
//        }
//    }
}