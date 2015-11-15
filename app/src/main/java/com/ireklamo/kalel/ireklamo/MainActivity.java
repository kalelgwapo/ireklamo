package com.ireklamo.kalel.ireklamo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnClickListener {

    private Spinner spinner;
    private EditText subject;
    private EditText body;
    private boolean isOther;
    private EditText otherRecipient;
    private Button btnAttachment;
    private Uri URI = null;
    private int columnIndex;
    private String attachmentFile;
    private TextView tv_attach;

    private static final String[]paths = {"item 1", "item 2", "item 3"};
    private static final int PICK_FROM_GALLERY = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isOther = false;
        subject = (EditText) findViewById(R.id.subject);
        body = (EditText) findViewById(R.id.body);
        otherRecipient = (EditText) findViewById(R.id.recipient);
        //btnAttachment = (Button) findViewById(R.id.buttonAttachment);
        //tv_attach = (TextView) findViewById(R.id.tv_attach_id);
        otherRecipient.setVisibility(View.GONE);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("phoneNo", null, "sms message", null, null);


        spinner = (Spinner)findViewById(R.id.spinner);
       /* ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/
        //btnAttachment.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendEmail();
                // after sending the email, clear the fields
                subject.setText("");
                body.setText("");
            }
        });
    }

    protected void sendEmail() {
        String recipient;
        if(isOther)
        recipient = otherRecipient.getText().toString();
        else
        recipient = String.valueOf(spinner.getSelectedItem());


        if(!isEmailValid(recipient))
            Toast.makeText(MainActivity.this, "Email address invalid",
                    Toast.LENGTH_LONG).show();
        else {
            String[] recipients = {recipient};
            Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
            // prompts email clients only
            email.setType("message/rfc822");
           /* email.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            email.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);*/
            email.putExtra(Intent.EXTRA_EMAIL, recipients);
            email.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
            email.putExtra(Intent.EXTRA_TEXT, body.getText().toString());
          /*  email.putExtra(Intent.EXTRA_STREAM, getImageContentUri(this, new
                    File(attachmentFile)));*/
            try {
                // the user can choose the email client
                startActivity(Intent.createChooser(email, "Choose an email client from..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "No email client installed.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("About");
            alertDialogBuilder.setMessage("ireklamo is an Android mailer app that keeps a record of email addresses that users can send to for their complaints.\n\n" +
                    "If you cant find the email address you can looking for, select Other to place your own. For suggestions email me at kalelreyes@gmail.com.\n\n" +
                    "ireklamo is a free app with no ads.");
            alertDialogBuilder.show();
            return true;
        }

       /* if (id == R.id.action_donate) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Donate");
            alertDialogBuilder.setMessage("ireklamo is a free app with no ads and forever will be. If you'd like to support the developer, you can donate any amount.\n" +
                    "BPI Savings : 9829 2649 91 (Kevin Khalil Reyes)");
            alertDialogBuilder.show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        if(position == 6) {
            isOther = true;
            otherRecipient.setVisibility(View.VISIBLE);
        }
        else {
            isOther = false;
            otherRecipient.setVisibility(View.GONE);
        }
        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        if (v == btnAttachment) {
            openGallery();
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(
                Intent.createChooser(intent, "Complete action using"),
                0);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && data != null){
            Uri URI = data.getData();
            File myFile = new File(URI.getPath());
            attachmentFile = myFile.getAbsolutePath();
            /*String realPath;
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());*/

            //Log.e("File path:", "Path is " + getImageContentUri(this, new File(attachmentFile)).toString());
            tv_attach.setText(Uri.fromFile(new
                    File(attachmentFile)).toString());
            //setTextViews(Build.VERSION.SDK_INT, data.getData().getPath(),realPath);
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        cursor.moveToFirst();
        if (cursor != null) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }}
}
