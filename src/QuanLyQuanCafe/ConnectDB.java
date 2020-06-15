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
        Connection conn = null;
        try {
            // jdbc:sqlite:/Users/namduong/Documents/database/QuanLiKhamBenh.db
            String url = "jdbc:sqlite:/Users/namduong/NetBeansProjects/QuanLiKhachHangg/src/database/database.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            return conn;
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return null;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        dbConnector();
    }
}
