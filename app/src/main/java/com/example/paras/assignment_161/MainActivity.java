package com.example.paras.assignment_161;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // creating instances of editText, buttons, textView
    EditText editText;
    Button saveButton, deleteButton;
    TextView textView;
    String dataString = "";
    private static final String EMPTY_STRING = "";

    // integer values of requests.
    private final int WRITE_REQUEST = 1;
    private final int DELETE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// initializing the views by referencing to their id in layout file.
        saveButton = (Button) findViewById(R.id.mySaveButton);
        deleteButton = (Button) findViewById(R.id.myDeleteButton);
        editText = (EditText) findViewById(R.id.myEditText);
        textView = (TextView) findViewById(R.id.myVisibleTextView);

// setting the listener to the buttons.
        saveButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);


    }// onCreate ends


    // overridden method on click of any button.
    @Override
    public void onClick(View v) {
        // if save is pressed.
        if (v.getId() == R.id.mySaveButton) {
            // take the data from the edit text and store it in string.
            dataString = editText.getText().toString();
            // create a new instance of the async task class and pass the string and write request while executing.
            new FetchData().execute(dataString, String.valueOf(WRITE_REQUEST));

        }
        // if delete is pressed.
        if (v.getId() == R.id.myDeleteButton) {
            // create a new instance of the async task class and pass the string and delete request while executing.
            new FetchData().execute(EMPTY_STRING, String.valueOf(DELETE_REQUEST));
        }
    }

    // private class extending async task.
    private class FetchData extends AsyncTask<String, String, String> {
        // initializing variables.
        String dataString = null;
        int receivedRequest = 0;
        File theFile;
        String read;
        // creating the instance of the progress bar and scroll view for showing loading effect while fetching data.
        ProgressBar myProgressBar = findViewById(R.id.myProgressBar);
        ScrollView myScrollView = findViewById(R.id.myScrollView);
        // a final string for triggering deletion of file.
        public static final String FILE_DELETE_TOKEN = "any garbage string $$#@@!!";

        // overridden method of the async task.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // setting progress bar visible and scroll view invisible.
            myProgressBar.setVisibility(View.VISIBLE);
            myScrollView.setVisibility(View.INVISIBLE);

        }

        // overridden method of the async task.
        @Override
        protected String doInBackground(String... strings) {
            // storing the passed strings in local variables.
            dataString = strings[0];
            receivedRequest = Integer.parseInt(strings[1]);
            // creating the file then reading it.
            theFile = createFileAsync();
            read = readAsync(theFile);

            // if the user enters valid string and requests for writing then,
            if (!dataString.isEmpty() && receivedRequest == WRITE_REQUEST) {
// write into the file the data appended with a new line character then read the file and return the read data.
                writeAsync(theFile, dataString + "\n");
                read = readAsync(theFile);
                return read;

                // if user requests for deleting the file then,
            } else if (receivedRequest == DELETE_REQUEST) {
                // delete the file.
                deleteAsync(theFile);

                // if user enters an empty string then,
            } else if (dataString.isEmpty()) {
                // read the file and return empty string.
                read = readAsync(theFile);
                return EMPTY_STRING;
            }

            // if deletion happens then delete token is returned.
            return FILE_DELETE_TOKEN;
        }

        // delete file method.
        private void deleteAsync(File file) {
            if (file.exists())
                file.delete();
        }

        // write into file method to create a file if not existing.
        private void writeAsync(File file, String dataStr) {
            if (!file.exists())
                file = createFileAsync();
            try {
                // use FileOutputStream to enter data in file.
                FileOutputStream f = new FileOutputStream(file, true);
                f.write(dataStr.getBytes());
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // reading from the file.
        private String readAsync(File file) {
            String ret = "";
            try {
                // use the FileInputStream to read complete file data from the file
                // then append line by line data to a string
                // then return that string.
                FileInputStream fis = new FileInputStream(file);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    ret = ret + strLine + "\n";
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }

        // method to create a file.
        private File createFileAsync() {
            // getting the external file path.
            File root = android.os.Environment.getExternalStorageDirectory();
            // creating a folder object with the path extracted and the folder name attached to the end of the file.
            File dir = new File(root.getAbsolutePath() + "/akasa");
            // if folder is not there then create the folder at the specified path.
                dir.mkdirs();
            // create a file inside the above created folder.
            File myFileTxt = new File(dir, "test.txt");
            // return the file.
            return myFileTxt;
        }


        // overridden method of the async task.
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // making the progress bar invisible and scroll view visible.
            myProgressBar.setVisibility(View.INVISIBLE);
            myScrollView.setVisibility(View.VISIBLE);

            // make textview visible.
            textView.setVisibility(View.VISIBLE);

            // if user enters empty string then prompt user to enter some non empty string to enter it in the file.
            if (s.equals(EMPTY_STRING))
                Toast.makeText(MainActivity.this, "Empty String", Toast.LENGTH_SHORT).show();

            // if file delete token is returned then make textview disappear.
            else if (s.equals(FILE_DELETE_TOKEN))
                textView.setVisibility(View.INVISIBLE);
            // set the returned text in the text view and clean the edit text.
            textView.setText(read);
            editText.setText("");
        }


    }


}//main activity ends

