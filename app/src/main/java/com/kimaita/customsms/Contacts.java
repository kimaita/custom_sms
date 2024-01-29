package com.kimaita.customsms;

import java.util.HashMap;
import java.util.Map;

public class Contacts {

    private Map<String, String> contactList;

    public Contacts() {
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
        contacts.put("Kimaita", "0738106349");
        contacts.put("Kelvin James", "0795604129");
        contacts.put("Kelvin James Mutethia", "0785604129");
        contacts.put("Jolly", "0719879370");

        setContactList(contacts);
    }
}
