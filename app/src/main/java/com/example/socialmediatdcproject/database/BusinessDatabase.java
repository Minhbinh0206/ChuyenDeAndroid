package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Business;

import java.util.ArrayList;

public class BusinessDatabase {
    ArrayList<Business> businesses = new ArrayList<>();

    public ArrayList<Business> dataBusinesses() {
        // 1. FPT Software
        Business b1 = new Business();
        b1.setId(0);
        b1.setBusinessName("FPT Software");

        // 2. Microsoft Office
        Business b2 = new Business();
        b2.setId(1);
        b2.setBusinessName("Microsoft Office");

        // 3. Google LLC
        Business b3 = new Business();
        b3.setId(2);
        b3.setBusinessName("Google LLC");

        // 4. Apple Inc.
        Business b4 = new Business();
        b4.setId(3);
        b4.setBusinessName("Apple Inc.");

        // 5. Amazon
        Business b5 = new Business();
        b5.setId(4);
        b5.setBusinessName("Amazon");

        // 6. Tesla
        Business b6 = new Business();
        b6.setId(5);
        b6.setBusinessName("Tesla");

        // 7. Meta (Facebook)
        Business b7 = new Business();
        b7.setId(6);
        b7.setBusinessName("Meta (Facebook)");

        // 8. Samsung Electronics
        Business b8 = new Business();
        b8.setId(7);
        b8.setBusinessName("Samsung Electronics");

        // 9. Netflix
        Business b9 = new Business();
        b9.setId(8);
        b9.setBusinessName("Netflix");

        // 10. IBM
        Business b10 = new Business();
        b10.setId(9);
        b10.setBusinessName("IBM");

        // Add all businesses to the list
        businesses.add(b1);
        businesses.add(b2);
        businesses.add(b3);
        businesses.add(b4);
        businesses.add(b5);
        businesses.add(b6);
        businesses.add(b7);
        businesses.add(b8);
        businesses.add(b9);
        businesses.add(b10);

        return businesses;
    }
}
