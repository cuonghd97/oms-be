package com.semicolon.oms;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("ADMIN_HASH=" + encoder.encode("Admin@123"));
        System.out.println("STAFF_HASH=" + encoder.encode("Staff@123"));
        System.out.println("CUSTOMER_HASH=" + encoder.encode("Customer@123"));
    }
}
