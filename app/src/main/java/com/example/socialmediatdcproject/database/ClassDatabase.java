package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Class;

import java.util.ArrayList;

public class ClassDatabase {

    // Create a list to store classes
    private ArrayList<Class> classes = new ArrayList<>();

    // Method to provide dummy data for classes
    public ArrayList<Class> dataClasses() {
        // 1. CD22TT01
        Class c1 = new Class();
        c1.setId(0);
        c1.setClassName("CD22TT01");
        c1.setMajorId(5);

        // 2. CD22TT02
        Class c2 = new Class();
        c2.setId(1);
        c2.setClassName("CD22TT02");
        c2.setMajorId(5);

        // 3. CD22TT03
        Class c3 = new Class();
        c3.setId(2);
        c3.setClassName("CD22TT03");
        c3.setMajorId(5);

        // 4. CD22TT04
        Class c4 = new Class();
        c4.setId(3);
        c4.setClassName("CD22TT04");
        c4.setMajorId(5);

        // 5. CD22TT05
        Class c5 = new Class();
        c5.setId(4);
        c5.setClassName("CD22TT05");
        c5.setMajorId(5);

        // Add all the classes to the list
        classes.add(c1);
        classes.add(c2);
        classes.add(c3);
        classes.add(c4);
        classes.add(c5);

        // Return the list of dummy classes
        return classes;
    }
}
