/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuanLyQuanCafe;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Timer;

public class menu extends javax.swing.JFrame {
    CardLayout cardLayout = new CardLayout();
    CardLayout cardLayout2 = new CardLayout();
    Connection conn = null;
    PreparedStatement pstmt =  null;
    
    public static String hoaDon = "";
    public static String thanhTien = "";
    public static String thueHoaDon = "";
    public static String tongCong = "";
    public static float khuyenMai = 0;
    public static int ID_HD = 0;
    public static int ID_SP = 0;
    
    /** 
     * Creates new form
     */
    String role = loginForm.role;
    public menu() {
        initComponents();
        //Làm mờ các search box
        searchMon.setBackground(new Color(0,0,0,0));
        searchNhanVien.setBackground(new Color(0,0,0,0));
        searchKhachHang.setBackground(new Color(0,0,0,0));
        searchKho.setBackground(new Color(0,0,0,0));
        //check quyền đăng nhập
        if ("user".equals(role)) {
            btnNhanVien.setVisible(false);
            btnKho.setVisible(false);
            txtAdmin.setText("User");
        }
        
        cardLayout = (CardLayout)(card.getLayout());
        cardLayout2 = (CardLayout)(cardCategory.getLayout());
        conn = ConnectDB.dbConnector();
        taoTableNhanVien();
        taoTableKhachHang();
        taoTableMon();
        taoTableKho();
        taoTableOrderList();
        fillComboboxDrink();
        fillComboboxCake();
        resetDatMon();
        //Đếm thời gian
        TimerTask task = new TimerTask() {
            public void run() {
                String hours = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                txtHours.setText(hours);
                String days = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                txtDays.setText(days);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, new Date(), 1000);
        
        //Xoá nền của tất cả các nút cần bo tròn viền
        JPanel[] panel = {btnXoaOrderList, btnThanhToan, btnResetOrderList, 
                            btnThemMon, btnXoaMon, btnSuaMon, btnChamCong, 
                            btnThemNhanVien, btnXoaNhanVien, btnSuaNhanVien,
                            btnThemKhachHang, btnXoaKhachHang, btnSuaKhachHang,
                            btnThemKho, btnXoaKho, btnSuaKho};
        for (int i = 0; i < panel.length; i++) {
            panel[i].setBackground(new Color(0,0,0,0));
        }
        
        //Trang trí table
        JTable[] table = {tableOrderList, tableMon, tableNhanVien, tableKhachHang, tableKho};
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(32, 136, 203));
        headerRenderer.setForeground(new Color(250,250,250));
        
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].getModel().getColumnCount(); j++) {
                table[i].getColumnModel().getColumn(j).setHeaderRenderer(headerRenderer);
                table[i].setSelectionBackground(new Color(190,190,190));
                table[i].setBackground(Color.white);
                table[i].setFillsViewportHeight(true);
            }
        }
        getID_HD();
        int curID_HD = ID_HD + 1;
        txtIDOrder.setText("#" + curID_HD);
    }
    
    class JPanelGradient extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            Color color1 = new Color(251,52,90);
            Color color2 = new Color(250,97,60);
            GradientPaint gp = new GradientPaint(150,0,color1,150,h,color2);
            g2d.setPaint(gp);
            g2d.fillRect(0,0,w,h);
        }
    }
        
    public void searchTable(String searchString, JTable table, DefaultTableModel tableModel) {    
        TableRowSorter<DefaultTableModel> tableSearch = new  TableRowSorter<DefaultTableModel>(tableModel);
        table.setRowSorter(tableSearch);
        tableSearch.setRowFilter(RowFilter.regexFilter(searchString));
    }
    
    public void redBar(JLabel red){
        redDatMon.setOpaque(false);
        redQuanLiMon.setOpaque(false);
        redNhanVien.setOpaque(false);
        redKho.setOpaque(false);
        redKhachHang.setOpaque(false);
        
        red.setOpaque(true);
        
        redDatMon.repaint();
        redQuanLiMon.repaint();
        redNhanVien.repaint();
        redKho.repaint();
        redKhachHang.repaint();
    }
    
    public void redCategory(JPanel red){
        redDrink.setOpaque(false);
        redCake.setOpaque(false);
        
        red.setOpaque(true);
        
        redDrink.repaint();
        redCake.repaint();
    }
    
    private void tinhTongBill(){
        float subtotal = 0;
        float thue = (float)10/100;
        for (int i = 0; i < tableOrderList.getRowCount(); i++){
            subtotal +=Integer.parseInt( tableOrderList.getValueAt(i, 3).toString());
        }
        thanhTien = String.valueOf(subtotal);
        thueHoaDon = String.valueOf(subtotal*thue);
        tongCong = String.valueOf(subtotal + (subtotal*thue) - (khuyenMai*subtotal));
        txtThanhTien.setText(thanhTien + " vnđ");
        txtKhuyenMai.setText((khuyenMai * subtotal) + " vnđ");
        txtThue.setText(thueHoaDon + " vnđ");
        txtTotal.setText(tongCong + " vnđ");
    }
    
    private void resetDatMon(){
        selectMon.setSelectedItem(null);
        txtTenMon_dr.setText(null);
        txtGia_dr.setText(null);
        txtMoTa_dr.setText(null);
        selectCake.setSelectedItem(null);
        txtTenMon_cake.setText(null);
        txtGia_cake.setText(null);
        txtMoTa_cake.setText(null);
        selectSize_dr.setSelectedItem(null);
        selectSoLuong_dr.setSelectedItem(null);
        selectSize_cake.setSelectedItem(null);
        selectSoLuong_cake.setSelectedItem(null);
    }
    
    private void fillComboboxDrink(){
        selectMon.removeAllItems();
        String sql = "select ten_SP, ID_SP from SANPHAM where loai_SP = 'Drink'";
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                selectMon.addItem(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void fillComboboxCake(){
        selectCake.removeAllItems();
        String sql = "select ten_SP, ID_SP from SANPHAM where loai_SP = 'Cake'";
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                selectCake.addItem(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void fillInfoDrink() {
        String sql = "select * from SANPHAM where TEN_SP = '" + selectMon.getSelectedItem() + "'";
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int row = 0;
            while (rs.next()) {
                row++;
                txtTenMon_dr.setText(rs.getString(2));
                txtGia_dr.setText(rs.getString(3));
                txtMoTa_dr.setText(rs.getString(4));
                // hình vừa với khung 
                ImageIcon ii = new ImageIcon(rs.getString(6));
                Image image = ii.getImage().getScaledInstance(lbDrinkImage.getWidth(), lbDrinkImage.getHeight(), Image.SCALE_SMOOTH);
                lbDrinkImage.setIcon(new ImageIcon(image));
                if (txtMoTa_dr.getText().length() > 25) {
                    txtMoTa_dr.setText(txtMoTa_dr.getText().substring(0,25) + "...");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        selectSize_dr.setSelectedItem("S");
        selectSoLuong_dr.setSelectedItem("1");
    }
    
    public void fillInfoCake() {
        String sql = "select * from SANPHAM where TEN_SP = '" + selectCake.getSelectedItem() + "'";
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int row = 0;
            while (rs.next()) {
                row++;
                txtTenMon_cake.setText(rs.getString(2));
                txtGia_cake.setText(rs.getString(3));
                txtMoTa_cake.setText(rs.getString(4));
                // hình vừa với khung 
                ImageIcon ii = new ImageIcon(rs.getString(6));
                Image image = ii.getImage().getScaledInstance(lbCakeImage.getWidth(), lbCakeImage.getHeight(), Image.SCALE_SMOOTH);
                lbCakeImage.setIcon(new ImageIcon(image));
                if (txtMoTa_cake.getText().length() > 25) {
                    txtMoTa_cake.setText(txtMoTa_cake.getText().substring(0,25) + "...");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        selectSize_cake.setSelectedItem("S");
        selectSoLuong_cake.setSelectedItem("1");
    }
    
    public void getID_HD() {
        String sql = "select max(ID_HD) from HOADON";
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ID_HD = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void getID_SP(int row) {
        String a = tableOrderList.getValueAt(row, 0).toString();
        String sql = "select ID_SP from SANPHAM where ten_SP = '" + a + "'";
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ID_SP = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void insertHOADON(){
        String days = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        String sql = "INSERT INTO HOADON(NG_HD, TRIGIA_HD) VALUES(?,?)";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, days);
            pstmt.setString(2, tongCong);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void insertCTHD(){
        getID_HD();
        String sql = "INSERT INTO CTHD(ID_HD, ID_SP, SIZE, SOLUONG) VALUES(?,?,?,?)";
        try {
            int rows = tableOrderList.getRowCount();
            for(int row = 0; row < rows; row++)
            {   
                getID_SP(row);
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, ID_HD);
                pstmt.setInt(2, ID_SP);
                pstmt.setString(3, (tableOrderList.getValueAt(row, 1).toString()).substring(0,1));
                pstmt.setString(4, tableOrderList.getValueAt(row, 2).toString());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    DefaultTableModel tblModelNhanVien, tblModelKhachHang, tblModelMon, tblModelOrderList, tblModelKho;
    
    public void taoTableNhanVien() {
        tblModelNhanVien = new DefaultTableModel();
        String tieuDe[] = {"ID", "Tên", "Tuổi", "Địa chỉ", "SDT", "Email", "Ngày vào làm", "Số ngày làm"};
        tblModelNhanVien.setColumnIdentifiers(tieuDe);
        loadDataNhanVien();
        setVisible(true);
        tableNhanVien.setDefaultEditor(Object.class, null);
    }
    public void taoTableKhachHang() {
        tblModelKhachHang = new DefaultTableModel();
        String tieuDe[] = {"ID", "Tên", "Tuổi", "Địa chỉ", "SDT", "Email", "Phân loại"};
        tblModelKhachHang.setColumnIdentifiers(tieuDe);
        loadDataKhachHang();
        setVisible(true);
        tableKhachHang.setDefaultEditor(Object.class, null);
    }
    public void taoTableMon() {
        tblModelMon = new DefaultTableModel();
        String tieuDe[] = {"Mã món", "Tên món", "Giá", "Mô tả", "Loại", "Ảnh"};
        tblModelMon.setColumnIdentifiers(tieuDe);
        loadDataMon();
        setVisible(true);
        tableMon.setDefaultEditor(Object.class, null);
    }
    public void taoTableKho() {
        tblModelKho = new DefaultTableModel();
        String tieuDe[] = {"ID", "Tên nguyên liệu", "Khối lượng", "Người nhập", "Ngày nhập", "Xuất xứ", "Trạng thái"};
        tblModelKho.setColumnIdentifiers(tieuDe);
        loadDataKho();
        setVisible(true);
        tableKho.setDefaultEditor(Object.class, null);
    }
    public void taoTableOrderList() {
        tblModelOrderList = new DefaultTableModel();
        String tieuDe[] = {"Tên", "Size", "Số lượng", "Giá"};
        tblModelOrderList.setColumnIdentifiers(tieuDe);
        tableOrderList.setModel(tblModelOrderList);
        setVisible(true);
        tableOrderList.setDefaultEditor(Object.class, null);
    }
    
    public void loadDataNhanVien() {
        DefaultTableModel tMOdel = (DefaultTableModel) tableNhanVien.getModel();
        tMOdel.setRowCount(0);
        String sql = "select * from NHANVIEN";
        String row[] = new String[8];
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getString(4);
                row[4] = rs.getString(5);
                row[5] = rs.getString(6);
                row[6] = rs.getString(7);
                row[7] = rs.getString(8);
                tblModelNhanVien.addRow(row);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        tableNhanVien.setModel(tblModelNhanVien);
    }
    public void loadDataKhachHang() {
        DefaultTableModel tMOdel = (DefaultTableModel) tableKhachHang.getModel();
        tMOdel.setRowCount(0);
        String sql = "select * from KHACHHANG";
        String row[] = new String[8];
        try {
             Statement stmt  = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getString(4);
                row[4] = rs.getString(5);
                row[5] = rs.getString(6);
                row[6] = rs.getString(7);
                tblModelKhachHang.addRow(row);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        tableKhachHang.setModel(tblModelKhachHang);
    }
    public void loadDataMon() {
       
        DefaultTableModel tMOdel = (DefaultTableModel) tableMon.getModel();
       
        tMOdel.setRowCount(0);
      
        String sql = "select * from SANPHAM";
        Object row[] = new Object[6];
        FileOutputStream fos = null;
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getString(4);
                row[4] = rs.getString(5);
                row[5] = rs.getString(6);
                tblModelMon.addRow(row);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        tableMon.setModel(tblModelMon);
    }
    public void loadDataKho() {
       
        DefaultTableModel tMOdel = (DefaultTableModel) tableKho.getModel();
       
        tMOdel.setRowCount(0);
      
        String sql = "select * from KHO";
        Object row[] = new Object[7];
        FileOutputStream fos = null;
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getString(4);
                row[4] = rs.getString(5);
                row[5] = rs.getString(6);
                row[6] = rs.getString(7);
                tblModelKho.addRow(row);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        tableKho.setModel(tblModelKho);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        navbar = new javax.swing.JPanel();
        btnDatMon = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        redDatMon = new javax.swing.JLabel();
        btnQuanLyMon = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        redQuanLiMon = new javax.swing.JLabel();
        btnNhanVien = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        redNhanVien = new javax.swing.JLabel();
        btnKhachHang = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        redKhachHang = new javax.swing.JLabel();
        btnKho = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        redKho = new javax.swing.JLabel();
        avt = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtAdmin = new javax.swing.JLabel();
        card = new javax.swing.JPanel();
        cardDatMon = new javax.swing.JPanel();
        jPanel11 = new JPanelGradient();
        jLabel18 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnCategory = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        btnDrink = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        redDrink = new javax.swing.JPanel();
        btnCake = new javax.swing.JPanel();
        redCake = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        cardCategory = new javax.swing.JPanel();
        cardDrink = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        btnOrderDrink = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel98 = new javax.swing.JLabel();
        selectMon = new javax.swing.JComboBox<>();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        selectSize_dr = new javax.swing.JComboBox<>();
        jLabel104 = new javax.swing.JLabel();
        selectSoLuong_dr = new javax.swing.JComboBox<>();
        txtTenMon_dr = new javax.swing.JLabel();
        txtGia_dr = new javax.swing.JLabel();
        txtMoTa_dr = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        lbDrinkImage = new javax.swing.JLabel();
        cardCake = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        btnOrderCake = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel69 = new javax.swing.JLabel();
        selectCake = new javax.swing.JComboBox<>();
        jLabel74 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        selectSize_cake = new javax.swing.JComboBox<>();
        jLabel97 = new javax.swing.JLabel();
        selectSoLuong_cake = new javax.swing.JComboBox<>();
        txtTenMon_cake = new javax.swing.JLabel();
        txtGia_cake = new javax.swing.JLabel();
        txtMoTa_cake = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        lbCakeImage = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableOrderList = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        txtOrderListID = new javax.swing.JLabel();
        txtHours = new javax.swing.JLabel();
        txtDays = new javax.swing.JLabel();
        txtIDOrder = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        btnXoaOrderList = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel65 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        btnResetOrderList = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel66 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtThue = new javax.swing.JLabel();
        txtTotal = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        txtThanhTien = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        txtKhuyenMai = new javax.swing.JLabel();
        btnThanhToan =  new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel67 = new javax.swing.JLabel();
        selectVIPCard = new javax.swing.JComboBox<>();
        jLabel75 = new javax.swing.JLabel();
        cardQuanLyMon = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableMon = new javax.swing.JTable();
        jPanel21 = new javax.swing.JPanel();
        btnThemMon = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel24 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnXoaMon = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        btnSuaMon = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jPanel40 = new javax.swing.JPanel();
        jPanel41 = new javax.swing.JPanel();
        jPanel42 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        btnSelectImage = new javax.swing.JButton();
        jPanel43 = new javax.swing.JPanel();
        txtTenMon = new javax.swing.JTextField();
        txtGia = new javax.swing.JTextField();
        txtURL = new javax.swing.JTextField();
        jPanel46 = new javax.swing.JPanel();
        jPanel49 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel50 = new javax.swing.JPanel();
        txtMoTa = new javax.swing.JTextField();
        selectLoai = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jPanel19 = new JPanelGradient();
        jLabel23 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        searchMon = new javax.swing.JTextField();
        cardNhanVien = new javax.swing.JPanel();
        jPanel36 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableNhanVien = new javax.swing.JTable();
        nhanvienButton = new javax.swing.JPanel();
        btnThemNhanVien = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel53 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnXoaNhanVien = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        btnSuaNhanVien = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        btnChamCong = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel73 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jPanel54 = new javax.swing.JPanel();
        jPanel55 = new javax.swing.JPanel();
        jPanel56 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jPanel57 = new javax.swing.JPanel();
        txtTenNV = new javax.swing.JTextField();
        txtTuoi = new javax.swing.JTextField();
        txtDiaChi = new javax.swing.JTextField();
        jPanel58 = new javax.swing.JPanel();
        jPanel59 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jPanel60 = new javax.swing.JPanel();
        txtSDT = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtNgayVaoLam = new com.toedter.calendar.JDateChooser();
        jPanel22 = new JPanelGradient();
        jLabel32 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        searchNhanVien = new javax.swing.JTextField();
        cardKhachHang = new javax.swing.JPanel();
        jPanel38 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableKhachHang = new javax.swing.JTable();
        nhanvienButton1 = new javax.swing.JPanel();
        btnThemKhachHang = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel70 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        btnXoaKhachHang = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        btnSuaKhachHang = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jPanel61 = new javax.swing.JPanel();
        jPanel62 = new javax.swing.JPanel();
        jPanel63 = new javax.swing.JPanel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jPanel64 = new javax.swing.JPanel();
        txtTenKH = new javax.swing.JTextField();
        txtTuoiKH = new javax.swing.JTextField();
        txtDiaChiKH = new javax.swing.JTextField();
        jPanel65 = new javax.swing.JPanel();
        jPanel66 = new javax.swing.JPanel();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jPanel67 = new javax.swing.JPanel();
        txtSDTKH = new javax.swing.JTextField();
        txtEmailKH = new javax.swing.JTextField();
        selectVIP = new javax.swing.JComboBox<>();
        jPanel23 = new JPanelGradient();
        jLabel33 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        searchKhachHang = new javax.swing.JTextField();
        cardKho = new javax.swing.JPanel();
        jPanel74 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tableKho = new javax.swing.JTable();
        jPanel75 = new javax.swing.JPanel();
        btnThemKho = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        btnXoaKho = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        btnSuaKho = new QuanLyQuanCafe.RoundedDecoration(20);
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jPanel76 = new javax.swing.JPanel();
        jPanel77 = new javax.swing.JPanel();
        jPanel78 = new javax.swing.JPanel();
        jLabel80 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jPanel79 = new javax.swing.JPanel();
        txtTenNL = new javax.swing.JTextField();
        txtKhoiLuong = new javax.swing.JTextField();
        txtTenNguoiNhap = new javax.swing.JTextField();
        jPanel80 = new javax.swing.JPanel();
        jPanel81 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jPanel83 = new javax.swing.JPanel();
        txtNgayNhap = new com.toedter.calendar.JDateChooser();
        txtXuatXu = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jPanel29 = new JPanelGradient();
        jLabel48 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        searchKho = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(830, 554));
        setUndecorated(true);
        setSize(new java.awt.Dimension(830, 554));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        navbar.setBackground(new java.awt.Color(33, 38, 54));

        btnDatMon.setBackground(new java.awt.Color(33, 38, 54));
        btnDatMon.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnDatMon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDatMonMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDatMonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDatMonMouseEntered(evt);
            }
        });
        btnDatMon.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/order.png"))); // NOI18N
        btnDatMon.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 30, 30));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Đặt Món");
        btnDatMon.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, -1));

        redDatMon.setBackground(new java.awt.Color(251, 52, 90));
        redDatMon.setOpaque(true);
        btnDatMon.add(redDatMon, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 10, 50));

        btnQuanLyMon.setBackground(new java.awt.Color(33, 38, 54));
        btnQuanLyMon.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnQuanLyMon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQuanLyMonMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnQuanLyMonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnQuanLyMonMouseEntered(evt);
            }
        });
        btnQuanLyMon.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Quản Lý Món");
        btnQuanLyMon.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/qlmon.png"))); // NOI18N
        btnQuanLyMon.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 30, 30));

        redQuanLiMon.setBackground(new java.awt.Color(251, 52, 90));
        btnQuanLyMon.add(redQuanLiMon, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 10, 50));

        btnNhanVien.setBackground(new java.awt.Color(33, 38, 54));
        btnNhanVien.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNhanVienMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnNhanVienMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnNhanVienMouseEntered(evt);
            }
        });
        btnNhanVien.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Nhân Viên");
        btnNhanVien.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, -1));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/employees.png"))); // NOI18N
        btnNhanVien.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 30, 30));

        redNhanVien.setBackground(new java.awt.Color(251, 52, 90));
        btnNhanVien.add(redNhanVien, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 10, 50));

        btnKhachHang.setBackground(new java.awt.Color(33, 38, 54));
        btnKhachHang.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnKhachHangMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnKhachHangMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnKhachHangMouseEntered(evt);
            }
        });
        btnKhachHang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Khách Hàng");
        btnKhachHang.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/customer.png"))); // NOI18N
        btnKhachHang.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 30, 30));

        redKhachHang.setBackground(new java.awt.Color(251, 52, 90));
        btnKhachHang.add(redKhachHang, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 10, 50));

        btnKho.setBackground(new java.awt.Color(33, 38, 54));
        btnKho.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnKho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnKhoMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnKhoMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnKhoMouseEntered(evt);
            }
        });
        btnKho.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Kho");
        btnKho.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, -1));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/inventory.png"))); // NOI18N
        btnKho.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 30, 30));

        redKho.setBackground(new java.awt.Color(251, 52, 90));
        btnKho.add(redKho, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 10, 50));

        avt.setBackground(new java.awt.Color(33, 38, 54));
        avt.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setBackground(new java.awt.Color(33, 38, 54));
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/login-icon.png"))); // NOI18N
        avt.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        txtAdmin.setBackground(new java.awt.Color(33, 38, 54));
        txtAdmin.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        txtAdmin.setForeground(new java.awt.Color(221, 221, 221));
        txtAdmin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtAdmin.setText("Admin");
        txtAdmin.setOpaque(true);
        txtAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtAdminMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtAdminMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtAdminMouseEntered(evt);
            }
        });
        avt.add(txtAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(-13, 110, 135, -1));

        javax.swing.GroupLayout navbarLayout = new javax.swing.GroupLayout(navbar);
        navbar.setLayout(navbarLayout);
        navbarLayout.setHorizontalGroup(
            navbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navbarLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(avt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(btnDatMon, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnQuanLyMon, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnKho, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        navbarLayout.setVerticalGroup(
            navbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navbarLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(avt, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnDatMon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnQuanLyMon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnKho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(117, 117, 117))
        );

        jPanel1.add(navbar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 550));

        card.setLayout(new java.awt.CardLayout());

        cardDatMon.setBackground(new java.awt.Color(255, 255, 255));
        cardDatMon.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel11.setBackground(new java.awt.Color(255, 102, 102));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Đặt Món ");
        jPanel11.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 220, 50));

        jLabel35.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setIcon(new javax.swing.ImageIcon("/Users/namduong/Downloads/inside-logout-icon.png")); // NOI18N
        jLabel35.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel35MouseClicked(evt);
            }
        });
        jPanel11.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 0, -1, 50));

        cardDatMon.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 50));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        btnCategory.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBackground(new java.awt.Color(33, 38, 54));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnDrink.setBackground(new java.awt.Color(33, 38, 54));
        btnDrink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDrinkMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDrinkMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDrinkMouseEntered(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("Drink");

        redDrink.setBackground(new java.awt.Color(251, 52, 90));
        redDrink.setPreferredSize(new java.awt.Dimension(60, 5));

        javax.swing.GroupLayout redDrinkLayout = new javax.swing.GroupLayout(redDrink);
        redDrink.setLayout(redDrinkLayout);
        redDrinkLayout.setHorizontalGroup(
            redDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );
        redDrinkLayout.setVerticalGroup(
            redDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout btnDrinkLayout = new javax.swing.GroupLayout(btnDrink);
        btnDrink.setLayout(btnDrinkLayout);
        btnDrinkLayout.setHorizontalGroup(
            btnDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnDrinkLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(btnDrinkLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(redDrink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        btnDrinkLayout.setVerticalGroup(
            btnDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnDrinkLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnDrinkLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(redDrink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.add(btnDrink, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 40));

        btnCake.setBackground(new java.awt.Color(33, 38, 54));
        btnCake.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCakeMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCakeMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCakeMouseEntered(evt);
            }
        });

        redCake.setBackground(new java.awt.Color(251, 52, 90));
        redCake.setOpaque(false);
        redCake.setPreferredSize(new java.awt.Dimension(60, 5));

        javax.swing.GroupLayout redCakeLayout = new javax.swing.GroupLayout(redCake);
        redCake.setLayout(redCakeLayout);
        redCakeLayout.setHorizontalGroup(
            redCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );
        redCakeLayout.setVerticalGroup(
            redCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jLabel41.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("Cake");

        javax.swing.GroupLayout btnCakeLayout = new javax.swing.GroupLayout(btnCake);
        btnCake.setLayout(btnCakeLayout);
        btnCakeLayout.setHorizontalGroup(
            btnCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnCakeLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(redCake, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(btnCakeLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        btnCakeLayout.setVerticalGroup(
            btnCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnCakeLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnCakeLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(redCake, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.add(btnCake, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, -1, 40));

        javax.swing.GroupLayout btnCategoryLayout = new javax.swing.GroupLayout(btnCategory);
        btnCategory.setLayout(btnCategoryLayout);
        btnCategoryLayout.setHorizontalGroup(
            btnCategoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnCategoryLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        btnCategoryLayout.setVerticalGroup(
            btnCategoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnCategoryLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        cardCategory.setLayout(new java.awt.CardLayout());

        btnOrderDrink.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/PngItem_5679765.png"))); // NOI18N
        btnOrderDrink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnOrderDrinkMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(btnOrderDrink)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(btnOrderDrink, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(86, 86, 86))
        );

        jLabel98.setText("Chọn món");

        selectMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectMonActionPerformed(evt);
            }
        });

        jLabel99.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel99.setText("Thông tin:");

        jLabel100.setText("Tên");

        jLabel101.setText("Giá");

        jLabel102.setText("Mô tả");

        jLabel103.setText("Size");

        selectSize_dr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "M (+10k)", "L (+20k)" }));

        jLabel104.setText("Số lượng");

        selectSoLuong_dr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel104, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addComponent(jLabel103, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel102, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel101, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel100, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel98, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectSoLuong_dr, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(selectSize_dr, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(selectMon, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtMoTa_dr, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtGia_dr, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtTenMon_dr, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))))
                        .addContainerGap())
                    .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectMon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTenMon_dr, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel100, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel101, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGia_dr, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMoTa_dr, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel102, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel103, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(selectSize_dr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel104, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectSoLuong_dr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbDrinkImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/a.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbDrinkImage, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbDrinkImage, javax.swing.GroupLayout.PREFERRED_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout cardDrinkLayout = new javax.swing.GroupLayout(cardDrink);
        cardDrink.setLayout(cardDrinkLayout);
        cardDrinkLayout.setHorizontalGroup(
            cardDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardDrinkLayout.createSequentialGroup()
                .addGroup(cardDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(cardDrinkLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        cardDrinkLayout.setVerticalGroup(
            cardDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardDrinkLayout.createSequentialGroup()
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 231, Short.MAX_VALUE)
                    .addGroup(cardDrinkLayout.createSequentialGroup()
                        .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        cardCategory.add(cardDrink, "cardDrink");

        btnOrderCake.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/PngItem_5679765.png"))); // NOI18N
        btnOrderCake.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnOrderCakeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(btnOrderCake)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(btnOrderCake, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(86, 86, 86))
        );

        jLabel69.setText("Chọn món");

        selectCake.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCakeActionPerformed(evt);
            }
        });

        jLabel74.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel74.setText("Thông tin:");

        jLabel84.setText("Tên");

        jLabel88.setText("Giá");

        jLabel95.setText("Mô tả");

        jLabel96.setText("Size");

        selectSize_cake.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "M (+10k)", "L (+20k)" }));

        jLabel97.setText("Số lượng");

        selectSoLuong_cake.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel97, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addComponent(jLabel96, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel95, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel88, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel84, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel69, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectSoLuong_cake, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(selectSize_cake, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(selectCake, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtMoTa_cake, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtGia_cake, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtTenMon_cake, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))))
                        .addContainerGap())
                    .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectCake, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTenMon_cake, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGia_cake, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMoTa_cake, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel96, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(selectSize_cake, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel97, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectSoLuong_cake, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbCakeImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/a.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbCakeImage, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbCakeImage, javax.swing.GroupLayout.PREFERRED_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout cardCakeLayout = new javax.swing.GroupLayout(cardCake);
        cardCake.setLayout(cardCakeLayout);
        cardCakeLayout.setHorizontalGroup(
            cardCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardCakeLayout.createSequentialGroup()
                .addGroup(cardCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(cardCakeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        cardCakeLayout.setVerticalGroup(
            cardCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardCakeLayout.createSequentialGroup()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 231, Short.MAX_VALUE)
                    .addGroup(cardCakeLayout.createSequentialGroup()
                        .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        cardCategory.add(cardCake, "cardCake");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(cardCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(btnCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(cardCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        cardDatMon.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 450, 500));

        jPanel3.setBackground(new java.awt.Color(33, 38, 54));

        jScrollPane2.setBorder(null);

        tableOrderList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tên", "Size", "Số lượng", "Giá"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableOrderList.setRequestFocusEnabled(false);
        tableOrderList.setSelectionBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(tableOrderList);
        if (tableOrderList.getColumnModel().getColumnCount() > 0) {
            tableOrderList.getColumnModel().getColumn(0).setResizable(false);
            tableOrderList.getColumnModel().getColumn(1).setResizable(false);
            tableOrderList.getColumnModel().getColumn(2).setResizable(false);
            tableOrderList.getColumnModel().getColumn(3).setResizable(false);
        }

        jPanel13.setBackground(new java.awt.Color(33, 38, 54));
        jPanel13.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(251, 52, 90)));

        txtOrderListID.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        txtOrderListID.setForeground(new java.awt.Color(255, 255, 255));
        txtOrderListID.setText("Order List");

        txtHours.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        txtHours.setForeground(new java.awt.Color(251, 52, 90));
        txtHours.setText("20:20:00");

        txtDays.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        txtDays.setForeground(new java.awt.Color(204, 204, 204));
        txtDays.setText("25/06/2020");

        txtIDOrder.setForeground(new java.awt.Color(204, 204, 204));
        txtIDOrder.setText("#1");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(txtOrderListID, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIDOrder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtHours, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDays, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOrderListID, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHours, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDays)
                    .addComponent(txtIDOrder))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(33, 38, 54));

        btnXoaOrderList.setBackground(new java.awt.Color(251, 52, 90));
        btnXoaOrderList.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnXoaOrderList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnXoaOrderListMouseClicked(evt);
            }
        });
        btnXoaOrderList.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel65.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
        btnXoaOrderList.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 20, 30));

        jLabel43.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 255, 255));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("Xoá");
        btnXoaOrderList.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 40, 30));

        btnResetOrderList.setBackground(new java.awt.Color(251, 52, 90));
        btnResetOrderList.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnResetOrderList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnResetOrderListMouseClicked(evt);
            }
        });
        btnResetOrderList.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel66.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/sinchronize-32.png"))); // NOI18N
        btnResetOrderList.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 20, 30));

        jLabel47.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(255, 255, 255));
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText("Đặt lại");
        btnResetOrderList.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 50, 30));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(btnXoaOrderList, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnResetOrderList, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnResetOrderList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoaOrderList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(33, 38, 54));
        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(251, 52, 90)));

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Tổng cộng");

        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jLabel6.setText("Thuế");

        txtThue.setForeground(new java.awt.Color(204, 204, 204));
        txtThue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtThue.setText(" ");

        txtTotal.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        txtTotal.setForeground(new java.awt.Color(255, 255, 255));
        txtTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotal.setText(" ");

        jLabel52.setForeground(new java.awt.Color(204, 204, 204));
        jLabel52.setText("Khuyến mãi");

        txtThanhTien.setForeground(new java.awt.Color(204, 204, 204));
        txtThanhTien.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtThanhTien.setText(" ");

        jLabel94.setForeground(new java.awt.Color(204, 204, 204));
        jLabel94.setText("Thành tiền");

        txtKhuyenMai.setForeground(new java.awt.Color(204, 204, 204));
        txtKhuyenMai.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtKhuyenMai.setText(" ");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel94, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtThue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtThanhTien, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtKhuyenMai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel94, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtThanhTien))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtKhuyenMai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtThue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotal))
                .addContainerGap())
        );

        btnThanhToan.setBackground(new java.awt.Color(251, 52, 90));
        btnThanhToan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnThanhToan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnThanhToanMouseClicked(evt);
            }
        });
        btnThanhToan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel67.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(255, 255, 255));
        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel67.setText("Thanh Toán");
        btnThanhToan.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 43));

        selectVIPCard.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Không có", "Thường", "VIP" }));
        selectVIPCard.setRequestFocusEnabled(false);
        selectVIPCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectVIPCardActionPerformed(evt);
            }
        });

        jLabel75.setForeground(new java.awt.Color(204, 204, 204));
        jLabel75.setText("Thẻ thành viên");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel75)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(selectVIPCard, 0, 214, Short.MAX_VALUE)
                                .addGap(6, 6, 6))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectVIPCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        cardDatMon.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 50, 350, 500));

        card.add(cardDatMon, "cardDatMon");

        cardQuanLyMon.setBackground(new java.awt.Color(255, 255, 255));
        cardQuanLyMon.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        tableMon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã Món", "Tên món", "Giá", "Mô tả", "Loại", "Ảnh"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Byte.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableMon.setRequestFocusEnabled(false);
        tableMon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMonMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableMon);

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));

        btnThemMon.setBackground(new java.awt.Color(251, 52, 90));
        btnThemMon.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnThemMon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnThemMonMouseClicked(evt);
            }
        });
        btnThemMon.setLayout(new java.awt.GridLayout(1, 0));

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/plus-5-32.png"))); // NOI18N
        btnThemMon.add(jLabel24);

        jLabel11.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Thêm");
        btnThemMon.add(jLabel11);

        btnXoaMon.setBackground(new java.awt.Color(251, 52, 90));
        btnXoaMon.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnXoaMon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnXoaMonMouseClicked(evt);
            }
        });
        btnXoaMon.setLayout(new java.awt.GridLayout(1, 0));

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
        btnXoaMon.add(jLabel25);

        jLabel26.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Xoá");
        btnXoaMon.add(jLabel26);

        btnSuaMon.setBackground(new java.awt.Color(251, 52, 90));
        btnSuaMon.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnSuaMon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSuaMonMouseClicked(evt);
            }
        });
        btnSuaMon.setLayout(new java.awt.GridLayout(1, 0));

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/edit-8-32.png"))); // NOI18N
        btnSuaMon.add(jLabel29);

        jLabel30.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Sửa");
        btnSuaMon.add(jLabel30);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnThemMon, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(btnXoaMon, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE)
                .addComponent(btnSuaMon, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSuaMon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnXoaMon, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(btnThemMon, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel40.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jPanel41.setLayout(new java.awt.GridLayout(1, 0));

        jPanel42.setLayout(new java.awt.GridLayout(0, 1));

        jLabel27.setText("Tên món");
        jPanel42.add(jLabel27);

        jLabel28.setText("Giá bán");
        jPanel42.add(jLabel28);

        btnSelectImage.setText("Chọn ảnh...");
        btnSelectImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectImageActionPerformed(evt);
            }
        });
        jPanel42.add(btnSelectImage);

        jPanel41.add(jPanel42);

        jPanel43.setLayout(new java.awt.GridLayout(0, 1));
        jPanel43.add(txtTenMon);
        jPanel43.add(txtGia);
        jPanel43.add(txtURL);

        jPanel41.add(jPanel43);

        jPanel46.setLayout(new java.awt.GridLayout(1, 0));

        jPanel49.setLayout(new java.awt.GridLayout(0, 1));

        jLabel34.setText("Mô tả");
        jPanel49.add(jLabel34);

        jLabel46.setText("Loại");
        jPanel49.add(jLabel46);
        jPanel49.add(jLabel22);

        jPanel46.add(jPanel49);

        jPanel50.setLayout(new java.awt.GridLayout(0, 1));
        jPanel50.add(txtMoTa);

        selectLoai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Drink", "Cake" }));
        jPanel50.add(selectLoai);
        jPanel50.add(jLabel10);

        jPanel46.add(jPanel50);

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel41, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        cardQuanLyMon.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 800, 500));

        jPanel19.setBackground(new java.awt.Color(255, 102, 102));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel23.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Quản Lý Món");
        jPanel19.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 220, 50));

        jLabel19.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setIcon(new javax.swing.ImageIcon("/Users/namduong/Downloads/inside-logout-icon.png")); // NOI18N
        jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel19MouseClicked(evt);
            }
        });
        jPanel19.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 0, -1, 50));

        searchMon.setFont(new java.awt.Font("Lucida Grande", 0, 15)); // NOI18N
        searchMon.setForeground(new java.awt.Color(204, 204, 204));
        searchMon.setText(" Tìm kiếm...");
        searchMon.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        searchMon.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchMonFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchMonFocusLost(evt);
            }
        });
        searchMon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchMonKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchMonKeyReleased(evt);
            }
        });
        jPanel19.add(searchMon, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 380, 30));

        cardQuanLyMon.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 50));

        card.add(cardQuanLyMon, "cardQuanLyMon");

        cardNhanVien.setBackground(new java.awt.Color(255, 255, 255));
        cardNhanVien.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel36.setBackground(new java.awt.Color(255, 255, 255));

        tableNhanVien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Tên", "Tuổi", "Địa chỉ", "SDT", "Email", "Ngày vào làm", "Số ngày làm"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableNhanVien.setFocusable(false);
        tableNhanVien.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tableNhanVien.setRequestFocusEnabled(false);
        tableNhanVien.setShowGrid(false);
        tableNhanVien.getTableHeader().setReorderingAllowed(false);
        tableNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableNhanVienMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tableNhanVien);
        if (tableNhanVien.getColumnModel().getColumnCount() > 0) {
            tableNhanVien.getColumnModel().getColumn(0).setResizable(false);
            tableNhanVien.getColumnModel().getColumn(1).setResizable(false);
            tableNhanVien.getColumnModel().getColumn(2).setResizable(false);
            tableNhanVien.getColumnModel().getColumn(3).setResizable(false);
            tableNhanVien.getColumnModel().getColumn(4).setResizable(false);
            tableNhanVien.getColumnModel().getColumn(5).setResizable(false);
            tableNhanVien.getColumnModel().getColumn(6).setResizable(false);
            tableNhanVien.getColumnModel().getColumn(7).setResizable(false);
        }

        nhanvienButton.setBackground(new java.awt.Color(255, 255, 255));

        btnThemNhanVien.setBackground(new java.awt.Color(251, 52, 90));
        btnThemNhanVien.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnThemNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnThemNhanVienMouseClicked(evt);
            }
        });
        btnThemNhanVien.setLayout(new java.awt.GridLayout(1, 0));

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/plus-5-32.png"))); // NOI18N
        btnThemNhanVien.add(jLabel53);

        jLabel20.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Thêm");
        btnThemNhanVien.add(jLabel20);

        btnXoaNhanVien.setBackground(new java.awt.Color(251, 52, 90));
        btnXoaNhanVien.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnXoaNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnXoaNhanVienMouseClicked(evt);
            }
        });
        btnXoaNhanVien.setLayout(new java.awt.GridLayout(1, 0));

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
        jLabel54.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel54MouseClicked(evt);
            }
        });
        btnXoaNhanVien.add(jLabel54);

        jLabel55.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(255, 255, 255));
        jLabel55.setText("Xoá");
        btnXoaNhanVien.add(jLabel55);

        btnSuaNhanVien.setBackground(new java.awt.Color(251, 52, 90));
        btnSuaNhanVien.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnSuaNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSuaNhanVienMouseClicked(evt);
            }
        });
        btnSuaNhanVien.setLayout(new java.awt.GridLayout(1, 0));

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/edit-8-32.png"))); // NOI18N
        btnSuaNhanVien.add(jLabel56);

        jLabel57.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(255, 255, 255));
        jLabel57.setText("Sửa");
        btnSuaNhanVien.add(jLabel57);

        btnChamCong.setBackground(new java.awt.Color(251, 52, 90));
        btnChamCong.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnChamCong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnChamCongMouseClicked(evt);
            }
        });
        btnChamCong.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel73.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/time-8-32.png"))); // NOI18N
        jLabel73.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel73MouseClicked(evt);
            }
        });
        btnChamCong.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 40, 46));

        jLabel81.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(255, 255, 255));
        jLabel81.setText("Chấm công");
        btnChamCong.add(jLabel81, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 90, 46));

        javax.swing.GroupLayout nhanvienButtonLayout = new javax.swing.GroupLayout(nhanvienButton);
        nhanvienButton.setLayout(nhanvienButtonLayout);
        nhanvienButtonLayout.setHorizontalGroup(
            nhanvienButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nhanvienButtonLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnThemNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(btnXoaNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(btnSuaNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addComponent(btnChamCong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        nhanvienButtonLayout.setVerticalGroup(
            nhanvienButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnThemNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnXoaNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnSuaNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnChamCong, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel54.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jPanel55.setLayout(new java.awt.GridLayout(1, 0));

        jPanel56.setLayout(new java.awt.GridLayout(0, 1));

        jLabel58.setText("Tên nhân viên");
        jPanel56.add(jLabel58);

        jLabel59.setText("Tuổi");
        jPanel56.add(jLabel59);

        jLabel60.setText("Địa chỉ");
        jPanel56.add(jLabel60);

        jPanel55.add(jPanel56);

        jPanel57.setLayout(new java.awt.GridLayout(0, 1));
        jPanel57.add(txtTenNV);
        jPanel57.add(txtTuoi);
        jPanel57.add(txtDiaChi);

        jPanel55.add(jPanel57);

        jPanel58.setLayout(new java.awt.GridLayout(1, 0));

        jPanel59.setLayout(new java.awt.GridLayout(0, 1));

        jLabel61.setText("Số điện thoại");
        jPanel59.add(jLabel61);

        jLabel62.setText("Email");
        jPanel59.add(jLabel62);

        jLabel85.setText("Ngày vào làm");
        jPanel59.add(jLabel85);

        jPanel58.add(jPanel59);

        jPanel60.setLayout(new java.awt.GridLayout(0, 1));
        jPanel60.add(txtSDT);
        jPanel60.add(txtEmail);
        jPanel60.add(txtNgayVaoLam);

        jPanel58.add(jPanel60);

        javax.swing.GroupLayout jPanel54Layout = new javax.swing.GroupLayout(jPanel54);
        jPanel54.setLayout(jPanel54Layout);
        jPanel54Layout.setHorizontalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel54Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel55, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel58, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel54Layout.setVerticalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel54Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(nhanvienButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jPanel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(nhanvienButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        cardNhanVien.add(jPanel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 800, 500));

        jPanel22.setBackground(new java.awt.Color(255, 102, 102));
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel32.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setText("Quản Lý Nhân Viên");
        jPanel22.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 220, 50));

        jLabel39.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setIcon(new javax.swing.ImageIcon("/Users/namduong/Downloads/inside-logout-icon.png")); // NOI18N
        jLabel39.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel39MouseClicked(evt);
            }
        });
        jPanel22.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 0, -1, 50));

        searchNhanVien.setFont(new java.awt.Font("Lucida Grande", 0, 15)); // NOI18N
        searchNhanVien.setForeground(new java.awt.Color(204, 204, 204));
        searchNhanVien.setText(" Tìm kiếm...");
        searchNhanVien.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        searchNhanVien.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchNhanVienFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchNhanVienFocusLost(evt);
            }
        });
        searchNhanVien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchNhanVienKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchNhanVienKeyReleased(evt);
            }
        });
        jPanel22.add(searchNhanVien, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 380, 30));

        cardNhanVien.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 50));

        card.add(cardNhanVien, "cardNhanVien");

        cardKhachHang.setBackground(new java.awt.Color(255, 255, 255));
        cardKhachHang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel38.setBackground(new java.awt.Color(255, 255, 255));

        tableKhachHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Tên", "Tuổi", "Địa chỉ", "SDT", "Email", "Phân loại"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableKhachHang.setRequestFocusEnabled(false);
        tableKhachHang.getTableHeader().setReorderingAllowed(false);
        tableKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableKhachHangMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tableKhachHang);
        if (tableKhachHang.getColumnModel().getColumnCount() > 0) {
            tableKhachHang.getColumnModel().getColumn(0).setResizable(false);
            tableKhachHang.getColumnModel().getColumn(1).setResizable(false);
            tableKhachHang.getColumnModel().getColumn(2).setResizable(false);
            tableKhachHang.getColumnModel().getColumn(3).setResizable(false);
            tableKhachHang.getColumnModel().getColumn(4).setResizable(false);
            tableKhachHang.getColumnModel().getColumn(5).setResizable(false);
        }

        nhanvienButton1.setBackground(new java.awt.Color(255, 255, 255));

        btnThemKhachHang.setBackground(new java.awt.Color(251, 52, 90));
        btnThemKhachHang.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnThemKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnThemKhachHangMouseClicked(evt);
            }
        });
        btnThemKhachHang.setLayout(new java.awt.GridLayout(1, 0));

        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel70.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/plus-5-32.png"))); // NOI18N
        btnThemKhachHang.add(jLabel70);

        jLabel31.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("Thêm");
        btnThemKhachHang.add(jLabel31);

        btnXoaKhachHang.setBackground(new java.awt.Color(251, 52, 90));
        btnXoaKhachHang.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnXoaKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnXoaKhachHangMouseClicked(evt);
            }
        });
        btnXoaKhachHang.setLayout(new java.awt.GridLayout(1, 0));

        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel71.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
        btnXoaKhachHang.add(jLabel71);

        jLabel72.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(255, 255, 255));
        jLabel72.setText("Xoá");
        btnXoaKhachHang.add(jLabel72);

        btnSuaKhachHang.setBackground(new java.awt.Color(251, 52, 90));
        btnSuaKhachHang.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnSuaKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSuaKhachHangMouseClicked(evt);
            }
        });
        btnSuaKhachHang.setLayout(new java.awt.GridLayout(1, 0));

        jLabel82.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel82.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/edit-8-32.png"))); // NOI18N
        btnSuaKhachHang.add(jLabel82);

        jLabel83.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel83.setForeground(new java.awt.Color(255, 255, 255));
        jLabel83.setText("Sửa");
        btnSuaKhachHang.add(jLabel83);

        javax.swing.GroupLayout nhanvienButton1Layout = new javax.swing.GroupLayout(nhanvienButton1);
        nhanvienButton1.setLayout(nhanvienButton1Layout);
        nhanvienButton1Layout.setHorizontalGroup(
            nhanvienButton1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nhanvienButton1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnThemKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(btnXoaKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(btnSuaKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        nhanvienButton1Layout.setVerticalGroup(
            nhanvienButton1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnThemKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnXoaKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnSuaKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel61.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jPanel62.setLayout(new java.awt.GridLayout(1, 0));

        jPanel63.setLayout(new java.awt.GridLayout(0, 1));

        jLabel89.setText("Tên khách hàng");
        jPanel63.add(jLabel89);

        jLabel90.setText("Tuổi");
        jPanel63.add(jLabel90);

        jLabel91.setText("Địa chỉ");
        jPanel63.add(jLabel91);

        jPanel62.add(jPanel63);

        jPanel64.setLayout(new java.awt.GridLayout(0, 1));
        jPanel64.add(txtTenKH);
        jPanel64.add(txtTuoiKH);
        jPanel64.add(txtDiaChiKH);

        jPanel62.add(jPanel64);

        jPanel65.setLayout(new java.awt.GridLayout(1, 0));

        jPanel66.setLayout(new java.awt.GridLayout(0, 1));

        jLabel92.setText("Số điện thoại");
        jPanel66.add(jLabel92);

        jLabel93.setText("Email");
        jPanel66.add(jLabel93);

        jLabel36.setText("Phân loại");
        jPanel66.add(jLabel36);

        jPanel65.add(jPanel66);

        jPanel67.setLayout(new java.awt.GridLayout(0, 1));
        jPanel67.add(txtSDTKH);
        jPanel67.add(txtEmailKH);

        selectVIP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Khách thường", "Khách VIP" }));
        jPanel67.add(selectVIP);

        jPanel65.add(jPanel67);

        javax.swing.GroupLayout jPanel61Layout = new javax.swing.GroupLayout(jPanel61);
        jPanel61.setLayout(jPanel61Layout);
        jPanel61Layout.setHorizontalGroup(
            jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel61Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel62, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel65, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel61Layout.setVerticalGroup(
            jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel61Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel61, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4)
                    .addComponent(nhanvienButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jPanel61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(nhanvienButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        cardKhachHang.add(jPanel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 800, 500));

        jPanel23.setBackground(new java.awt.Color(255, 102, 102));
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel33.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("Quản Lý Khách Hàng");
        jPanel23.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 220, 50));

        jLabel45.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(255, 255, 255));
        jLabel45.setIcon(new javax.swing.ImageIcon("/Users/namduong/Downloads/inside-logout-icon.png")); // NOI18N
        jLabel45.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel45MouseClicked(evt);
            }
        });
        jPanel23.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 0, -1, 50));

        searchKhachHang.setFont(new java.awt.Font("Lucida Grande", 0, 15)); // NOI18N
        searchKhachHang.setForeground(new java.awt.Color(204, 204, 204));
        searchKhachHang.setText(" Tìm kiếm...");
        searchKhachHang.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        searchKhachHang.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchKhachHangFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchKhachHangFocusLost(evt);
            }
        });
        searchKhachHang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchKhachHangKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKhachHangKeyReleased(evt);
            }
        });
        jPanel23.add(searchKhachHang, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 380, 30));

        cardKhachHang.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 50));

        card.add(cardKhachHang, "cardKhachHang");

        cardKho.setBackground(new java.awt.Color(255, 255, 255));
        cardKho.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel74.setBackground(new java.awt.Color(255, 255, 255));

        tableKho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Tên nguyên liệu", "Khối lương", "Người nhập", "Ngày nhập", "Xuất xứ", "Trạng thái"
            }
        ));
        tableKho.setRequestFocusEnabled(false);
        tableKho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableKhoMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tableKho);

        jPanel75.setBackground(new java.awt.Color(255, 255, 255));

        btnThemKho.setBackground(new java.awt.Color(251, 52, 90));
        btnThemKho.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnThemKho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnThemKhoMouseClicked(evt);
            }
        });
        btnThemKho.setLayout(new java.awt.GridLayout(1, 0));

        jLabel105.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel105.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/plus-5-32.png"))); // NOI18N
        btnThemKho.add(jLabel105);

        jLabel106.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel106.setForeground(new java.awt.Color(255, 255, 255));
        jLabel106.setText("Thêm");
        btnThemKho.add(jLabel106);

        btnXoaKho.setBackground(new java.awt.Color(251, 52, 90));
        btnXoaKho.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnXoaKho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnXoaKhoMouseClicked(evt);
            }
        });
        btnXoaKho.setLayout(new java.awt.GridLayout(1, 0));

        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel76.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
        btnXoaKho.add(jLabel76);

        jLabel77.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel77.setForeground(new java.awt.Color(255, 255, 255));
        jLabel77.setText("Xoá");
        btnXoaKho.add(jLabel77);

        btnSuaKho.setBackground(new java.awt.Color(251, 52, 90));
        btnSuaKho.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnSuaKho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSuaKhoMouseClicked(evt);
            }
        });
        btnSuaKho.setLayout(new java.awt.GridLayout(1, 0));

        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel78.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/edit-8-32.png"))); // NOI18N
        btnSuaKho.add(jLabel78);

        jLabel79.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(255, 255, 255));
        jLabel79.setText("Sửa");
        btnSuaKho.add(jLabel79);

        javax.swing.GroupLayout jPanel75Layout = new javax.swing.GroupLayout(jPanel75);
        jPanel75.setLayout(jPanel75Layout);
        jPanel75Layout.setHorizontalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnThemKho, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(btnXoaKho, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE)
                .addComponent(btnSuaKho, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        jPanel75Layout.setVerticalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSuaKho, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                    .addComponent(btnXoaKho, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                    .addComponent(btnThemKho, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel76.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jPanel77.setLayout(new java.awt.GridLayout(1, 0));

        jPanel78.setLayout(new java.awt.GridLayout(0, 1));

        jLabel80.setText("Tên nguyên liệu");
        jPanel78.add(jLabel80);

        jLabel86.setText("Khối lượng");
        jPanel78.add(jLabel86);

        jLabel87.setText("Người nhập");
        jPanel78.add(jLabel87);

        jPanel77.add(jPanel78);

        jPanel79.setLayout(new java.awt.GridLayout(0, 1));
        jPanel79.add(txtTenNL);
        jPanel79.add(txtKhoiLuong);
        jPanel79.add(txtTenNguoiNhap);

        jPanel77.add(jPanel79);

        jPanel80.setLayout(new java.awt.GridLayout(1, 0));

        jPanel81.setLayout(new java.awt.GridLayout(0, 1));

        jLabel21.setText("Ngày nhập");
        jPanel81.add(jLabel21);

        jLabel63.setText("Xuất xứ");
        jPanel81.add(jLabel63);
        jPanel81.add(jLabel64);

        jPanel80.add(jPanel81);

        jPanel83.setLayout(new java.awt.GridLayout(0, 1));
        jPanel83.add(txtNgayNhap);
        jPanel83.add(txtXuatXu);
        jPanel83.add(jLabel68);

        jPanel80.add(jPanel83);

        javax.swing.GroupLayout jPanel76Layout = new javax.swing.GroupLayout(jPanel76);
        jPanel76.setLayout(jPanel76Layout);
        jPanel76Layout.setHorizontalGroup(
            jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel76Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel77, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel80, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel76Layout.setVerticalGroup(
            jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel76Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel77, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel80, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel74Layout = new javax.swing.GroupLayout(jPanel74);
        jPanel74.setLayout(jPanel74Layout);
        jPanel74Layout.setHorizontalGroup(
            jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel74Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5)
                    .addComponent(jPanel76, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel75, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel74Layout.setVerticalGroup(
            jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel74Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jPanel76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jPanel75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        cardKho.add(jPanel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 800, 500));

        jPanel29.setBackground(new java.awt.Color(255, 102, 102));
        jPanel29.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel48.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(255, 255, 255));
        jLabel48.setText("Quản Lý Kho");
        jPanel29.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 220, 50));

        jLabel50.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(255, 255, 255));
        jLabel50.setIcon(new javax.swing.ImageIcon("/Users/namduong/Downloads/inside-logout-icon.png")); // NOI18N
        jLabel50.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel50MouseClicked(evt);
            }
        });
        jPanel29.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 0, -1, 50));

        searchKho.setFont(new java.awt.Font("Lucida Grande", 0, 15)); // NOI18N
        searchKho.setForeground(new java.awt.Color(204, 204, 204));
        searchKho.setText(" Tìm kiếm...");
        searchKho.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        searchKho.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchKhoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchKhoFocusLost(evt);
            }
        });
        searchKho.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchKhoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKhoKeyReleased(evt);
            }
        });
        jPanel29.add(searchKho, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 380, 30));

        cardKho.add(jPanel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 50));

        card.add(cardKho, "cardKho");

        jPanel1.add(card, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 0, 800, 550));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDatMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDatMonMouseClicked
        cardLayout.show(card, "cardDatMon");
        redBar(redDatMon);
        fillComboboxDrink();
        fillComboboxCake();
    }//GEN-LAST:event_btnDatMonMouseClicked

    private void btnDatMonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDatMonMouseEntered
        btnDatMon.setBackground(Color.decode("#303545"));
    }//GEN-LAST:event_btnDatMonMouseEntered

    private void btnDatMonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDatMonMouseExited
        btnDatMon.setBackground(Color.decode("#212636"));
    }//GEN-LAST:event_btnDatMonMouseExited

    private void btnQuanLyMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuanLyMonMouseClicked
        cardLayout.show(card, "cardQuanLyMon");
        redBar(redQuanLiMon);
    }//GEN-LAST:event_btnQuanLyMonMouseClicked

    private void btnQuanLyMonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuanLyMonMouseExited
        btnQuanLyMon.setBackground(Color.decode("#212636"));
        
    }//GEN-LAST:event_btnQuanLyMonMouseExited

    private void btnQuanLyMonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuanLyMonMouseEntered
        btnQuanLyMon.setBackground(Color.decode("#303545"));
    }//GEN-LAST:event_btnQuanLyMonMouseEntered

    private void btnNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNhanVienMouseClicked
        cardLayout.show(card, "cardNhanVien");
        redBar(redNhanVien);
    }//GEN-LAST:event_btnNhanVienMouseClicked

    private void btnNhanVienMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNhanVienMouseExited
        btnNhanVien.setBackground(Color.decode("#212636"));
    }//GEN-LAST:event_btnNhanVienMouseExited

    private void btnNhanVienMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNhanVienMouseEntered
        btnNhanVien.setBackground(Color.decode("#303545"));
    }//GEN-LAST:event_btnNhanVienMouseEntered

    private void btnKhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKhoMouseClicked
        cardLayout.show(card, "cardKho");
        redBar(redKho);
    }//GEN-LAST:event_btnKhoMouseClicked

    private void btnKhoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKhoMouseExited
        btnKho.setBackground(Color.decode("#212636"));
    }//GEN-LAST:event_btnKhoMouseExited

    private void btnKhoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKhoMouseEntered
        btnKho.setBackground(Color.decode("#303545"));
    }//GEN-LAST:event_btnKhoMouseEntered

    private void btnThemMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemMonMouseClicked
        if (txtTenMon.getText().equals("") || 
            txtGia.getText().equals("") || 
            txtMoTa.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
        } else {
            String sql = "INSERT INTO SANPHAM(ten_SP, gia_SP, mota_SP, loai_SP, anh_SP) VALUES(?,?,?,?,?)";
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenMon.getText());
                pstmt.setString(2, txtGia.getText());
                pstmt.setString(3, txtMoTa.getText());
                pstmt.setString(4, (String)selectLoai.getSelectedItem());
                pstmt.setString(5, txtURL.getText());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Thêm món thành công!");
                loadDataMon();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Thêm món không thành công!");
            }
            txtTenMon.setText(null);
            txtGia.setText(null); 
            txtMoTa.setText(null); 
            selectLoai.setSelectedItem(null);
            txtURL.setText(null);
        }
    }//GEN-LAST:event_btnThemMonMouseClicked

    private void btnXoaMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaMonMouseClicked
        int indexTB = tableMon.getSelectedRow();
        String selected = tableMon.getValueAt(indexTB, 0).toString();
        int ret = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xoá?", "Xoá thông tin món", JOptionPane.YES_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            if (indexTB < tblModelMon.getRowCount() && indexTB >=0) {
                tblModelMon.removeRow(indexTB);
            }
            String sql = "DELETE FROM SANPHAM where ID_SP = ?";
            try {
                pstmt = conn.prepareStatement(sql);
                // set the corresponding param
                pstmt.setString(1, selected);
                // execute the delete statement
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        txtTenMon.setText(null);
        txtGia.setText(null); 
        txtMoTa.setText(null); 
        selectLoai.setSelectedItem(null);
        txtURL.setText(null);
        
    }//GEN-LAST:event_btnXoaMonMouseClicked

    private void btnSuaMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaMonMouseClicked
        int indexTB = tableMon.getSelectedRow();
        String selected = tableMon.getValueAt(indexTB, 0).toString();
        String sql = "update SANPHAM set ten_SP = ?, gia_SP = ?, mota_SP = ?, loai_SP = ?, anh_SP = ? where id_SP =?";
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenMon.getText());
                pstmt.setString(2, txtGia.getText());
                pstmt.setString(3, txtMoTa.getText());
                pstmt.setString(4, (String) selectLoai.getSelectedItem());
                pstmt.setString(5, txtURL.getText());
                pstmt.setString(6, selected);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Sửa món thành công!");
                loadDataMon();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Sửa món không thành công!");
            }
            txtTenMon.setText(null);
            txtGia.setText(null); 
            txtMoTa.setText(null); 
            selectLoai.setSelectedItem(null);
            txtURL.setText(null);
    }//GEN-LAST:event_btnSuaMonMouseClicked

    private void btnThemNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemNhanVienMouseClicked
        if (txtTenNV.getText().equals("") || 
            txtTuoi.getText().equals("") || 
            txtDiaChi.getText().equals("") ||
            txtSDT.getText().equals("") ||
            txtEmail.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
        } else {
            String sql = "INSERT INTO NHANVIEN(ten_NV, tuoi_NV, diachi_NV, sdt_NV, email_NV, ngayvaolam_NV) VALUES(?,?,?,?,?,?)";
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenNV.getText());
                pstmt.setString(2, txtTuoi.getText());
                pstmt.setString(3, txtDiaChi.getText());
                pstmt.setString(4, txtSDT.getText());
                pstmt.setString(5, txtEmail.getText());
                pstmt.setString(6, ((JTextField) txtNgayVaoLam.getDateEditor().getUiComponent()).getText());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Thêm nhân viên thành công!");
                loadDataNhanVien();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Thêm nhân viên không thành công!");
            }
            txtTenNV.setText(null);
            txtTuoi.setText(null); 
            txtDiaChi.setText(null); 
            txtSDT.setText(null);
            txtEmail.setText(null); 
            ((JTextField) txtNgayVaoLam.getDateEditor().getUiComponent()).setText(null);
        }
    }//GEN-LAST:event_btnThemNhanVienMouseClicked

    private void btnXoaKhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaKhoMouseClicked
        int indexTB = tableKho.getSelectedRow();
        String selected = tableKho.getValueAt(indexTB, 0).toString();
        int ret = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xoá?", "Xoá thông tin nguyên liệu", JOptionPane.YES_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            if (indexTB < tblModelKho.getRowCount() && indexTB >=0) {
                tblModelKho.removeRow(indexTB);
            }
            String sql = "DELETE FROM KHO where ID_NL = ?";
            try {
                pstmt = conn.prepareStatement(sql);
                // set the corresponding param
                pstmt.setString(1, selected);
                // execute the delete statement
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        txtTenNL.setText(null);
        txtKhoiLuong.setText(null); 
        txtXuatXu.setText(null); 
        txtTenNguoiNhap.setText(null);
        ((JTextField) txtNgayNhap.getDateEditor().getUiComponent()).setText(null);
    }//GEN-LAST:event_btnXoaKhoMouseClicked

    private void btnSuaKhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaKhoMouseClicked
        int indexTB = tableKho.getSelectedRow();
        String selected = tableKho.getValueAt(indexTB, 0).toString();
        String sql = "update KHO set ten_NL = ?, khoiluong_NL = ?, ngaynhap_NL = ?, nguoinhap_NL = ?, xuatxu_NL = ? where id_NL =?";
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenNL.getText());
                pstmt.setString(2, txtKhoiLuong.getText());
                pstmt.setString(3, ((JTextField) txtNgayNhap.getDateEditor().getUiComponent()).getText());
                pstmt.setString(4, txtTenNguoiNhap.getText());
                pstmt.setString(5, txtXuatXu.getText());
                pstmt.setString(6, selected);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Sửa nguyên liệu thành công!");
                loadDataKho();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Sửa nguyên liệu không thành công!");
            }
            txtTenNL.setText(null);
            txtKhoiLuong.setText(null); 
            txtTenNguoiNhap.setText(null); 
            txtXuatXu.setText(null);
            ((JTextField) txtNgayNhap.getDateEditor().getUiComponent()).setText(null);
    }//GEN-LAST:event_btnSuaKhoMouseClicked

    private void btnXoaNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaNhanVienMouseClicked
        int indexTB = tableNhanVien.getSelectedRow();
        String selected = tableNhanVien.getValueAt(indexTB, 0).toString();
        int ret = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xoá?", "Xoá thông tin nhân viên", JOptionPane.YES_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            if (indexTB < tblModelNhanVien.getRowCount() && indexTB >=0) {
                tblModelNhanVien.removeRow(indexTB);
            }
            String sql = "DELETE FROM NHANVIEN where ID_NV = ?";
            try {
                pstmt = conn.prepareStatement(sql);
                // set the corresponding param
                pstmt.setString(1, selected);
                // execute the delete statement
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        txtTenNV.setText(null);
        txtTuoi.setText(null); 
        txtDiaChi.setText(null); 
        txtSDT.setText(null);
        txtEmail.setText(null); 
        ((JTextField) txtNgayVaoLam.getDateEditor().getUiComponent()).setText(null);
    }//GEN-LAST:event_btnXoaNhanVienMouseClicked

    private void btnSuaNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaNhanVienMouseClicked
        int indexTB = tableNhanVien.getSelectedRow();
        String selected = tableNhanVien.getValueAt(indexTB, 0).toString();
        String sql = "update NHANVIEN set ten_NV = ?, tuoi_NV = ?, diachi_NV = ?, sdt_NV = ?, email_NV = ?, ngayvaolam_NV = ? where id_NV =?";
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenNV.getText());
                pstmt.setString(2, txtTuoi.getText());
                pstmt.setString(3, txtDiaChi.getText());
                pstmt.setString(4, txtSDT.getText());
                pstmt.setString(5, txtEmail.getText());
                pstmt.setString(6, ((JTextField) txtNgayVaoLam.getDateEditor().getUiComponent()).getText());
                pstmt.setString(7, selected);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Sửa nhân viên thành công!");
                loadDataNhanVien();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Sửa nhân viên không thành công!");
            }
            txtTenNV.setText(null);
            txtTuoi.setText(null); 
            txtDiaChi.setText(null); 
            txtSDT.setText(null);
            txtEmail.setText(null); 
            ((JTextField) txtNgayVaoLam.getDateEditor().getUiComponent()).setText(null);
    }//GEN-LAST:event_btnSuaNhanVienMouseClicked

    private void btnChamCongMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnChamCongMouseClicked
        new ChamCong().setVisible(true);
    }//GEN-LAST:event_btnChamCongMouseClicked

    private void jLabel54MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel54MouseClicked

    }//GEN-LAST:event_jLabel54MouseClicked

    private void tableNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableNhanVienMouseClicked
        try {
            int index = tableNhanVien.getSelectedRow();
            txtTenNV.setText(tableNhanVien.getValueAt(index , 1).toString());
            txtTuoi.setText(tableNhanVien.getValueAt(index , 2).toString());
            txtDiaChi.setText(tableNhanVien.getValueAt(index , 3).toString());
            txtSDT.setText(tableNhanVien.getValueAt(index , 4).toString());
            txtEmail.setText(tableNhanVien.getValueAt(index , 5).toString());
            ((JTextField) txtNgayVaoLam.getDateEditor().getUiComponent()).setText(tableNhanVien.getValueAt(index , 6).toString());
        } catch(Exception e) {}
    }//GEN-LAST:event_tableNhanVienMouseClicked

    private void jLabel73MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel73MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel73MouseClicked

    private void btnKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKhachHangMouseClicked
        cardLayout.show(card, "cardKhachHang");
        redBar(redKhachHang);
    }//GEN-LAST:event_btnKhachHangMouseClicked

    private void btnKhachHangMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKhachHangMouseExited
        btnKhachHang.setBackground(Color.decode("#212636"));
    }//GEN-LAST:event_btnKhachHangMouseExited

    private void btnKhachHangMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKhachHangMouseEntered
        btnKhachHang.setBackground(Color.decode("#303545"));
    }//GEN-LAST:event_btnKhachHangMouseEntered

    private void btnSuaKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaKhachHangMouseClicked
        int indexTB = tableKhachHang.getSelectedRow();
        String selected = tableKhachHang.getValueAt(indexTB, 0).toString();
        String sql = "update KHACHHANG set ten_KH = ?, tuoi_KH = ?, diachi_KH = ?, sdt_KH = ?, email_KH = ?, loai_KH = ? where id_KH =?";
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenKH.getText());
                pstmt.setString(2, txtTuoiKH.getText());
                pstmt.setString(3, txtDiaChiKH.getText());
                pstmt.setString(4, txtSDTKH.getText());
                pstmt.setString(5, txtEmailKH.getText());
                pstmt.setString(6, (String) selectVIP.getSelectedItem());
                pstmt.setString(7, selected);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Sửa khách hàng thành công!");
                loadDataKhachHang();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Sửa khach hàng không thành công!");
            }
            txtTenKH.setText(null);
            txtTuoiKH.setText(null); 
            txtDiaChiKH.setText(null); 
            txtSDTKH.setText(null);
            txtEmailKH.setText(null); 
            selectVIP.setSelectedItem(null);
    }//GEN-LAST:event_btnSuaKhachHangMouseClicked

    private void btnXoaKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaKhachHangMouseClicked
        int indexTB = tableKhachHang.getSelectedRow();
        String selected = tableKhachHang.getValueAt(indexTB, 0).toString();
        int ret = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xoá?", "Xoá thông tin khách hàng", JOptionPane.YES_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            if (indexTB < tblModelKhachHang.getRowCount() && indexTB >=0) {
                tblModelKhachHang.removeRow(indexTB);
            }
            String sql = "DELETE FROM KHACHHANG where ID_KH = ?";
            try {
                pstmt = conn.prepareStatement(sql);
                // set the corresponding param
                pstmt.setString(1, selected);
                // execute the delete statement
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        txtTenKH.setText(null);
        txtTuoiKH.setText(null); 
        txtDiaChiKH.setText(null); 
        txtSDTKH.setText(null);
        txtEmailKH.setText(null); 
    }//GEN-LAST:event_btnXoaKhachHangMouseClicked

    private void btnThemKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemKhachHangMouseClicked
        if (txtTenKH.getText().equals("") || 
            txtTuoiKH.getText().equals("") || 
            txtDiaChiKH.getText().equals("") ||
            txtSDTKH.getText().equals("") ||
            txtEmailKH.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
        } else {
            String sql = "INSERT INTO KHACHHANG(ten_KH, tuoi_KH, diachi_KH, sdt_KH, email_KH, loai_KH) VALUES(?,?,?,?,?,?)";
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenKH.getText());
                pstmt.setString(2, txtTuoiKH.getText());
                pstmt.setString(3, txtDiaChiKH.getText());
                pstmt.setString(4, txtSDTKH.getText());
                pstmt.setString(5, txtEmailKH.getText());
                pstmt.setString(6, (String) selectVIP.getSelectedItem());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Thêm khách hàng thành công!");
                loadDataKhachHang();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Thêm khách hàng không thành công!");
            }
            txtTenKH.setText(null);
            txtTuoiKH.setText(null); 
            txtDiaChiKH.setText(null); 
            txtSDTKH.setText(null);
            txtEmailKH.setText(null); 
            selectVIP.setSelectedItem(null);
        }
    }//GEN-LAST:event_btnThemKhachHangMouseClicked

    private void tableKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableKhachHangMouseClicked
        try {
            int index = tableKhachHang.getSelectedRow();
            txtTenKH.setText(tableKhachHang.getValueAt(index , 1).toString());
            txtTuoiKH.setText(tableKhachHang.getValueAt(index , 2).toString());
            txtDiaChiKH.setText(tableKhachHang.getValueAt(index , 3).toString());
            txtSDTKH.setText(tableKhachHang.getValueAt(index , 4).toString());
            txtEmailKH.setText(tableKhachHang.getValueAt(index , 5).toString());
            selectVIP.setSelectedItem(tableKhachHang.getValueAt(index , 6).toString());
        } catch(Exception e) {}
    }//GEN-LAST:event_tableKhachHangMouseClicked

    private void txtAdminMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAdminMouseEntered
        txtAdmin.setForeground(Color.decode("#ffffff"));
    }//GEN-LAST:event_txtAdminMouseEntered

    private void txtAdminMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAdminMouseExited
        txtAdmin.setForeground(Color.decode("#dddddd"));
    }//GEN-LAST:event_txtAdminMouseExited

    private void tableMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMonMouseClicked
        try {
            int index = tableMon.getSelectedRow();
            txtTenMon.setText(tableMon.getValueAt(index , 1).toString());
            txtGia.setText(tableMon.getValueAt(index , 2).toString());
            txtMoTa.setText(tableMon.getValueAt(index , 3).toString());
            selectLoai.setSelectedItem(tableMon.getValueAt(index , 4).toString());
            txtURL.setText(tableMon.getValueAt(index , 5).toString());
        } catch(Exception e) {}
    }//GEN-LAST:event_tableMonMouseClicked

    private void jLabel35MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel35MouseClicked
        new loginForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel35MouseClicked

    private void jLabel19MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel19MouseClicked
        new loginForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel19MouseClicked

    private void jLabel39MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel39MouseClicked
        new loginForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel39MouseClicked

    private void jLabel45MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel45MouseClicked
        new loginForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel45MouseClicked

    private void jLabel50MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel50MouseClicked
        new loginForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel50MouseClicked

    private void btnDrinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDrinkMouseClicked
        cardLayout2.show(cardCategory, "cardDrink");
        redCategory(redDrink);
    }//GEN-LAST:event_btnDrinkMouseClicked

    private void btnCakeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCakeMouseClicked
        cardLayout2.show(cardCategory, "cardCake");
        redCategory(redCake);
    }//GEN-LAST:event_btnCakeMouseClicked

    private void btnSelectImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectImageActionPerformed
        JFileChooser browerFile = new JFileChooser();
        
        FileNameExtensionFilter fnef = new FileNameExtensionFilter("IMAGES", "png", "jpeg", "jpg");
        
        browerFile.addChoosableFileFilter(fnef);
        
        int showOpenDialogue = browerFile.showOpenDialog(null);
        
        if (showOpenDialogue == JFileChooser.APPROVE_OPTION) {
            File getSelectedFile = browerFile.getSelectedFile();
            String selectedImagePath = getSelectedFile.getAbsolutePath();
            ImageIcon ii = new ImageIcon(selectedImagePath);
            txtURL.setText(selectedImagePath);
        }
    }//GEN-LAST:event_btnSelectImageActionPerformed

    private void btnDrinkMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDrinkMouseEntered
        btnDrink.setBackground(Color.decode("#303545"));
    }//GEN-LAST:event_btnDrinkMouseEntered

    private void btnDrinkMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDrinkMouseExited
        btnDrink.setBackground(Color.decode("#212636"));
    }//GEN-LAST:event_btnDrinkMouseExited

    private void btnCakeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCakeMouseEntered
        btnCake.setBackground(Color.decode("#303545"));
    }//GEN-LAST:event_btnCakeMouseEntered

    private void btnCakeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCakeMouseExited
        btnCake.setBackground(Color.decode("#212636"));
    }//GEN-LAST:event_btnCakeMouseExited

    private void selectCakeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCakeActionPerformed
        fillInfoCake();
    }//GEN-LAST:event_selectCakeActionPerformed

    private void btnOrderCakeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOrderCakeMouseClicked
        if (selectSize_cake.getSelectedIndex() == -1 ||
            selectSoLuong_cake.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin món!");
        } else {
            String row[] = new String[4];
            row[0] = txtTenMon_cake.getText();
            row[1] = selectSize_cake.getSelectedItem().toString();
            row[2] = selectSoLuong_cake.getSelectedItem().toString();
            int soluong = Integer.parseInt(selectSoLuong_cake.getSelectedItem().toString());
            int gia = Integer.parseInt(txtGia_cake.getText());
            if ("L (+20k)".equals(selectSize_cake.getSelectedItem().toString())) {
                gia = gia + 20000;
            }
            if ("M (+10k)".equals(selectSize_cake.getSelectedItem().toString())) {
                gia = gia + 10000;
            }
            row[3] = String.valueOf(soluong * gia);
            tblModelOrderList.addRow(row);
            txtTenMon_cake.setText(null);
            txtGia_cake.setText(null);
            txtMoTa_cake.setText(null);
            selectCake.setSelectedItem(null);
            selectSize_cake.setSelectedItem(null);
            selectSoLuong_cake.setSelectedItem(null);
        }
        tinhTongBill();
    }//GEN-LAST:event_btnOrderCakeMouseClicked

    private void btnXoaOrderListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaOrderListMouseClicked
        int indexTB = tableOrderList.getSelectedRow();
        if (tableOrderList.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món muốn xoá!");
        } else {
            tblModelOrderList.removeRow(indexTB);
        }
        tinhTongBill();
    }//GEN-LAST:event_btnXoaOrderListMouseClicked

    private void btnResetOrderListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnResetOrderListMouseClicked
        ((DefaultTableModel)tableOrderList.getModel()).setNumRows(0);
        tinhTongBill();
    }//GEN-LAST:event_btnResetOrderListMouseClicked
    
    public void strHoaDon() {
        String tenMon = "";
        for (int i = 0; i < tableOrderList.getRowCount(); i++){
            for (int j = 0; j < tableOrderList.getColumnCount(); j++){
                tenMon = tenMon.concat("     " + tableOrderList.getValueAt(i, j).toString() + "\t");
            }
            tenMon = tenMon.concat("\n");
        }
        hoaDon = tenMon;
    }
    
    private void btnThanhToanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThanhToanMouseClicked
        int ret = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn thanh toán?", "Thanh Toán", JOptionPane.YES_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            if (tableOrderList.getRowCount() <= 0) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn món trước khi thanh toán!");
            } else {
                strHoaDon();
                new InHoaDon().setVisible(true);
                insertHOADON();
                insertCTHD();
                tinhTongBill();
                ((DefaultTableModel)tableOrderList.getModel()).setNumRows(0);
                selectVIPCard.setSelectedIndex(0);
                getID_HD();
                int curID_HD = ID_HD + 1;
                txtIDOrder.setText("#" + curID_HD);
            }
        }
    }//GEN-LAST:event_btnThanhToanMouseClicked

    private void btnOrderDrinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOrderDrinkMouseClicked
        if (selectSize_dr.getSelectedIndex() == -1 ||
            selectSoLuong_dr.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin món!");
        } else {
            String row[] = new String[4];
            row[0] = txtTenMon_dr.getText();
            row[1] = selectSize_dr.getSelectedItem().toString();
            row[2] = selectSoLuong_dr.getSelectedItem().toString();
            int soluong = Integer.parseInt(selectSoLuong_dr.getSelectedItem().toString());
            int gia = Integer.parseInt(txtGia_dr.getText());
            if ("L (+20k)".equals(selectSize_dr.getSelectedItem().toString())) {
                gia = gia + 20000;
            } 
            if ("M (+10k)".equals(selectSize_dr.getSelectedItem().toString())) {
                gia = gia + 10000;
            }
            row[3] = String.valueOf(soluong * gia);
            tblModelOrderList.addRow(row);
            txtTenMon_dr.setText(null);
            txtGia_dr.setText(null);
            txtMoTa_dr.setText(null);
            selectMon.setSelectedItem(null);
            selectSize_dr.setSelectedItem(null);
            selectSoLuong_dr.setSelectedItem(null);
        }
        tinhTongBill();
    }//GEN-LAST:event_btnOrderDrinkMouseClicked

    private void selectMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectMonActionPerformed
        fillInfoDrink();
    }//GEN-LAST:event_selectMonActionPerformed

    private void btnThemKhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemKhoMouseClicked
        if (txtTenNL.getText().equals("") || 
            txtKhoiLuong.getText().equals("") || 
            txtTenNguoiNhap.getText().equals("") ||
            txtXuatXu.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
        } else {
            String sql = "INSERT INTO KHO(Ten_NL, khoiluong_NL, nguoinhap_NL, ngaynhap_NL, xuatxu_NL) VALUES(?,?,?,?,?)";
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenNL.getText());
                pstmt.setString(2, txtKhoiLuong.getText());
                pstmt.setString(3, txtTenNguoiNhap.getText());
                pstmt.setString(4, ((JTextField) txtNgayNhap.getDateEditor().getUiComponent()).getText());
                pstmt.setString(5, txtXuatXu.getText());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Thêm nguyên liệu thành công!");
                loadDataKho();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Thêm nguyên liệu không thành công!");
            }
            txtTenNL.setText(null);
            txtKhoiLuong.setText(null); 
            txtTenNguoiNhap.setText(null); 
            txtXuatXu.setText(null);
            ((JTextField) txtNgayNhap.getDateEditor().getUiComponent()).setText(null);
        }
    }//GEN-LAST:event_btnThemKhoMouseClicked

    private void tableKhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableKhoMouseClicked
        try {
            int index = tableKho.getSelectedRow();
            txtTenNL.setText(tableKho.getValueAt(index , 1).toString());
            txtKhoiLuong.setText(tableKho.getValueAt(index , 2).toString());
            txtTenNguoiNhap.setText(tableKho.getValueAt(index , 3).toString());
            ((JTextField) txtNgayNhap.getDateEditor().getUiComponent()).setText(tableKho.getValueAt(index , 4).toString());
            txtXuatXu.setText(tableKho.getValueAt(index , 5).toString());
        } catch(Exception e) {}
    }//GEN-LAST:event_tableKhoMouseClicked

    private void txtAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAdminMouseClicked
        if ("admin".equals(role)) {
            new QuanLyTaiKhoan().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Bạn không có quyền chỉnh sửa tài khoản!");
        }
    }//GEN-LAST:event_txtAdminMouseClicked

    private void searchMonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchMonFocusGained
        if(searchMon.getText().equals(" Tìm kiếm...")){
            searchMon.setCaretPosition(0);
            searchMon.setText(null);
        }
    }//GEN-LAST:event_searchMonFocusGained

    private void searchMonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchMonFocusLost
        searchMon.setText(" Tìm kiếm...");
    }//GEN-LAST:event_searchMonFocusLost

    private void searchMonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchMonKeyPressed
        if(searchMon.getText().equals(" Tìm kiếm...")){
            searchMon.setText(null);
            searchMon.setCaretPosition(0);
        }
    }//GEN-LAST:event_searchMonKeyPressed

    private void searchMonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchMonKeyReleased
        searchTable(searchMon.getText(), tableMon, tblModelMon);
    }//GEN-LAST:event_searchMonKeyReleased

    private void searchNhanVienFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchNhanVienFocusGained
        if(searchNhanVien.getText().equals(" Tìm kiếm...")){
            searchNhanVien.setCaretPosition(0);
            searchNhanVien.setText(null);
        }
    }//GEN-LAST:event_searchNhanVienFocusGained

    private void searchNhanVienFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchNhanVienFocusLost
        searchNhanVien.setText(" Tìm kiếm...");
    }//GEN-LAST:event_searchNhanVienFocusLost

    private void searchNhanVienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchNhanVienKeyPressed
        if(searchNhanVien.getText().equals(" Tìm kiếm...")){
            searchNhanVien.setText(null);
            searchNhanVien.setCaretPosition(0);
        }
    }//GEN-LAST:event_searchNhanVienKeyPressed

    private void searchNhanVienKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchNhanVienKeyReleased
        searchTable(searchNhanVien.getText(), tableNhanVien, tblModelNhanVien);
    }//GEN-LAST:event_searchNhanVienKeyReleased

    private void searchKhachHangFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchKhachHangFocusGained
        if(searchKhachHang.getText().equals(" Tìm kiếm...")){
            searchKhachHang.setCaretPosition(0);
            searchKhachHang.setText(null);
        }
    }//GEN-LAST:event_searchKhachHangFocusGained

    private void searchKhachHangFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchKhachHangFocusLost
        searchKhachHang.setText(" Tìm kiếm...");
    }//GEN-LAST:event_searchKhachHangFocusLost

    private void searchKhachHangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKhachHangKeyPressed
        if(searchKhachHang.getText().equals(" Tìm kiếm...")){
            searchKhachHang.setText(null);
            searchKhachHang.setCaretPosition(0);
        }
    }//GEN-LAST:event_searchKhachHangKeyPressed

    private void searchKhachHangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKhachHangKeyReleased
        searchTable(searchKhachHang.getText(), tableKhachHang, tblModelKhachHang);
    }//GEN-LAST:event_searchKhachHangKeyReleased

    private void searchKhoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchKhoFocusGained
        if(searchKho.getText().equals(" Tìm kiếm...")){
            searchKho.setCaretPosition(0);
            searchKho.setText(null);
        }
    }//GEN-LAST:event_searchKhoFocusGained

    private void searchKhoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchKhoFocusLost
        searchKho.setText(" Tìm kiếm...");
    }//GEN-LAST:event_searchKhoFocusLost

    private void searchKhoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKhoKeyPressed
        if(searchKho.getText().equals(" Tìm kiếm...")){
            searchKho.setText(null);
            searchKho.setCaretPosition(0);
        }
    }//GEN-LAST:event_searchKhoKeyPressed

    private void searchKhoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKhoKeyReleased
        searchTable(searchKho.getText(), tableKho, tblModelKho);
    }//GEN-LAST:event_searchKhoKeyReleased

    private void selectVIPCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectVIPCardActionPerformed
        if (selectVIPCard.getSelectedIndex() == 1) {
            khuyenMai = (float)5/100;
        } else if (selectVIPCard.getSelectedIndex() == 2) {
            khuyenMai = (float)10/100;
        } else {
            khuyenMai = 0;
        }
        tinhTongBill();
    }//GEN-LAST:event_selectVIPCardActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new menu().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel avt;
    private javax.swing.JPanel btnCake;
    private javax.swing.JPanel btnCategory;
    private javax.swing.JPanel btnChamCong;
    private javax.swing.JPanel btnDatMon;
    private javax.swing.JPanel btnDrink;
    private javax.swing.JPanel btnKhachHang;
    private javax.swing.JPanel btnKho;
    private javax.swing.JPanel btnNhanVien;
    private javax.swing.JLabel btnOrderCake;
    private javax.swing.JLabel btnOrderDrink;
    private javax.swing.JPanel btnQuanLyMon;
    private javax.swing.JPanel btnResetOrderList;
    private javax.swing.JButton btnSelectImage;
    private javax.swing.JPanel btnSuaKhachHang;
    private javax.swing.JPanel btnSuaKho;
    private javax.swing.JPanel btnSuaMon;
    private javax.swing.JPanel btnSuaNhanVien;
    private javax.swing.JPanel btnThanhToan;
    private javax.swing.JPanel btnThemKhachHang;
    private javax.swing.JPanel btnThemKho;
    private javax.swing.JPanel btnThemMon;
    private javax.swing.JPanel btnThemNhanVien;
    private javax.swing.JPanel btnXoaKhachHang;
    private javax.swing.JPanel btnXoaKho;
    private javax.swing.JPanel btnXoaMon;
    private javax.swing.JPanel btnXoaNhanVien;
    private javax.swing.JPanel btnXoaOrderList;
    private javax.swing.JPanel card;
    private javax.swing.JPanel cardCake;
    private javax.swing.JPanel cardCategory;
    private javax.swing.JPanel cardDatMon;
    private javax.swing.JPanel cardDrink;
    private javax.swing.JPanel cardKhachHang;
    private javax.swing.JPanel cardKho;
    private javax.swing.JPanel cardNhanVien;
    private javax.swing.JPanel cardQuanLyMon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel54;
    private javax.swing.JPanel jPanel55;
    private javax.swing.JPanel jPanel56;
    private javax.swing.JPanel jPanel57;
    private javax.swing.JPanel jPanel58;
    private javax.swing.JPanel jPanel59;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel60;
    private javax.swing.JPanel jPanel61;
    private javax.swing.JPanel jPanel62;
    private javax.swing.JPanel jPanel63;
    private javax.swing.JPanel jPanel64;
    private javax.swing.JPanel jPanel65;
    private javax.swing.JPanel jPanel66;
    private javax.swing.JPanel jPanel67;
    private javax.swing.JPanel jPanel74;
    private javax.swing.JPanel jPanel75;
    private javax.swing.JPanel jPanel76;
    private javax.swing.JPanel jPanel77;
    private javax.swing.JPanel jPanel78;
    private javax.swing.JPanel jPanel79;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel80;
    private javax.swing.JPanel jPanel81;
    private javax.swing.JPanel jPanel83;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lbCakeImage;
    private javax.swing.JLabel lbDrinkImage;
    private javax.swing.JPanel navbar;
    private javax.swing.JPanel nhanvienButton;
    private javax.swing.JPanel nhanvienButton1;
    private javax.swing.JPanel redCake;
    private javax.swing.JLabel redDatMon;
    private javax.swing.JPanel redDrink;
    private javax.swing.JLabel redKhachHang;
    private javax.swing.JLabel redKho;
    private javax.swing.JLabel redNhanVien;
    private javax.swing.JLabel redQuanLiMon;
    private javax.swing.JTextField searchKhachHang;
    private javax.swing.JTextField searchKho;
    private javax.swing.JTextField searchMon;
    private javax.swing.JTextField searchNhanVien;
    private javax.swing.JComboBox<String> selectCake;
    private javax.swing.JComboBox<String> selectLoai;
    private javax.swing.JComboBox<String> selectMon;
    private javax.swing.JComboBox<String> selectSize_cake;
    private javax.swing.JComboBox<String> selectSize_dr;
    private javax.swing.JComboBox<String> selectSoLuong_cake;
    private javax.swing.JComboBox<String> selectSoLuong_dr;
    private javax.swing.JComboBox<String> selectVIP;
    private javax.swing.JComboBox<String> selectVIPCard;
    private javax.swing.JTable tableKhachHang;
    private javax.swing.JTable tableKho;
    private javax.swing.JTable tableMon;
    private javax.swing.JTable tableNhanVien;
    private javax.swing.JTable tableOrderList;
    private javax.swing.JLabel txtAdmin;
    private javax.swing.JLabel txtDays;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtDiaChiKH;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEmailKH;
    private javax.swing.JTextField txtGia;
    private javax.swing.JLabel txtGia_cake;
    private javax.swing.JLabel txtGia_dr;
    private javax.swing.JLabel txtHours;
    private javax.swing.JLabel txtIDOrder;
    private javax.swing.JTextField txtKhoiLuong;
    private javax.swing.JLabel txtKhuyenMai;
    private javax.swing.JTextField txtMoTa;
    private javax.swing.JLabel txtMoTa_cake;
    private javax.swing.JLabel txtMoTa_dr;
    private com.toedter.calendar.JDateChooser txtNgayNhap;
    private com.toedter.calendar.JDateChooser txtNgayVaoLam;
    private javax.swing.JLabel txtOrderListID;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSDTKH;
    private javax.swing.JTextField txtTenKH;
    private javax.swing.JTextField txtTenMon;
    private javax.swing.JLabel txtTenMon_cake;
    private javax.swing.JLabel txtTenMon_dr;
    private javax.swing.JTextField txtTenNL;
    private javax.swing.JTextField txtTenNV;
    private javax.swing.JTextField txtTenNguoiNhap;
    private javax.swing.JLabel txtThanhTien;
    private javax.swing.JLabel txtThue;
    private javax.swing.JLabel txtTotal;
    private javax.swing.JTextField txtTuoi;
    private javax.swing.JTextField txtTuoiKH;
    private javax.swing.JTextField txtURL;
    private javax.swing.JTextField txtXuatXu;
    // End of variables declaration//GEN-END:variables
}
