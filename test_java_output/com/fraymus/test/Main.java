package com.fraymus.test;

import java.util.*;
import java.io.*;

public class Main {

    public static class User {
        public String Name;
        public int Age;
        public String Email;
    }

    public static int Add(int a, int b) {
        return a + b;
    }

    public static void Greet(String name) {
        System.out.println("Hello, " + name);
    }

    public static void main(String[] args) {
        var x = 10;
        var y = 20;
        var result = Add(x, y);
        System.out.println("Result:", result);
        var user = User{Name: "Alice", Age: 30, Email: "alice@example.com"}
        Greet(user.Name);
        var isValid = true;
        if isValid {
        System.out.println("Valid user");
    }
    }
}