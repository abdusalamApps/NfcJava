package com.example.nfcjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MainActivity extends AppCompatActivity {

    TextView resultTextView;
    EditText messageEditText;
    TextView tagContentTextView;
    Button writeButton;

    NfcAdapter mNfcAdapter;

    Context mainActivityContext;

    private void findViews() {
        resultTextView = findViewById(R.id.resultTextView);
        messageEditText = findViewById(R.id.messageEditText);
        tagContentTextView = findViewById(R.id.tagContentTextView);
        writeButton = findViewById(R.id.writeButtton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mainActivityContext = this;

    }

    @Override
    protected void onResume() {
        super.onResume();
        NFCUtilJava.enableNFCInForeground(mNfcAdapter, this, getClass());
    }

    @Override
    protected void onPause() {
        super.onPause();
        NFCUtilJava.disableNFCInForeground(mNfcAdapter, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tagContentTextView.setText(getPayload(intent));
        writeButton.setOnClickListener(v -> {
            boolean messageWrittenSuccessfully =
                    NFCUtilJava.createNFCMessage(messageEditText.getText().toString().trim(),
                            intent, mainActivityContext);
            if (messageWrittenSuccessfully) {
                resultTextView.setText("Successful Written to Tag");
            } else {
                resultTextView.setText("Something went wrong Try Again");

            }
        });
    }

    private String getPayload(Intent intent) {
        String payloadString = "";
        if (intent != null) {

            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables != null && parcelables.length > 0) {
                NdefMessage ndefMessage = (NdefMessage) parcelables[0];
                payloadString = new String(ndefMessage.getRecords()[0].getPayload(), UTF_8);

            } else {
                payloadString = "No NDEF messages found";
            }
        }
        return payloadString;
    }

}