/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuanLyQuanCafe;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.filechooser.*;
import java.io.*;
import javax.imageio.ImageIO;

public class menu extends javax.swing.JFrame {
    CardLayout cardLayout = new CardLayout();
    CardLayout cardLayout2 = new CardLayout();
    Connection connection = null;
    PreparedStatement pstmt =  null;
    
    /** 
     * Creates new form gio tao sua file roi luu lai
     */
    public menu() {
        initComponents();
        cardLayout = (CardLayout)(card.getLayout());
        cardLayout2 = (CardLayout)(cardCategory.getLayout());
        connection = ConnectDB.dbConnector();
        this.taoTableNhanVien();
        tableNhanVien.setDefaultEditor(Object.class, null);
        this.taoTableKhachHang();
        tableKhachHang.setDefaultEditor(Object.class, null);
        this.taoTableMon();
        tableMon.setDefaultEditor(Object.class, null);
        fillComboboxDrink();
        fillComboboxCake();
        resetDatMon();
    }
    
    class JPanelGradient extends JPanel {
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            Color color1 = new Color(251,52,90);
            Color color2 = new Color(250,97,60);
            GradientPaint gp = new GradientPaint(200,0,color1,300,h,color2);
            g2d.setPaint(gp);
            g2d.fillRect(0,0,w,h);
        }
    }
    
    private void resetDatMon(){
        selectMon.setSelectedItem(null);
        txtTenMon_or.setText(null);
        txtGia_or.setText(null);
        txtMoTa_or.setText(null);
        selectCake.setSelectedItem(null);
        txtTenMon_cake.setText(null);
        txtGia_cake.setText(null);
        txtMoTa_cake.setText(null);
    }
    
    private void fillComboboxDrink(){
        selectMon.removeAllItems();
        String sql = "select ten_SP, ID_SP from SANPHAM where loai_SP = 'Drink'";
        try (Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
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
        try (Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()) {
                selectCake.addItem(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void fillInfoDrink() {
        String sql = "select * from SANPHAM where TEN_SP = '" + selectMon.getSelectedItem() + "'";
        try (Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            int row = 0;
            while (rs.next()) {
                row++;
                txtTenMon_or.setText(rs.getString(2));
                txtGia_or.setText(rs.getString(3));
                txtMoTa_or.setText(rs.getString(4));
                // hình vừa với khung 
                ImageIcon ii = new ImageIcon(rs.getString(6));
                Image image = ii.getImage().getScaledInstance(lbDrinkImage.getWidth(), lbDrinkImage.getHeight(), Image.SCALE_SMOOTH);
//                lbDrinkImage.setIcon(new ImageIcon(image));
                
//                ImageIcon imgThisImg = new ImageIcon(ii);
                lbDrinkImage.setIcon(new ImageIcon(image));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void fillInfoCake() {
        String sql = "select * from SANPHAM where TEN_SP = '" + selectCake.getSelectedItem() + "'";
        try (Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            int row = 0;
            while (rs.next()) {
                row++;
                txtTenMon_cake.setText(rs.getString(2));
                txtGia_cake.setText(rs.getString(3));
                txtMoTa_cake.setText(rs.getString(4));
                // hình vừa với khung 
                ImageIcon ii = new ImageIcon(rs.getString(6));
                Image image = ii.getImage().getScaledInstance(lbCakeImage.getWidth(), lbCakeImage.getHeight(), Image.SCALE_SMOOTH);
//                lbDrinkImage.setIcon(new ImageIcon(image));
                
//                ImageIcon imgThisImg = new ImageIcon(ii);
                lbCakeImage.setIcon(new ImageIcon(image));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private Connection connect() {
        String url = "jdbc:sqlite:/Users/namduong/NetBeansProjects/QuanLiKhachHangg/src/database/database.db";
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }
    
    DefaultTableModel tblModelNhanVien, tblModelKhachHang, tblModelMon;
    
    public void taoTableNhanVien() {
        tblModelNhanVien = new DefaultTableModel();
        String tieuDe[] = {"ID", "Tên", "Tuổi", "Địa chỉ", "SDT", "Email", "Ngày vào làm", "Số ngày làm"};
        tblModelNhanVien.setColumnIdentifiers(tieuDe);
        loadDataNhanVien();
        setVisible(true);
    }
    public void taoTableKhachHang() {
        tblModelKhachHang = new DefaultTableModel();
        String tieuDe[] = {"ID", "Tên", "Tuổi", "Địa chỉ", "SDT", "Email"};
        tblModelKhachHang.setColumnIdentifiers(tieuDe);
        loadDataKhachHang();
        setVisible(true);
    }
    public void taoTableMon() {
        tblModelMon = new DefaultTableModel();
        String tieuDe[] = {"Mã món", "Tên món", "Giá", "Mô tả", "Loại", "Ảnh"};
        tblModelMon.setColumnIdentifiers(tieuDe);
        loadDataMon();
        setVisible(true);
    }
    
    public void loadDataNhanVien() {
        DefaultTableModel tMOdel = (DefaultTableModel) tableNhanVien.getModel();
        tMOdel.setRowCount(0);
        String sql = "select * from NHANVIEN";
        String row[] = new String[8];
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
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
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()) {
                row[0] = rs.getString(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getString(4);
                row[4] = rs.getString(5);
                row[5] = rs.getString(6);
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
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
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
        jPanel7 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        selectMon = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel42 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        txtTenMon_or = new javax.swing.JLabel();
        txtGia_or = new javax.swing.JLabel();
        txtMoTa_or = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lbDrinkImage = new javax.swing.JLabel();
        cardCake = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        selectCake = new javax.swing.JComboBox<>();
        jLabel47 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel67 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        txtTenMon_cake = new javax.swing.JLabel();
        txtGia_cake = new javax.swing.JLabel();
        txtMoTa_cake = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        lbCakeImage = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        cardQuanLyMon = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableMon = new javax.swing.JTable();
        jPanel21 = new javax.swing.JPanel();
        btnThemMon = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnXoaMon = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        btnSuaMon = new javax.swing.JPanel();
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
        jPanel50 = new javax.swing.JPanel();
        txtMoTa = new javax.swing.JTextField();
        selectLoai = new javax.swing.JComboBox<>();
        jPanel19 = new JPanelGradient();
        jLabel23 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        cardNhanVien = new javax.swing.JPanel();
        jPanel36 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableNhanVien = new javax.swing.JTable();
        nhanvienButton = new javax.swing.JPanel();
        btnThemNhanVien = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnXoaNhanVien = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        btnSuaNhanVien = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        btnChamCong = new javax.swing.JPanel();
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
        cardKhachHang = new javax.swing.JPanel();
        jPanel38 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableKhachHang = new javax.swing.JTable();
        nhanvienButton1 = new javax.swing.JPanel();
        btnThemKhachHang = new javax.swing.JPanel();
        jLabel70 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        btnXoaKhachHang = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        btnSuaKhachHang = new javax.swing.JPanel();
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
        jLabel94 = new javax.swing.JLabel();
        jPanel67 = new javax.swing.JPanel();
        txtSDTKH = new javax.swing.JTextField();
        txtEmailKH = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel23 = new JPanelGradient();
        jLabel33 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        cardKho = new javax.swing.JPanel();
        jPanel74 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jPanel75 = new javax.swing.JPanel();
        btnThemMon4 = new javax.swing.JPanel();
        jLabel75 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        btnXoaMon4 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        btnSuaMon4 = new javax.swing.JPanel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jPanel76 = new javax.swing.JPanel();
        jPanel77 = new javax.swing.JPanel();
        jPanel78 = new javax.swing.JPanel();
        jLabel80 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jPanel79 = new javax.swing.JPanel();
        jTextField18 = new javax.swing.JTextField();
        jTextField19 = new javax.swing.JTextField();
        jTextField20 = new javax.swing.JTextField();
        jPanel80 = new javax.swing.JPanel();
        jPanel81 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jPanel83 = new javax.swing.JPanel();
        jTextField21 = new javax.swing.JTextField();
        jTextField22 = new javax.swing.JTextField();
        jTextField13 = new javax.swing.JTextField();
        jPanel29 = new JPanelGradient();
        jLabel48 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(830, 554));
        setSize(new java.awt.Dimension(830, 554));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        navbar.setBackground(new java.awt.Color(33, 38, 54));
        navbar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        navbar.add(btnDatMon, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 165, 150, -1));

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

        navbar.add(btnQuanLyMon, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 221, 150, -1));

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

        navbar.add(btnNhanVien, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 277, 150, -1));

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

        navbar.add(btnKhachHang, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 333, 150, -1));

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

        navbar.add(btnKho, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 389, 150, -1));

        avt.setBackground(new java.awt.Color(33, 38, 54));
        avt.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setBackground(new java.awt.Color(33, 38, 54));
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/login-icon.png"))); // NOI18N
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel14MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel14MouseEntered(evt);
            }
        });
        avt.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        txtAdmin.setBackground(new java.awt.Color(33, 38, 54));
        txtAdmin.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        txtAdmin.setForeground(new java.awt.Color(221, 221, 221));
        txtAdmin.setText("Admin");
        txtAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtAdminMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtAdminMouseEntered(evt);
            }
        });
        avt.add(txtAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        navbar.add(avt, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 21, -1, 132));

        jPanel1.add(navbar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 550));

        card.setLayout(new java.awt.CardLayout());

        cardDatMon.setBackground(new java.awt.Color(255, 255, 255));
        cardDatMon.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel11.setBackground(new java.awt.Color(251, 52, 90));
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
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        btnCategoryLayout.setVerticalGroup(
            btnCategoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnCategoryLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        cardCategory.setLayout(new java.awt.CardLayout());

        jLabel10.setText("Chọn món");

        selectMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectMonActionPerformed(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel36.setText("Thông tin:");

        jLabel37.setText("Tên");

        jLabel44.setText("Giá");

        jLabel49.setText("Mô tả");

        jLabel38.setText("Size");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "M", "L" }));

        jLabel42.setText("Số lượng");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.LEADING, 0, 201, Short.MAX_VALUE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtMoTa_or, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtGia_or, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTenMon_or, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(selectMon, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectMon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTenMon_or, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGia_or, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMoTa_or, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/PngItem_5679765.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                .addGap(89, 89, 89))
        );

        lbDrinkImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/a.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(lbDrinkImage, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbDrinkImage, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout cardDrinkLayout = new javax.swing.GroupLayout(cardDrink);
        cardDrink.setLayout(cardDrinkLayout);
        cardDrinkLayout.setHorizontalGroup(
            cardDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(cardDrinkLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        cardDrinkLayout.setVerticalGroup(
            cardDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardDrinkLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(cardDrinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        cardCategory.add(cardDrink, "cardDrink");

        jLabel43.setText("Chọn món");

        selectCake.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCakeActionPerformed(evt);
            }
        });

        jLabel47.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel47.setText("Thông tin:");

        jLabel51.setText("Tên");

        jLabel52.setText("Giá");

        jLabel65.setText("Mô tả");

        jLabel66.setText("Size");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "M", "L" }));

        jLabel67.setText("Số lượng");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel67, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addComponent(jLabel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel43, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.Alignment.LEADING, 0, 201, Short.MAX_VALUE)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtMoTa_cake, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtGia_cake, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTenMon_cake, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(selectCake, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectCake, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTenMon_cake, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGia_cake, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMoTa_cake, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel68.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/PngItem_5679765.png"))); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel68)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                .addGap(89, 89, 89))
        );

        lbCakeImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/a.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(lbCakeImage, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbCakeImage, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout cardCakeLayout = new javax.swing.GroupLayout(cardCake);
        cardCake.setLayout(cardCakeLayout);
        cardCakeLayout.setHorizontalGroup(
            cardCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(cardCakeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        cardCakeLayout.setVerticalGroup(
            cardCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardCakeLayout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(cardCakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 231, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        cardCategory.add(cardCake, "cardCake");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(cardCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(30, 30, 30)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(btnCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(cardCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        cardDatMon.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 480, 500));

        jPanel3.setBackground(new java.awt.Color(33, 38, 54));

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Order List");
        jLabel2.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(251, 52, 90)));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tên", "Size", "Số lượng", "Giá"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 316, Short.MAX_VALUE))
        );

        cardDatMon.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 50, 320, 500));

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
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnThemMonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnThemMonMouseEntered(evt);
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
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnXoaMonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnXoaMonMouseEntered(evt);
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
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSuaMonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSuaMonMouseEntered(evt);
            }
        });
        btnSuaMon.setLayout(new java.awt.GridLayout(1, 0));

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
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

        jPanel46.add(jPanel49);

        jPanel50.setLayout(new java.awt.GridLayout(0, 1));
        jPanel50.add(txtMoTa);

        selectLoai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Drink", "Cake" }));
        jPanel50.add(selectLoai);

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
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        tableNhanVien.setSelectionForeground(new java.awt.Color(51, 51, 0));
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
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnThemNhanVienMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnThemNhanVienMouseEntered(evt);
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
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnXoaNhanVienMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnXoaNhanVienMouseEntered(evt);
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
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSuaNhanVienMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSuaNhanVienMouseEntered(evt);
            }
        });
        btnSuaNhanVien.setLayout(new java.awt.GridLayout(1, 0));

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
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
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnChamCongMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnChamCongMouseEntered(evt);
            }
        });
        btnChamCong.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel73.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
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

        cardNhanVien.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 50));

        card.add(cardNhanVien, "cardNhanVien");

        cardKhachHang.setBackground(new java.awt.Color(255, 255, 255));
        cardKhachHang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel38.setBackground(new java.awt.Color(255, 255, 255));

        tableKhachHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Tên", "Tuổi", "Địa chỉ", "SDT", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnThemKhachHangMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnThemKhachHangMouseEntered(evt);
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
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnXoaKhachHangMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnXoaKhachHangMouseEntered(evt);
            }
        });
        btnXoaKhachHang.setLayout(new java.awt.GridLayout(1, 0));

        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel71.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
        jLabel71.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel71MouseClicked(evt);
            }
        });
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
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSuaKhachHangMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSuaKhachHangMouseEntered(evt);
            }
        });
        btnSuaKhachHang.setLayout(new java.awt.GridLayout(1, 0));

        jLabel82.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel82.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
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
        jPanel66.add(jLabel94);

        jPanel65.add(jPanel66);

        jPanel67.setLayout(new java.awt.GridLayout(0, 1));
        jPanel67.add(txtSDTKH);
        jPanel67.add(txtEmailKH);
        jPanel67.add(jLabel4);

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
                .addContainerGap(32, Short.MAX_VALUE))
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

        cardKhachHang.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 50));

        card.add(cardKhachHang, "cardKhachHang");

        cardKho.setBackground(new java.awt.Color(255, 255, 255));
        cardKho.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel74.setBackground(new java.awt.Color(255, 255, 255));

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(jTable5);

        jPanel75.setBackground(new java.awt.Color(255, 255, 255));

        btnThemMon4.setBackground(new java.awt.Color(251, 52, 90));
        btnThemMon4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnThemMon4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnThemMon4MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnThemMon4MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnThemMon4MouseEntered(evt);
            }
        });
        btnThemMon4.setLayout(new java.awt.GridLayout(1, 0));

        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel75.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/plus-5-32.png"))); // NOI18N
        btnThemMon4.add(jLabel75);

        jLabel22.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Thêm");
        btnThemMon4.add(jLabel22);

        btnXoaMon4.setBackground(new java.awt.Color(251, 52, 90));
        btnXoaMon4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnXoaMon4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnXoaMon4MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnXoaMon4MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnXoaMon4MouseEntered(evt);
            }
        });
        btnXoaMon4.setLayout(new java.awt.GridLayout(1, 0));

        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel76.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
        btnXoaMon4.add(jLabel76);

        jLabel77.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel77.setForeground(new java.awt.Color(255, 255, 255));
        jLabel77.setText("Xoá");
        btnXoaMon4.add(jLabel77);

        btnSuaMon4.setBackground(new java.awt.Color(251, 52, 90));
        btnSuaMon4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnSuaMon4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSuaMon4MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSuaMon4MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSuaMon4MouseEntered(evt);
            }
        });
        btnSuaMon4.setLayout(new java.awt.GridLayout(1, 0));

        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel78.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuanLyQuanCafe/img/minus-5-32.png"))); // NOI18N
        btnSuaMon4.add(jLabel78);

        jLabel79.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(255, 255, 255));
        jLabel79.setText("Sửa");
        btnSuaMon4.add(jLabel79);

        javax.swing.GroupLayout jPanel75Layout = new javax.swing.GroupLayout(jPanel75);
        jPanel75.setLayout(jPanel75Layout);
        jPanel75Layout.setHorizontalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnThemMon4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(btnXoaMon4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSuaMon4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        jPanel75Layout.setVerticalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSuaMon4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnXoaMon4, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(btnThemMon4, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel76.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jPanel77.setLayout(new java.awt.GridLayout(1, 0));

        jPanel78.setLayout(new java.awt.GridLayout(0, 1));

        jLabel80.setText("thêm gì đéo biết");
        jPanel78.add(jLabel80);

        jLabel86.setText("thêm gì đéo biết");
        jPanel78.add(jLabel86);

        jLabel87.setText("thêm gì đéo biết");
        jPanel78.add(jLabel87);

        jPanel77.add(jPanel78);

        jPanel79.setLayout(new java.awt.GridLayout(0, 1));
        jPanel79.add(jTextField18);
        jPanel79.add(jTextField19);
        jPanel79.add(jTextField20);

        jPanel77.add(jPanel79);

        jPanel80.setLayout(new java.awt.GridLayout(1, 0));

        jPanel81.setLayout(new java.awt.GridLayout(0, 1));

        jLabel21.setText("jLabel21");
        jPanel81.add(jLabel21);

        jLabel63.setText("jLabel63");
        jPanel81.add(jLabel63);

        jLabel64.setText("jLabel64");
        jPanel81.add(jLabel64);

        jPanel80.add(jPanel81);

        jPanel83.setLayout(new java.awt.GridLayout(0, 1));
        jPanel83.add(jTextField21);
        jPanel83.add(jTextField22);
        jPanel83.add(jTextField13);

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
                .addContainerGap(15, Short.MAX_VALUE))
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
                .addGap(37, 37, 37)
                .addComponent(jPanel75, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        cardKho.add(jPanel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 800, 500));

        jPanel29.setBackground(new java.awt.Color(251, 52, 90));
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
                Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenMon.getText());
                pstmt.setString(2, txtGia.getText());
                pstmt.setString(3, txtMoTa.getText());
                pstmt.setString(4, (String)selectLoai.getSelectedItem());
                pstmt.setString(5, txtURL.getText());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Thêm món thành công!");
                loadDataMon();
                conn.close();
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

    private void btnThemMonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemMonMouseExited
        btnThemMon.setBackground(Color.decode("#FB345A"));
    }//GEN-LAST:event_btnThemMonMouseExited

    private void btnThemMonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemMonMouseEntered
        // TODO add your handling code here:
        btnThemMon.setBackground(Color.decode("#DD163C"));
    }//GEN-LAST:event_btnThemMonMouseEntered

    private void btnXoaMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaMonMouseClicked
        int indexTB = tableMon.getSelectedRow();
        String selected = tableMon.getValueAt(indexTB, 0).toString();
        int ret = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xoá?", "Xoá thông tin món", JOptionPane.YES_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            if (indexTB < tblModelMon.getRowCount() && indexTB >=0) {
                tblModelMon.removeRow(indexTB);
            }
            String sql = "DELETE FROM SANPHAM where ID_SP = ?";
            try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
    }//GEN-LAST:event_btnXoaMonMouseClicked

    private void btnXoaMonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaMonMouseExited
        btnXoaMon.setBackground(Color.decode("#FB345A"));
    }//GEN-LAST:event_btnXoaMonMouseExited

    private void btnXoaMonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaMonMouseEntered
        btnXoaMon.setBackground(Color.decode("#DD163C"));
    }//GEN-LAST:event_btnXoaMonMouseEntered

    private void btnSuaMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaMonMouseClicked
        int indexTB = tableMon.getSelectedRow();
        String selected = tableMon.getValueAt(indexTB, 0).toString();
        String sql = "update SANPHAM set ten_SP = ?, gia_SP = ?, mota_SP = ?, loai_SP = ?, anh_SP = ? where id_SP =?";
            try {
                Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenMon.getText());
                pstmt.setString(2, txtGia.getText());
                pstmt.setString(3, txtMoTa.getText());
                pstmt.setString(4, (String) selectLoai.getSelectedItem());
                pstmt.setString(5, txtURL.getText());
                pstmt.setString(6, selected);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Sửa món thành công!");
                loadDataMon();
                conn.close();
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

    private void btnSuaMonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaMonMouseExited
        btnSuaMon.setBackground(Color.decode("#FB345A"));
    }//GEN-LAST:event_btnSuaMonMouseExited

    private void btnSuaMonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaMonMouseEntered
        btnSuaMon.setBackground(Color.decode("#DD163C"));
    }//GEN-LAST:event_btnSuaMonMouseEntered

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
                Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenNV.getText());
                pstmt.setString(2, txtTuoi.getText());
                pstmt.setString(3, txtDiaChi.getText());
                pstmt.setString(4, txtSDT.getText());
                pstmt.setString(5, txtEmail.getText());
                pstmt.setString(6, ((JTextField) txtNgayVaoLam.getDateEditor().getUiComponent()).getText());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Thêm nhân viên thành công!");
                loadDataNhanVien();
                conn.close();
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

    private void btnThemNhanVienMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemNhanVienMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThemNhanVienMouseExited

    private void btnThemNhanVienMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemNhanVienMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThemNhanVienMouseEntered

    private void btnThemMon4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemMon4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThemMon4MouseClicked

    private void btnThemMon4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemMon4MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThemMon4MouseExited

    private void btnThemMon4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemMon4MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThemMon4MouseEntered

    private void btnXoaMon4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaMon4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXoaMon4MouseClicked

    private void btnXoaMon4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaMon4MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXoaMon4MouseExited

    private void btnXoaMon4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaMon4MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXoaMon4MouseEntered

    private void btnSuaMon4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaMon4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSuaMon4MouseClicked

    private void btnSuaMon4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaMon4MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSuaMon4MouseExited

    private void btnSuaMon4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaMon4MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSuaMon4MouseEntered

    private void btnXoaNhanVienMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaNhanVienMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXoaNhanVienMouseEntered

    private void btnXoaNhanVienMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaNhanVienMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXoaNhanVienMouseExited

    private void btnXoaNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaNhanVienMouseClicked
        int indexTB = tableNhanVien.getSelectedRow();
        String selected = tableNhanVien.getValueAt(indexTB, 0).toString();
        int ret = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xoá?", "Xoá thông tin nhân viên", JOptionPane.YES_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            if (indexTB < tblModelNhanVien.getRowCount() && indexTB >=0) {
                tblModelNhanVien.removeRow(indexTB);
            }
            String sql = "DELETE FROM NHANVIEN where ID_NV = ?";
            try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    private void btnSuaNhanVienMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaNhanVienMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSuaNhanVienMouseEntered

    private void btnSuaNhanVienMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaNhanVienMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSuaNhanVienMouseExited

    private void btnSuaNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaNhanVienMouseClicked
        int indexTB = tableNhanVien.getSelectedRow();
        String selected = tableNhanVien.getValueAt(indexTB, 0).toString();
        String sql = "update NHANVIEN set ten_NV = ?, tuoi_NV = ?, diachi_NV = ?, sdt_NV = ?, email_NV = ?, ngayvaolam_NV = ? where id_NV =?";
            try {
                Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
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
                conn.close();
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

    private void btnChamCongMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnChamCongMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnChamCongMouseExited

    private void btnChamCongMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnChamCongMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnChamCongMouseEntered

    private void jLabel54MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel54MouseClicked

    }//GEN-LAST:event_jLabel54MouseClicked

    private void tableNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableNhanVienMouseClicked
        int index = tableNhanVien.getSelectedRow();
        txtTenNV.setText(tableNhanVien.getValueAt(index , 1).toString());
        txtTuoi.setText(tableNhanVien.getValueAt(index , 2).toString());
        txtDiaChi.setText(tableNhanVien.getValueAt(index , 3).toString());
        txtSDT.setText(tableNhanVien.getValueAt(index , 4).toString());
        txtEmail.setText(tableNhanVien.getValueAt(index , 5).toString());
        ((JTextField) txtNgayVaoLam.getDateEditor().getUiComponent()).setText(tableNhanVien.getValueAt(index , 6).toString());
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

    private void btnSuaKhachHangMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaKhachHangMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSuaKhachHangMouseEntered

    private void btnSuaKhachHangMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaKhachHangMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSuaKhachHangMouseExited

    private void btnSuaKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSuaKhachHangMouseClicked
        int indexTB = tableKhachHang.getSelectedRow();
        String selected = tableKhachHang.getValueAt(indexTB, 0).toString();
        String sql = "update KHACHHANG set ten_KH = ?, tuoi_KH = ?, diachi_KH = ?, sdt_KH = ?, email_KH = ? where id_KH =?";
            try {
                Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenKH.getText());
                pstmt.setString(2, txtTuoiKH.getText());
                pstmt.setString(3, txtDiaChiKH.getText());
                pstmt.setString(4, txtSDTKH.getText());
                pstmt.setString(5, txtEmailKH.getText());
                pstmt.setString(6, selected);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Sửa khách hàng thành công!");
                loadDataKhachHang();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Sửa khach hàng không thành công!");
            }
            txtTenKH.setText(null);
            txtTuoiKH.setText(null); 
            txtDiaChiKH.setText(null); 
            txtSDTKH.setText(null);
            txtEmailKH.setText(null); 
    }//GEN-LAST:event_btnSuaKhachHangMouseClicked

    private void btnXoaKhachHangMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaKhachHangMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXoaKhachHangMouseEntered

    private void btnXoaKhachHangMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaKhachHangMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXoaKhachHangMouseExited

    private void btnXoaKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnXoaKhachHangMouseClicked
        int indexTB = tableKhachHang.getSelectedRow();
        String selected = tableKhachHang.getValueAt(indexTB, 0).toString();
        int ret = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xoá?", "Xoá thông tin khách hàng", JOptionPane.YES_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            if (indexTB < tblModelKhachHang.getRowCount() && indexTB >=0) {
                tblModelKhachHang.removeRow(indexTB);
            }
            String sql = "DELETE FROM KHACHHANG where ID_KH = ?";
            try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    private void jLabel71MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel71MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel71MouseClicked

    private void btnThemKhachHangMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemKhachHangMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThemKhachHangMouseEntered

    private void btnThemKhachHangMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemKhachHangMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThemKhachHangMouseExited

    private void btnThemKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnThemKhachHangMouseClicked
        if (txtTenKH.getText().equals("") || 
            txtTuoiKH.getText().equals("") || 
            txtDiaChiKH.getText().equals("") ||
            txtSDTKH.getText().equals("") ||
            txtEmailKH.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
        } else {
            String sql = "INSERT INTO KHACHHANG(ten_KH, tuoi_KH, diachi_KH, sdt_KH, email_KH) VALUES(?,?,?,?,?)";
            try {
                Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtTenKH.getText());
                pstmt.setString(2, txtTuoiKH.getText());
                pstmt.setString(3, txtDiaChiKH.getText());
                pstmt.setString(4, txtSDTKH.getText());
                pstmt.setString(5, txtEmailKH.getText());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Thêm khách hàng thành công!");
                loadDataKhachHang();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Thêm khách hàng không thành công!");
            }
            txtTenKH.setText(null);
            txtTuoiKH.setText(null); 
            txtDiaChiKH.setText(null); 
            txtSDTKH.setText(null);
            txtEmailKH.setText(null); 
        }
    }//GEN-LAST:event_btnThemKhachHangMouseClicked

    private void tableKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableKhachHangMouseClicked
        int index = tableKhachHang.getSelectedRow();
        txtTenKH.setText(tableKhachHang.getValueAt(index , 1).toString());
        txtTuoiKH.setText(tableKhachHang.getValueAt(index , 2).toString());
        txtDiaChiKH.setText(tableKhachHang.getValueAt(index , 3).toString());
        txtSDTKH.setText(tableKhachHang.getValueAt(index , 4).toString());
        txtEmailKH.setText(tableKhachHang.getValueAt(index , 5).toString());
    }//GEN-LAST:event_tableKhachHangMouseClicked

    private void txtAdminMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAdminMouseEntered
        txtAdmin.setForeground(Color.decode("#ffffff"));
    }//GEN-LAST:event_txtAdminMouseEntered

    private void txtAdminMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAdminMouseExited
        txtAdmin.setForeground(Color.decode("#dddddd"));
    }//GEN-LAST:event_txtAdminMouseExited

    private void jLabel14MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseEntered
        txtAdmin.setForeground(Color.decode("#ffffff"));
    }//GEN-LAST:event_jLabel14MouseEntered

    private void jLabel14MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseExited
        txtAdmin.setForeground(Color.decode("#dddddd"));
    }//GEN-LAST:event_jLabel14MouseExited

    private void tableMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMonMouseClicked
       int index = tableMon.getSelectedRow();
        txtTenMon.setText(tableMon.getValueAt(index , 1).toString());
        txtGia.setText(tableMon.getValueAt(index , 2).toString());
        txtMoTa.setText(tableMon.getValueAt(index , 3).toString());
        selectLoai.setSelectedItem(tableMon.getValueAt(index , 4).toString());
        txtURL.setText(tableMon.getValueAt(index , 5).toString());
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

    private void selectMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectMonActionPerformed
        fillInfoDrink();
    }//GEN-LAST:event_selectMonActionPerformed

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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new menu().setVisible(true);
                System.out.print(checkLogin.getLoggedrole());
            }
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
    private javax.swing.JPanel btnQuanLyMon;
    private javax.swing.JButton btnSelectImage;
    private javax.swing.JPanel btnSuaKhachHang;
    private javax.swing.JPanel btnSuaMon;
    private javax.swing.JPanel btnSuaMon4;
    private javax.swing.JPanel btnSuaNhanVien;
    private javax.swing.JPanel btnThemKhachHang;
    private javax.swing.JPanel btnThemMon;
    private javax.swing.JPanel btnThemMon4;
    private javax.swing.JPanel btnThemNhanVien;
    private javax.swing.JPanel btnXoaKhachHang;
    private javax.swing.JPanel btnXoaMon;
    private javax.swing.JPanel btnXoaMon4;
    private javax.swing.JPanel btnXoaNhanVien;
    private javax.swing.JPanel card;
    private javax.swing.JPanel cardCake;
    private javax.swing.JPanel cardCategory;
    private javax.swing.JPanel cardDatMon;
    private javax.swing.JPanel cardDrink;
    private javax.swing.JPanel cardKhachHang;
    private javax.swing.JPanel cardKho;
    private javax.swing.JPanel cardNhanVien;
    private javax.swing.JPanel cardQuanLyMon;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
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
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
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
    private javax.swing.JPanel jPanel5;
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
    private javax.swing.JPanel jPanel7;
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
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable5;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
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
    private javax.swing.JComboBox<String> selectCake;
    private javax.swing.JComboBox<String> selectLoai;
    private javax.swing.JComboBox<String> selectMon;
    private javax.swing.JTable tableKhachHang;
    private javax.swing.JTable tableMon;
    private javax.swing.JTable tableNhanVien;
    private javax.swing.JLabel txtAdmin;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtDiaChiKH;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEmailKH;
    private javax.swing.JTextField txtGia;
    private javax.swing.JLabel txtGia_cake;
    private javax.swing.JLabel txtGia_or;
    private javax.swing.JTextField txtMoTa;
    private javax.swing.JLabel txtMoTa_cake;
    private javax.swing.JLabel txtMoTa_or;
    private com.toedter.calendar.JDateChooser txtNgayVaoLam;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSDTKH;
    private javax.swing.JTextField txtTenKH;
    private javax.swing.JTextField txtTenMon;
    private javax.swing.JLabel txtTenMon_cake;
    private javax.swing.JLabel txtTenMon_or;
    private javax.swing.JTextField txtTenNV;
    private javax.swing.JTextField txtTuoi;
    private javax.swing.JTextField txtTuoiKH;
    private javax.swing.JTextField txtURL;
    // End of variables declaration//GEN-END:variables
}
