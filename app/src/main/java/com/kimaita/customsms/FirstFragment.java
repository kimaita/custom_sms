package com.kimaita.customsms;

import static android.telephony.SmsManager.getSmsManagerForSubscriptionId;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.kimaita.customsms.databinding.FragmentFirstBinding;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class FirstFragment extends Fragment {

    private static final String INTENT_ACTION_SENT = "Intent_Sent";
    private static final String INTENT_ACTION_DELIVERY = "Intent_Delivered";
    private static final int REQUEST_CODE_ACTION_SENT = 1;
    private static final int REQUEST_CODE_ACTION_DELIVERY = 1;

    BroadcastReceiver smsSentDeliveredReceiver;

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeReceivers();

        Map<String, String> contacts = new Contacts().getContactList();

        binding.btnSendSms.setOnClickListener(view1 -> {
            checkRequestSmsPermission();
            sendTexts.accept(contacts);
        });

        binding.btnRequestPermission.setOnClickListener(v ->
                checkRequestSmsPermission());

        binding.textRecipients.setText(getString(R.string.recipient_count, contacts.size()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(smsSentDeliveredReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(INTENT_ACTION_SENT);
        filter.addAction(INTENT_ACTION_DELIVERY);
        registerReceiver(requireContext(), smsSentDeliveredReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (Boolean.TRUE.equals(isGranted)) {
                    enableSmsButton();
                } else {
                    disableSmsButton();
                }
            });

    private void checkRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("SMS Permission Check", getString(R.string.permission_granted));
            enableSmsButton();
        } else {
            Log.d("SMS Permission Check", getString(R.string.permission_not_granted));
            requestPermissionLauncher.launch(
                    Manifest.permission.SEND_SMS);
        }
    }

    private void disableSmsButton() {
        Toast.makeText(getContext(), "SMS usage disabled", Toast.LENGTH_LONG).show();
        binding.btnSendSms.setEnabled(false);
        binding.btnRequestPermission.setVisibility(View.VISIBLE);
    }

    private void enableSmsButton() {
        binding.btnSendSms.setEnabled(true);
        binding.btnRequestPermission.setVisibility(View.INVISIBLE);
    }

    Consumer<Map<String, String>> sendTexts = map ->
            map.forEach((k, v) -> {
                k = getString(R.string.def_sms, k);
                sendSMS(v, k);
            });

    private void sendSMS(String number, String message) {

        Intent sentIntent = new Intent(INTENT_ACTION_SENT);
        PendingIntent pendingSentIntent = PendingIntent.getBroadcast(requireContext(),
                REQUEST_CODE_ACTION_SENT, sentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deliveryIntent = new Intent(INTENT_ACTION_DELIVERY);
        PendingIntent pendingDeliveryIntent = PendingIntent.getBroadcast(requireContext(),
                REQUEST_CODE_ACTION_DELIVERY, deliveryIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        int subscriptionID = SmsManager.getDefaultSmsSubscriptionId();
        SmsManager smsManager = getSmsManagerForSubscriptionId(subscriptionID);

        smsManager.sendTextMessage(number, null, message, pendingSentIntent,
                pendingDeliveryIntent);
    }


    private void initializeReceivers() {
        smsSentDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processBroadcasts(intent);
            }
        };
    }

    private void processBroadcasts(Intent intent) {
        String action = intent.getAction();
        final String TAG = "SMS Broadcast Receiver";
        Log.i(TAG, "Received: " + action);

        if (action.equals(INTENT_ACTION_SENT)) {
            Bundle bundle = intent.getExtras();
            // Need to check for error messages
            Log.i(TAG, "Message: Sent. Result: " + bundle);
            Toast.makeText(getContext(), "Message sent", Toast.LENGTH_LONG).show();
        } else if (action.equals(INTENT_ACTION_DELIVERY)) {
            Bundle bundle = intent.getExtras();
            Set<String> keys = bundle.keySet();
            // Need to check for error messages
            Log.i(TAG, "Message: Delivered. Result: " + bundle + " " + keys);
            Toast.makeText(getContext(), "Message delivered", Toast.LENGTH_LONG).show();
        }
    }
}