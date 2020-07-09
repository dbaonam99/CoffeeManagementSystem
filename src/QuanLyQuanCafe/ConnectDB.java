/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuanLyQuanCafe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author namduong
 */
public class ConnectDB {
    public static Connection dbConnector() {
//        Connection conn;
        try {
            String url = "jdbc:sqlite:../QuanLyQuanCafe/src/database/database.db";
            Connection conn = DriverManager.getConnection(url);
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
