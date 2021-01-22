package com.example.nfcjava;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.widget.Toast;

import java.io.IOException;

public class NFCUtilJava {
    public static boolean createNFCMessage(String payload, Intent intent, Context context) {
        String pathPrefix = "peterjohnwelcome.com:nfcapp";
        NdefRecord nfcRecord = new NdefRecord(
                NdefRecord.TNF_EXTERNAL_TYPE,
                pathPrefix.getBytes(),
                new byte[0],
                payload.getBytes()
        );
        NdefMessage nfcMessage = new NdefMessage(nfcRecord);
        if (intent != null) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            return writeMessageToTag(nfcMessage, tag, context);
        }
        return false;
    }

    private static boolean writeMessageToTag(NdefMessage nfcMessage, Tag tag, Context context) {
        Ndef nDefTag = Ndef.get(tag);
        try {
            nDefTag.connect();

            if (nDefTag.getMaxSize() < nfcMessage.getByteArrayLength()) {
                return false;
            }
            if (nDefTag.isWritable()) {
                nDefTag.writeNdefMessage(nfcMessage);
                nDefTag.close();
                // Message is written to tag
                Toast.makeText(context, "Message written", Toast.LENGTH_LONG).show();

                return true;
            }
            if (!nDefTag.isWritable()) {
                Toast.makeText(context, "Tag is read only", Toast.LENGTH_LONG).show();
                return false;
            }

            NdefFormatable nDefFormatableTag = NdefFormatable.get(tag);
            try {
                nDefFormatableTag.connect();
                nDefFormatableTag.format(nfcMessage);
                nDefFormatableTag.close();
                // The data is written to the tag
                Toast.makeText(context, "Message written", Toast.LENGTH_LONG).show();
                return true;

            } catch (IOException e) {
                // Failed to format tag
                Toast.makeText(context, "Failed to format tag", Toast.LENGTH_LONG).show();
                return false;
            }

        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T> void enableNFCInForeground(NfcAdapter nfcAdapter, Activity activity, Class<T> classType) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                activity, 0,
                new Intent(activity, classType).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        );
        IntentFilter nfcIntentFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter[] filters = {nfcIntentFilter};
        String[][] techLists = {
                {Ndef.class.getName()},
                {NdefFormatable.class.getName()}
        };
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, techLists);
    }

    public static void disableNFCInForeground(NfcAdapter nfcAdapter, Activity activity) {
        nfcAdapter.disableForegroundDispatch(activity);
    }


}
