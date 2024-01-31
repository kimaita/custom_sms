package com.kimaita.customsms;

import android.content.Context;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Contacts {

    private Map<String, String> contactList;
    private final Context c;
    private static final String FILENAME = "contacts.csv";

    public Contacts(Context c) {
        this.c = c;
        populateContacts();
    }

    public Map<String, String> getContactList() {
        return contactList;
    }

    public void setContactList(Map<String, String> contactList) {
        this.contactList = contactList;
    }

    private void populateContacts() {
        Map<String, String> contacts = new HashMap<>();
        contacts.put("0738106349", "Kimaita");
        contacts.put( "0795604129", "Kelvin James");
        contacts.put("0785604129","Kelvin James Mutethia");
        contacts.put("0719879370", "Jolly");

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                c.getAssets().open(FILENAME)))) {

            try (CSVReader reader = new CSVReaderBuilder(bufferedReader).withSkipLines(1).build()) {
                String[] nextLine;

                while ((nextLine = reader.readNext()) != null) {
                    String name = nextLine[0].split(" ")[0];
                    if (!Character.isLowerCase(name.charAt(1))) {
                        name = Character.toTitleCase(name.charAt(0)) + name.substring(1).toLowerCase();
                    }
                    String phoneNumber = '0' + nextLine[1];
                    contacts.put(phoneNumber, name);
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        setContactList(contacts);
    }

}
