package com.android.barscanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MessageDialogFragment extends DialogFragment {
    public interface MessageDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    private String mTitle;
    private String mMessage;
    private MessageDialogListener mListener;
    private final String SETTINGS = "SETTINGS";
    private SharedPreferences sp;
    private String email = "email";
    private static String emailMessage;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setRetainInstance(true);
        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        if(sp.contains("SAVED_EMAIL")) {
            email = (sp.getString("SAVED_EMAIL", ""));
        }
    }

    public static MessageDialogFragment newInstance(String title, String message, MessageDialogListener listener) {
        MessageDialogFragment fragment = new MessageDialogFragment();
        fragment.mTitle = title;
        fragment.mMessage = message;
        fragment.mListener = listener;

        emailMessage =  message;

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMessage)
                .setTitle(mTitle);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Код");
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailMessage);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        builder.setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (mListener != null) {
                    mListener.onDialogNegativeClick(MessageDialogFragment.this);
                }
            }
        });

        return builder.create();
    }
}
