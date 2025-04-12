package org.example;

import utils.MaConnexion;

import java.sql.Connection;
public class Main {
    public static void main(String[] args) {



        Connection conn;
        conn=MaConnexion.getInstance().getConnection();
        System.out.println(conn);
    }




}