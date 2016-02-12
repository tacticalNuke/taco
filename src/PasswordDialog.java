import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
public class PasswordDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField loginTextField;
    private JPasswordField passwordTextField;

    public PasswordDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{onOK();}
                catch(SQLException e1){
                    e1.printStackTrace();
                }
                catch(ClassNotFoundException e2){
                    e2.printStackTrace();
                }
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e3) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e3) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() throws SQLException, ClassNotFoundException {
        Connection c = null;
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:test.bd");
        PreparedStatement ps = c.prepareStatement("SELECT * FROM LOGINS WHERE LOGIN = (?)");
        ps.setString(1,loginTextField.getText());
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            if(rs.getString("PASSWORD").compareTo(md5(passwordTextField.getText() + rs.getString("SALT")))==0){
                new MainWindow();
                dispose();
            }else{
                passwordTextField.setText("wrongPassword");
            }
        }else{
            loginTextField.setText("wrongUserName");
        }
    }

    private void onCancel() {
        dispose();
    }

    public static String md5(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
    public static void main(String[] args) {
        PasswordDialog dialog = new PasswordDialog();
        dialog.pack();
        dialog.setVisible(true);
    }
}
