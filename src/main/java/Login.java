//Import necessary libraries
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.ui.FlatLineBorder;
import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import static constants.RegexConstants.*;
import static constants.DatabaseConstants.*;
import static constants.Colors.*;
import static features.SoundPlayer.*;
import features.ForgotPassword;

public class Login extends JFrame implements KeyListener{
    
    //Components initialization
    private JPanel contentPanel;
    private final JPanel signupP, loginP;
    private final JLabel sLabel, lLabel, sLabelLink, lLabelLink, forgotPasswordLink, qrCodeLoginLink, lErrorLabel, sErrorLabel;
    private final JTextField sUser, sEmail;
    private final JPasswordField sPass;
    public final JTextField lUser;
    public final JPasswordField lPass;
    private static int loginAttempt = 0;
    private static long lockTime = 0;
    private static JButton sButton;
    public static JButton lButton;

    //Login constructor
    public Login() {
        setTitle("Connect - Log In");
        loginSound();
        UIManager.put("defaultFont", new Font(FlatInterFont.FAMILY, Font.PLAIN, 13));

        // -- GUI creation and designs --
        contentPanel = new JPanel();
        contentPanel.setBackground(DGRAY);
        contentPanel.setSize(700, 300);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ----- SIGN UP PART -----
        signupP = new JPanel(new GridLayout(6, 1, 0, 22));
        signupP.setSize(352, 612);
        signupP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        URL logoURL = getClass().getResource("/icons/Favicon.png");
        ImageIcon logo = null;
        if (logoURL != null) {
            logo = new ImageIcon(logoURL);
        }


        sLabel = new JLabel("Sign Up", JLabel.CENTER);
        sLabel.setIcon(logo);
        sLabel.setIconTextGap(10);
        sLabel.setFont(new Font(FlatInterFont.FAMILY, Font.BOLD, 20));
        sLabel.setForeground(UIManager.getColor("Label.foreground"));

        sUser = new JTextField(20);
        sUser.addKeyListener(this);
        sUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        sUser.setToolTipText("Username");
        sUser.setCaretColor(accentColor);

        sEmail = new JTextField(20);
        sEmail.addKeyListener(this);
        sEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        sEmail.setToolTipText("Email");
        sEmail.setCaretColor(accentColor);

        sPass = new JPasswordField();
        sPass.addKeyListener(this);
        sPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        sPass.setToolTipText("Password");
        sPass.setCaretColor(accentColor);

        textFieldDesigner(sUser);
        textFieldDesigner(sEmail);
        passwordFieldDesigner(sPass);

        sButton = new JButton("Sign Up");
        sButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sButton.setForeground(accentColor);
        sButton.setBackground(backgroundColor);
        buttonDesigner(sButton);
        sButton.setFocusable(false);
        sButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sButton.setBackground(accentColor);
                sButton.setForeground(WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sButton.setBackground(backgroundColor);
                sButton.setForeground(accentColor);
            }

        });
        sButton.addActionListener(e -> new Thread(this::handleSignUp).start());

        sErrorLabel = new JLabel();
        sErrorLabel.setText("");
        URL errorPath = getClass().getResource("/icons/error.png");
        if (errorPath != null) {
            ImageIcon errorIcon = new ImageIcon(errorPath);
            sErrorLabel.setIcon(errorIcon);
        } else {
            errorSound();
            System.err.println("Error: Resource not found: /icons/error.png");
        }
        sErrorLabel.setOpaque(true);
        sErrorLabel.setForeground(new Color(255, 255, 255));
        sErrorLabel.setBackground(errorColor);
        sErrorLabel.setBorder(new FlatLineBorder(new Insets(5, 10, 5, 10), RED, 1f, 12));
        sErrorLabel.setVisible(false);

        URL loginWhiteURL = getClass().getResource("/icons/login_white3.png");
        URL loginBlueURL = getClass().getResource("/icons/login_blue3.png");
        ImageIcon loginWhite = new ImageIcon(loginWhiteURL);
        ImageIcon loginBlue = new ImageIcon(loginBlueURL);
        Image scaledLoginWhite = loginWhite.getImage().getScaledInstance(21, 21, Image.SCALE_SMOOTH);
        Image scaledLoginBlue = loginBlue.getImage().getScaledInstance(21, 21, Image.SCALE_SMOOTH);
        ImageIcon scaledLoginWhiteIcon = new ImageIcon(scaledLoginWhite);
        ImageIcon scaledLoginBlueIcon = new ImageIcon(scaledLoginBlue);

        sLabelLink = new JLabel("Already have an account? Log in here!");
        sLabelLink.setForeground(WHITE);
        sLabelLink.setIcon(scaledLoginWhiteIcon);
        sLabelLink.setIconTextGap(10);
        sLabelLink.setHorizontalTextPosition(SwingConstants.LEFT);
        sLabelLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sLabelLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setTitle("Connect - Log In");
                    loginSound();
                    sUser.setText("");
                    sPass.setText("");
                    sEmail.setText("");
                    lUser.setText("");
                    lPass.setText("");
                    new Thread(() -> {
                        SwingUtilities.invokeLater(() -> {
                            // UI update logic here (same as inside your Timer)
                            loginP.remove(lErrorLabel);
                            loginP.remove(lLabelLink);
                            loginP.remove(forgotPasswordLink);
                            loginP.remove(qrCodeLoginLink);
                            loginP.setLayout(new GridLayout(7, 1, 30, 15));
                            loginP.add(lLabelLink);
                            loginP.add(forgotPasswordLink);
                            loginP.add(qrCodeLoginLink);

                            signupP.remove(sErrorLabel);
                            signupP.remove(sLabelLink);
                            signupP.setLayout(new GridLayout(6, 1, 0, 22));
                            signupP.add(sLabelLink);

                            lErrorLabel.setVisible(false);
                            sErrorLabel.setVisible(false);
                            loginP.revalidate();
                            signupP.revalidate();
                            loginP.repaint();
                            signupP.repaint();
                        });
                    }).start();
                    remove(contentPanel);
                    contentPanel = loginP;
                    add(contentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    errorSound();
                    JOptionPane.showMessageDialog(contentPanel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                sLabelLink.setForeground(Color.WHITE);
                sLabelLink.setIcon(scaledLoginWhiteIcon);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                sLabelLink.setForeground(accentColor);
                sLabelLink.setIcon(scaledLoginBlueIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sLabelLink.setForeground(WHITE);
                sLabelLink.setIcon(scaledLoginWhiteIcon);
            }
        });

        signupP.add(sLabel);
        signupP.add(sUser);
        signupP.add(sEmail);
        signupP.add(sPass);
        signupP.add(sButton);
        signupP.add(sLabelLink);

        // ----- LOGIN PART -----
        loginP = new JPanel(new GridLayout(7, 1, 30, 15));
        loginP.setSize(300, 500);
        loginP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lLabel = new JLabel("Log In", JLabel.CENTER);
        lLabel.setIcon(logo);
        lLabel.setIconTextGap(10);
        lLabel.setFont(new Font(FlatInterFont.FAMILY, Font.BOLD, 20));
        lLabel.setForeground(UIManager.getColor("Label.foreground"));

        lUser = new JTextField(20);
        lUser.addKeyListener(this);
        lUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username or Email");
        lUser.setToolTipText("Username or Email");
        lUser.setCaretColor(accentColor);

        lPass = new JPasswordField();
        lPass.addKeyListener(this);
        lPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        lPass.setToolTipText("Password");
        lPass.setCaretColor(accentColor);

        textFieldDesigner(lUser);
        passwordFieldDesigner(lPass);

        lButton = new JButton("Log In");
        lButton.setForeground(accentColor);
        lButton.setBackground(backgroundColor);
        buttonDesigner(lButton);
        lButton.setFocusable(false);
        lButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                lButton.setBackground(accentColor);
                lButton.setForeground(WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lButton.setBackground(backgroundColor);
                lButton.setForeground(accentColor);
            }
        });
        lButton.addActionListener(e -> new Thread(this::handleLogin).start());

        lErrorLabel = new JLabel();
        lErrorLabel.setText("");
        if (errorPath != null) {
            ImageIcon errorIcon = new ImageIcon(errorPath);
            lErrorLabel.setIcon(errorIcon);
        } else {
            errorSound();
            System.err.println("Error: Resource not found: /icons/error.png");
        }
        lErrorLabel.setOpaque(true);
        lErrorLabel.setForeground(new Color(255, 255, 255));
        lErrorLabel.setBackground(errorColor);
        lErrorLabel.setBorder(new FlatLineBorder(new Insets(5, 10, 5, 10), RED, 1f, 12));
        lErrorLabel.setVisible(false);

        URL signupWhiteURL = getClass().getResource("/icons/signup_white.png");
        URL signupBlueURL = getClass().getResource("/icons/signup_blue.png");
        ImageIcon signupWhite = new ImageIcon(signupWhiteURL);
        ImageIcon signupBlue = new ImageIcon(signupBlueURL);
        Image scaledSignupWhite = signupWhite.getImage().getScaledInstance(21, 21, Image.SCALE_SMOOTH);
        Image scaledSignupBlue = signupBlue.getImage().getScaledInstance(21, 21, Image.SCALE_SMOOTH);
        ImageIcon scaledSignupWhiteIcon = new ImageIcon(scaledSignupWhite);
        ImageIcon scaledSignupBlueIcon = new ImageIcon(scaledSignupBlue);

        lLabelLink = new JLabel("Don't have an account? Sign up here!");
        lLabelLink.setIcon(scaledSignupWhiteIcon);
        lLabelLink.setIconTextGap(10);
        lLabelLink.setHorizontalTextPosition(SwingConstants.LEFT);
        lLabelLink.setForeground(WHITE);
        lLabelLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lLabelLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setTitle("Connect - Sign Up");
                    loginSound();
                    sUser.setText("");
                    sPass.setText("");
                    sEmail.setText("");
                    lUser.setText("");
                    lPass.setText("");
                    new Thread(() -> {
                        SwingUtilities.invokeLater(() -> {
                            // UI update logic here (same as inside your Timer)
                            loginP.remove(lErrorLabel);
                            loginP.remove(lLabelLink);
                            loginP.remove(forgotPasswordLink);
                            loginP.remove(qrCodeLoginLink);
                            loginP.setLayout(new GridLayout(7, 1, 30, 15));
                            loginP.add(lLabelLink);
                            loginP.add(forgotPasswordLink);
                            loginP.add(qrCodeLoginLink);

                            signupP.remove(sErrorLabel);
                            signupP.remove(sLabelLink);
                            signupP.setLayout(new GridLayout(6, 1, 0, 22));
                            signupP.add(sLabelLink);

                            lErrorLabel.setVisible(false);
                            sErrorLabel.setVisible(false);
                            loginP.revalidate();
                            signupP.revalidate();
                            loginP.repaint();
                            signupP.repaint();
                        });
                    }).start();
                    remove(contentPanel);
                    contentPanel = signupP;
                    add(contentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    errorSound();
                    JOptionPane.showMessageDialog(contentPanel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                lLabelLink.setForeground(Color.WHITE);
                lLabelLink.setIcon(scaledSignupWhiteIcon);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lLabelLink.setForeground(accentColor);
                lLabelLink.setIcon(scaledSignupBlueIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lLabelLink.setForeground(WHITE);
                lLabelLink.setIcon(scaledSignupWhiteIcon);
            }
        });

        URL forgotWhiteURL = getClass().getResource("/icons/forgot_white.png");
        URL forgotBlueURL = getClass().getResource("/icons/forgot_blue.png");
        ImageIcon forgotWhite = new ImageIcon(forgotWhiteURL);
        ImageIcon forgotBlue = new ImageIcon(forgotBlueURL);
        Image scaledForgotWhite = forgotWhite.getImage().getScaledInstance(21, 21, Image.SCALE_SMOOTH);
        Image scaledForgotBlue = forgotBlue.getImage().getScaledInstance(21, 21, Image.SCALE_SMOOTH);
        ImageIcon scaledForgotWhiteIcon = new ImageIcon(scaledForgotWhite);
        ImageIcon scaledForgotBlueIcon = new ImageIcon(scaledForgotBlue);

        forgotPasswordLink = new JLabel("Forgot Password?");
        forgotPasswordLink.setIcon(scaledForgotWhiteIcon);
        forgotPasswordLink.setIconTextGap(10);
        forgotPasswordLink.setHorizontalTextPosition(SwingConstants.LEFT);
        forgotPasswordLink.setForeground(WHITE);
        forgotPasswordLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ForgotPassword forgotPass = new ForgotPassword(DB_URL, DB_USER, DB_PASSWORD);
                forgotPass.forgotFunc();
                forgotPasswordLink.setForeground(WHITE);
                forgotPasswordLink.setIcon(scaledForgotWhiteIcon);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLink.setForeground(accentColor);
                forgotPasswordLink.setIcon(scaledForgotBlueIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLink.setForeground(WHITE);
                forgotPasswordLink.setIcon(scaledForgotWhiteIcon);
            }
        });

        URL qrWhiteURL = getClass().getResource("/icons/QrWhite.png");
        URL qrBlueURL = getClass().getResource("/icons/QrBlue.png");
        ImageIcon qrWhite = new ImageIcon(qrWhiteURL);
        ImageIcon qrBlue = new ImageIcon(qrBlueURL);
        Image scaledWhite = qrWhite.getImage().getScaledInstance(21, 21, Image.SCALE_SMOOTH);
        Image scaledBlue = qrBlue.getImage().getScaledInstance(21, 21, Image.SCALE_SMOOTH);
        ImageIcon scaledQrWhite = new ImageIcon(scaledWhite);
        ImageIcon scaledQrBlue = new ImageIcon(scaledBlue);

        qrCodeLoginLink = new JLabel("Log in using QR Code");
        qrCodeLoginLink.setIcon(scaledQrWhite);
        qrCodeLoginLink.setHorizontalTextPosition(SwingConstants.LEFT);
        qrCodeLoginLink.setIconTextGap(10);
        qrCodeLoginLink.setForeground(WHITE);
        qrCodeLoginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        qrCodeLoginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new QRCodeLogin(Login.this);
                loginSound();
                qrCodeLoginLink.setForeground(WHITE);
                qrCodeLoginLink.setIcon(scaledQrWhite);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                qrCodeLoginLink.setForeground(accentColor);
                qrCodeLoginLink.setIcon(scaledQrBlue);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                qrCodeLoginLink.setForeground(WHITE);
                qrCodeLoginLink.setIcon(scaledQrWhite);
            }
        });

        loginP.add(lLabel);
        loginP.add(lUser);
        loginP.add(lPass);
        loginP.add(lButton);
        loginP.add(lLabelLink);
        loginP.add(forgotPasswordLink);
        loginP.add(qrCodeLoginLink);

        //sets the icon for the JFrame
        URL iconURL = getClass().getResource("/icons/Favicon.png");
        ImageIcon icon;
        if (iconURL != null) {
            icon = new ImageIcon(iconURL);
            setIconImage(icon.getImage());
        } else {
            errorSound();
            JOptionPane.showMessageDialog(null, "Icon not found", "Error", JOptionPane.ERROR_MESSAGE);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        addKeyListener(this);
        setLayout(new BorderLayout());
        contentPanel = loginP;
        add(contentPanel, BorderLayout.CENTER);
        setResizable(false);
        setSize(352, 612);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //user registration and login handling
    private void handleSignUp() {
        //Get inputs from fields
        String userF = sUser.getText();
        String passF = new String(sPass.getPassword());
        String emailF = sEmail.getText();

        //Checks empty field
        if (userF.isEmpty() || passF.isEmpty() || emailF.isEmpty()) {
            sErrorLabel.setText("<html><div style='width:156px'>Please enter credentials, Text field cannot be empty.</div></html>");
            errorSound();
            handleErrorUI();
            return;
        }

        //Username validation
        try (Connection connection = Database.getConnection()) {
            String selectQuery = "SELECT * FROM Credentials WHERE BINARY Users = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setString(1, userF);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    sErrorLabel.setText("<html><div style='width:156px'>Username already exists.</div></html>");
                    errorSound();
                    handleErrorUI();
                    sPass.setText("");
                    return;
                } else if (userF.length() < 4) {
                    sErrorLabel.setText("<html><div style='width:156px'>Username length cannot be less than 4 characters.</div></html>");
                    errorSound();
                    handleErrorUI();
                    sPass.setText("");
                    return;
                } else if (userF.length() > 20) {
                    sErrorLabel.setText("<html><div style='width:156px'>Username length cannot exceed 20 characters.</div></html>");
                    errorSound();
                    handleErrorUI();
                    sPass.setText("");
                    return;
                }
            }
        } catch (SQLException ex) {
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Username error: " + ex.getMessage() , "Username Error", JOptionPane.WARNING_MESSAGE);
            sPass.setText("");
        }

        //Email validation
        try (Connection connection = Database.getConnection()) {
            String selectQuery = "SELECT * FROM Credentials WHERE BINARY Emails = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setString(1, emailF);
                ResultSet resultSet = statement.executeQuery();

                if (!emailF.endsWith("@gmail.com")) {
                    sErrorLabel.setText("<html><div style='width:156px'>For now, We only accept Gmail. Sorry for the inconvenience.</div></html>");
                    errorSound();
                    handleErrorUI();
                    sPass.setText("");
                } else if (resultSet.next()) {
                    sErrorLabel.setText("<html><div style='width:156px'>Email already in use.</div></html>");
                    errorSound();
                    handleErrorUI();
                    sPass.setText("");
                    return;
                }
            }
        } catch (SQLException ex) {
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Email Error: " + ex.getMessage(), "Email Error", JOptionPane.WARNING_MESSAGE);
        }

        //Password validation
        String passwordToCheck = sPass.getText();

        if (!passwordToCheck.matches(COMPLEX_PASSWORD_PATTERN)) {
            sErrorLabel.setText("<html><div style='width:156px'>Password must contain at least one uppercase letter, one lowercase letter, one numeric digit, and one symbol.</div></html>");
            errorSound();
            handleErrorUI();
            sPass.setText("");
            return;
        } else if (sPass.getText().length() < 8) {
            sErrorLabel.setText("<html><div style='width:156px'>Password length cannot be less than 8 characters.</div></html>");
            errorSound();
            handleErrorUI();
            sPass.setText("");
            return;
        } else if (sPass.getText().length() > 20 ) {
            sErrorLabel.setText("<html><div style='width:156px'>Password length cannot exceed 20 characters.</div></html>");
            errorSound();
            handleErrorUI();
            sPass.setText("");
            return;
        }

        //User registration into database
        try (Connection connection = Database.getConnection()) {
            String insertQuery = "INSERT INTO Credentials (Users, Passwords, Emails, Roles) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setString(1, userF);
                statement.setString(2, BCrypt.hashpw(passF, BCrypt.gensalt(14)));  //Hash the pass before storing into database
                statement.setString(3, emailF);
                statement.setString(4, "User");
                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    loginSound();
                    JOptionPane.showMessageDialog(contentPanel, "Signup successful!", "Registration successful", JOptionPane.INFORMATION_MESSAGE);
                    sErrorLabel.setVisible(false);
                    setTitle(" Connect - Log In");
                    remove(contentPanel);
                    contentPanel = loginP;
                    add(contentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } else {
                    sErrorLabel.setText("<html><div style='width:156px'>Signup failed.</div></html>");
                    errorSound();
                    handleErrorUI();
                    sPass.setText("");
                }
            }
        } catch (SQLException ex) {
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    private void handleLogin() {
        lButton.setEnabled(false);
        lButton.setText("Please wait...");

        //Checks if the user is locked out
        if (System.currentTimeMillis() < lockTime) {
            long remainingTime = (lockTime - System.currentTimeMillis()) / 1000;
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Too many failed attempts. Please wait " + remainingTime + " seconds.", "Login Locked", JOptionPane.WARNING_MESSAGE);
            lPass.setText("");
            return;
        }

        //Get inputs from fields
        String userF = lUser.getText();
        String passF = new String(lPass.getPassword());

        //Checks empty field
        if (userF.isEmpty() || passF.isEmpty()) {
            lErrorLabel.setText("<html><div style='width:156px'>Please enter credentials, Text field cannot be empty.</div></html>");
            errorSound();
            handleErrorUI();
            handleFailedAttempt();
            return;
        }

        boolean isEmail = userF.endsWith("@gmail.com");

        //Checks existing user with correct password
        try (Connection connection = Database.getConnection()) {
            String selectQuery = isEmail ? "SELECT * FROM Credentials WHERE BINARY Emails = ?" : "SELECT * FROM Credentials WHERE BINARY Users = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setString(1, userF);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Verify the input password and recorded hash
                    if (BCrypt.checkpw(passF, resultSet.getString("Passwords"))) {
                        loginSound();
                        JOptionPane.showMessageDialog(contentPanel, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        //Reset attempts
                        loginAttempt = 0;

                        //After successful login
                        offlineSound();
                        dispose();



                        String role = resultSet.getString("Roles");
                        if (resultSet.getString("Roles").equalsIgnoreCase("Administrator")) {
                            //Admin Control - bahala na si Justine
                            loginSound();
                            try {
                                // Try to connect to the chat server with this username
                                ChatClient chatClient = new ChatClient("localhost", 12345, userF);
                                // Pass the chat client into your ChatWindow (update ChatWindow constructor if needed)
                                new MainPanel(userF, role);
                                new ChatWindow(userF, chatClient);
                            } catch (IOException e) {
                                // Server rejected the login OR could not connect
                                JOptionPane.showMessageDialog(contentPanel, "Could not connect to Chat Server:\n" + e.getMessage(), "Chat Error", JOptionPane.ERROR_MESSAGE);
                                // Optionally: Return or skip opening ChatWindow
                                return;
                            }

                        } else if (resultSet.getString("Roles").equalsIgnoreCase("User")) {
                            loginSound();
                            try {
                                // Try to connect to the chat server with this username
                                ChatClient chatClient = new ChatClient("localhost", 12345, userF);
                                // Pass the chat client into your ChatWindow (update ChatWindow constructor if needed)
                                new MainPanel(userF, role);
                                new ChatWindow(userF, chatClient);
                            } catch (IOException e) {
                                // Server rejected the login OR could not connect
                                JOptionPane.showMessageDialog(contentPanel, "Could not connect to Chat Server:\n" + e.getMessage(), "Chat Error", JOptionPane.ERROR_MESSAGE);
                                // Optionally: Return or skip opening ChatWindow
                                return;
                            }
                        } else {
                            try {
                                // Try to connect to the chat server with this username
                                ChatClient chatClient = new ChatClient("localhost", 12345, userF);
                                // Pass the chat client into your ChatWindow (update ChatWindow constructor if needed)
                                new MainPanel(userF, role);
                                new ChatWindow(userF, chatClient);
                            } catch (IOException e) {
                                // Server rejected the login OR could not connect
                                JOptionPane.showMessageDialog(contentPanel, "Could not connect to Chat Server:\n" + e.getMessage(), "Chat Error", JOptionPane.ERROR_MESSAGE);
                                // Optionally: Return or skip opening ChatWindow
                                return;
                            }
                        }

                    } else {
                        lErrorLabel.setText("<html><div style='width:156px'>Password incorrect, try again.</div></html>");
                        errorSound();
                        handleErrorUI();
                        lPass.setText("");
                        handleFailedAttempt();
                    }
                } else {
                    lErrorLabel.setText("<html><div style='width:156px'>Username doesn't exist.</div></html>");
                    errorSound();
                    handleErrorUI();
                    lPass.setText("");
                    handleFailedAttempt();
                }
            }
        } catch (SQLException ex) {
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        lButton.setEnabled(true);
        lButton.setText("Log In");
    }

    //Text Field Designer
    private void textFieldDesigner(JTextField textField) {
        textField.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth: 2; borderColor: #1877F2;");

        textField.setFont(new Font(FlatInterFont.FAMILY, Font.PLAIN, 14));
        textField.setBackground(backgroundColor);
    }

    //Password Field Designer
    private void passwordFieldDesigner(JPasswordField passwordField) {
        passwordField.putClientProperty(FlatClientProperties.STYLE,
                "showRevealButton: true; showCapsLock: true; borderWidth: 2; borderColor: #1877F2;");

        passwordField.setFont(new Font(FlatInterFont.FAMILY, Font.PLAIN, 14));
        passwordField.setBackground(backgroundColor);
    }

    //Button Designer
    private void buttonDesigner(JButton button) {
        button.putClientProperty(FlatClientProperties.STYLE, "borderWidth: 2; borderColor: #1877F2;");

        button.setFont(new Font(FlatInterFont.FAMILY, Font.BOLD, 13));
        button.setBackground(backgroundColor);
    }

    //Handle failed login attempts
    private void handleFailedAttempt() {
        loginAttempt++;
        if (loginAttempt >= 3) {
            lockTime = System.currentTimeMillis() + (2 * 60 * 1000); // 2 minutes lockout
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Too many failed attempts. Please wait 2 minutes.", "Login Locked", JOptionPane.WARNING_MESSAGE);
        }
    }

    //Handles the error UI
    private void handleErrorUI() {
        loginP.remove(lLabelLink);
        loginP.remove(forgotPasswordLink);
        loginP.remove(qrCodeLoginLink);
        loginP.setLayout(new GridLayout(8, 1, 30, 15));
        loginP.add(lErrorLabel);
        loginP.add(lLabelLink);
        loginP.add(forgotPasswordLink);
        loginP.add(qrCodeLoginLink);
        lErrorLabel.setVisible(true);

        signupP.remove(sLabelLink);
        signupP.setLayout(new GridLayout(7, 1, 0, 22));
        signupP.add(sErrorLabel);
        signupP.add(sLabelLink);
        sErrorLabel.setVisible(true);

        Timer timer = new Timer(5000, e -> {
            loginP.remove(lErrorLabel);
            loginP.remove(lLabelLink);
            loginP.remove(forgotPasswordLink);
            loginP.remove(qrCodeLoginLink);
            loginP.setLayout(new GridLayout(7, 1, 30, 15));
            loginP.add(lLabelLink);
            loginP.add(forgotPasswordLink);
            loginP.add(qrCodeLoginLink);

            signupP.remove(sErrorLabel);
            signupP.remove(sLabelLink);
            signupP.setLayout(new GridLayout(6, 1, 0, 22));
            signupP.add(sLabelLink);

            lErrorLabel.setVisible(false);
            sErrorLabel.setVisible(false);
            loginP.revalidate();
            signupP.revalidate();
            loginP.repaint();
            signupP.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    //Main method
    public static void main(String[] args) {
        FlatInterFont.install();
        FlatLaf.registerCustomDefaultsSource("uca.themes");
        UIManager.put("defaultFont", new Font(FlatInterFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        new Login();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //Null function
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Null function
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 10) {
            if(loginP.isShowing()) {
                lButton.doClick();
            }if(signupP.isShowing()) {
                sButton.doClick();
            }
        }
    }
}

// -- DUMP AFTER CONNECTING ROMMARDS CODE --
//Call after successful login
class MainPanel extends JFrame{
    public MainPanel(String username, String role) {
        setTitle("Main Panel : @" + username);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("uca.themes");
        UIManager.put("defaultFont", new Font(FlatInterFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();

        JLabel usernameLabel = new JLabel("@" + username, JLabel.CENTER);
        usernameLabel.setFont(new Font(FlatInterFont.FAMILY, Font.BOLD, 18));
        usernameLabel.setForeground(WHITE);

        JLabel welcomeLabel = new JLabel("Welcome to the Main Panel", JLabel.CENTER);
        welcomeLabel.setFont(new Font(FlatInterFont.FAMILY, Font.BOLD, 20));
        welcomeLabel.setForeground(WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusable(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setBorder(BorderFactory.createLineBorder(ORANGE, 2));

        JButton adminButton = new JButton("Admin Controls");
        logoutButton.setFocusable(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setBorder(BorderFactory.createLineBorder(ORANGE, 2));

        logoutButton.setBackground(WHITE);
        logoutButton.setForeground(ORANGE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setPreferredSize(new Dimension(50, 50));
        logoutButton.setFocusable(false);
        logoutButton.addActionListener(e -> {
            offlineSound();
            JOptionPane.showMessageDialog(this, "You are logged out!", "Logout", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new Login();
        });
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(ORANGE);
                logoutButton.setForeground(WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(WHITE);
                logoutButton.setForeground(ORANGE);
            }
        });
        adminButton.addActionListener( e -> new AdminDashboard());

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(DGRAY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(usernameLabel, BorderLayout.NORTH);
        if (role.equals("Administrator")){
            contentPanel.add(adminButton, BorderLayout.CENTER);
        }else{
            contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        }
        contentPanel.add(logoutButton, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}