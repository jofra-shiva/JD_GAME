/* LevelDevilAllInOne.java
 *
 * Single-file merged project: Login/Signup (MySQL) + Game + Quiz + World Objects
 *
 * Requirements:
 *  - MySQL (or compatible) DB
 *  - players table (see SQL above)
 *  - MySQL JDBC driver JAR on classpath
 *
 * How to compile:
 *  javac -cp .:mysql-connector-java-8.0.XXX.jar LevelDevilAllInOne.java
 * How to run:
 *  java  -cp .:mysql-connector-java-8.0.XXX.jar LevelDevilAllInOne
 *
 * (Replace : with ; on Windows)
 */

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.util.*;
import java.util.List;

/* ------------------------- ENTRY POINT ------------------------- */
public class LevelDevilAllInOne {
    public static void main(String[] args) {
        // optionally set look and feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}

/* ------------------------- DB CONNECTION ------------------------- */
class DBConnection {
    // Edit these to your DB settings
    private static final String URL = "jdbc:mysql://localhost:3306/leveldevil?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "1525";

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

/* ------------------------- LOGIN WINDOW (UI) ------------------------- */
class LoginWindow extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JComboBox<String> genderBox;
    JButton loginBtn, signupBtn;

    LoginWindow() {
        setTitle("Level Devil - Login");
        setSize(380, 280);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel p = new JPanel(null);
        p.setBackground(new Color(25, 25, 30));
        add(p);

        JLabel title = new JLabel("LEVEL DEVIL");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBounds(120, 6, 200, 30);
        p.add(title);

        JLabel l1 = new JLabel("Username:");
        JLabel l2 = new JLabel("Password:");
        JLabel l3 = new JLabel("Gender:");

        l1.setForeground(Color.WHITE);
        l2.setForeground(Color.WHITE);
        l3.setForeground(Color.WHITE);

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        loginBtn = new JButton("Login");
        signupBtn = new JButton("Sign Up");

        l1.setBounds(40, 50, 100, 25);
        usernameField.setBounds(140, 50, 180, 25);
        l2.setBounds(40, 90, 100, 25);
        passwordField.setBounds(140, 90, 180, 25);
        l3.setBounds(40, 130, 100, 25);
        genderBox.setBounds(140, 130, 180, 25);
        loginBtn.setBounds(60, 180, 120, 30);
        signupBtn.setBounds(200, 180, 120, 30);

        loginBtn.setBackground(new Color(60, 130, 255));
        loginBtn.setForeground(Color.WHITE);
        signupBtn.setBackground(new Color(80, 200, 120));
        signupBtn.setForeground(Color.WHITE);

        loginBtn.setFocusPainted(false);
        signupBtn.setFocusPainted(false);

        p.add(l1); p.add(usernameField);
        p.add(l2); p.add(passwordField);
        p.add(l3); p.add(genderBox);
        p.add(loginBtn); p.add(signupBtn);

        loginBtn.addActionListener(e -> login());
        signupBtn.addActionListener(e -> signup());
    }

    private void login() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM players WHERE username=? AND password=?");
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int level = rs.getInt("level");
                int deaths = rs.getInt("deaths");
                String gender = rs.getString("gender");
                // Launch game with saved state
                SwingUtilities.invokeLater(() -> {
                    new LevelDevilJavaQuizPlus(user, gender == null ? "Other" : gender, Math.max(0, level), Math.max(0, deaths));
                });
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void signup() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String gender = (String) genderBox.getSelectedItem();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO players(username,password,gender,level,deaths) VALUES (?,?,?,?,?)");
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, gender);
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sign up successful! Please log in.");
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Username already exists!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}

/* ------------------------- MAIN GAME WINDOW ------------------------- */
class LevelDevilJavaQuizPlus extends JFrame {
    public LevelDevilJavaQuizPlus(String name, String gender, int level, int deaths) {
        setTitle("Level Devil: Java Quiz+ (Pixel Edition)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel panel = new GamePanel(1366, 768, name, gender, level, deaths);
        setContentPane(panel);

        // Fullscreen-ish: We'll size to given logical resolution then maximize window decoration-less
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        panel.start();
    }
}

/* ------------------------- GAME PANEL ------------------------- */
class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final int W, H;

    // Physics
    private final double GRAVITY = 0.78;
    private final double MOVE = 3.4;
    private final double JUMP = -17.8;
    private final double MAX_FALL = 19;

    // Loop
    private final Timer timer = new Timer(16, this);

    // Player (physics hitbox)
    Rectangle player = new Rectangle(70, 0, 28, 36);
    private double vx = 0, vy = 0;
    private boolean onGround = false;
    private boolean left, right;
    private Point spawn;

    // Pixel render/animation
    private int animTick = 0;
    private int runFrame = 0; // 0..5
    private boolean facingRight = true;

    // World
    private final List<Rectangle> platforms = new ArrayList<>();
    private final List<CollapsingTile> collapsers = new ArrayList<>();
    private final List<FragileTile> fragiles = new ArrayList<>();
    private final List<HiddenSpike> spikes = new ArrayList<>();
    private final List<MovingSaw> saws = new ArrayList<>();
    private final List<TeleportTile> teleports = new ArrayList<>();
    private final List<SecretPlatform> secrets = new ArrayList<>();
    private final List<ExitDoor> doors = new ArrayList<>();
    private ExitDoor realDoor;

    // meta
    private String playerGender = "Other";
    private String playerName = "Player";
    private int levelIndex = 0;
    private int deaths = 0;
    private boolean inDialog = false;
    private boolean gameFinished = false;

    // Hint arrow (after 3 deaths)
    private int arrowOffsetX = 0;
    private int arrowDirX = 1;

    // Quiz
    private final QuestionManager qManager = new QuestionManager();
    private final Map<Integer, Question> lastWrongPerLevel = new HashMap<>();
    private final Set<Integer> usedQuestionIds = new HashSet<>();

    GamePanel(int width, int height, String name, String gender, int savedLevel, int savedDeaths) {
        this.W = width; this.H = height;
        this.playerName = name == null || name.trim().isEmpty() ? "Player" : name;
        this.playerGender = gender == null || gender.trim().isEmpty() ? "Other" : gender;
        this.levelIndex = Math.max(0, Math.min(14, savedLevel));
        this.deaths = Math.max(0, savedDeaths);

        setPreferredSize(new Dimension(W, H));
        setBackground(new Color(18, 20, 24));
        setFocusable(true);
        addKeyListener(this);

        // Show short greeting
        JOptionPane.showMessageDialog(this,
                "Welcome " + titleFor(playerGender) + " " + playerName + "!\n" +
                        "Reach the REAL door each level.\nWrong answer repeats on the same level.\n\n" +
                        "Controls:\nMove: A/D or ←/→   Jump: Space   Reset: R   Exit: ESC",
                "Ready", JOptionPane.INFORMATION_MESSAGE);

        buildLevel(levelIndex);
    }

    void start() { timer.start(); }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inDialog || gameFinished) { repaint(); return; }

        // Input
        vx = 0;
        if (left) vx -= MOVE;
        if (right) vx += MOVE;
        if (vx > 0) facingRight = true;
        else if (vx < 0) facingRight = false;

        // Gravity
        vy += GRAVITY;
        if (vy > MAX_FALL) vy = MAX_FALL;

        // Horizontal step
        player.x += (int) Math.round(vx);
        collideHorizontal();

        // Vertical step
        onGround = false;
        player.y += (int) Math.round(vy);
        collideVertical();

        // Objects update
        for (CollapsingTile c : collapsers) c.update(player);
        for (FragileTile f : fragiles) if (f.update(player)) die();
        for (HiddenSpike s : spikes) if (s.update(player)) { die(); break; }
        for (MovingSaw saw : saws) if (saw.update(player)) { die(); break; }
        for (TeleportTile t : teleports) t.update(player);
        for (SecretPlatform s : secrets) s.update(player);

        // Doors
        for (ExitDoor d : doors) {
            if (player.intersects(d.bounds)) {
                if (d.fake) {
                    d.shake();
                    JOptionPane.showMessageDialog(this,
                            "Fake door da " + pronounFor(playerGender) + " " + playerName + "! Nice try—respawn.");
                    die();
                } else {
                    askQuestionForLevel();
                }
                break;
            }
        }

        // Void
        if (player.y > H + 300) die();

        // Arrow animation (after 3 deaths)
        arrowOffsetX += arrowDirX;
        if (arrowOffsetX > 24 || arrowOffsetX < -24) arrowDirX *= -1;

        // Run animation
        animTick++;
        if (animTick % 6 == 0) runFrame = (runFrame + 1) % 6;

        repaint();
    }

    /* ---------- Collision helpers ---------- */
    private void collideHorizontal() {
        for (Rectangle r : solids()) {
            if (player.intersects(r)) {
                if (vx > 0) player.x = r.x - player.width;
                else if (vx < 0) player.x = r.x + r.width;
            }
        }
    }

    private void collideVertical() {
        for (Rectangle r : solids()) {
            if (player.intersects(r)) {
                if (vy > 0) {
                    player.y = r.y - player.height;
                    vy = 0;
                    onGround = true;
                    for (CollapsingTile c : collapsers) c.onLand(player);
                } else if (vy < 0) {
                    player.y = r.y + r.height;
                    vy = 0;
                }
            }
        }
    }

    private List<Rectangle> solids() {
        List<Rectangle> all = new ArrayList<>(platforms);
        for (CollapsingTile c : collapsers) if (c.solid) all.add(c.bounds);
        for (FragileTile f : fragiles) if (f.solid) all.add(f.bounds);
        for (TeleportTile t : teleports) if (t.solid) all.add(t.bounds);
        for (SecretPlatform s : secrets) if (s.solid) all.add(s.bounds);
        return all;
    }

    /* ---------- Death & next level ---------- */
    private void die() {
        deaths++;

        left = false;
        right = false;

        // D4 mixed commentary
        String[] calm = {
                "Steady... Try again, " + titleFor(playerGender) + " " + playerName + ".",
                "No rush " + pronounFor(playerGender) + ". Reset and go smooth."
        };
        String[] troll = {
                "Ayyo " + pronounFor(playerGender) + " " + playerName + " 😆 Spike-ku hi sollita!",
                "Fake door pathu poga pona, nice respect 🤝"
        };
        String[] motivate = {
                "Good attempt " + pronounFor(playerGender) + "! Next one better 🔥",
                "Focus. Ithu un level, " + titleFor(playerGender) + " " + playerName + " 💪"
        };
        String msg;
        int pick = new Random().nextInt(3);
        if (pick == 0) msg = calm[new Random().nextInt(calm.length)];
        else if (pick == 1) msg = troll[new Random().nextInt(troll.length)];
        else msg = motivate[new Random().nextInt(motivate.length)];
        JOptionPane.showMessageDialog(this, msg, "Respawn", JOptionPane.INFORMATION_MESSAGE);

        // Reset world
        for (CollapsingTile c : collapsers) c.reset();
        for (FragileTile f : fragiles) f.reset();
        for (HiddenSpike s : spikes) s.reset();
        for (MovingSaw saw : saws) saw.reset();
        for (TeleportTile t : teleports) t.reset();
        for (SecretPlatform s : secrets) s.reset();
        player.x = spawn.x;
        player.y = spawn.y;
        vx = vy = 0;
        onGround = false;
        left = false;
        right = false;

        // Save deaths to DB
        saveProgressToDB();
    }

    private void nextLevel() {
        levelIndex++;
        if (levelIndex >= 15) {
            gameFinished = true;
            repaint();
            JOptionPane.showMessageDialog(this,
                    "🏁 GG " + titleFor(playerGender) + " " + playerName + "! 15/15 cleared. Deaths: " + deaths);
            saveProgressToDB();
            return;
        }
        buildLevel(levelIndex);
        saveProgressToDB();
    }

    private void saveProgressToDB() {
        // Update players table: level, deaths
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE players SET level=?, deaths=? WHERE username=?");
            ps.setInt(1, levelIndex);
            ps.setInt(2, deaths);
            ps.setString(3, playerName);
            ps.executeUpdate();
        } catch (SQLException ex) {
            // swallow but print for debugging
            ex.printStackTrace();
        }
    }

    /* ---------- Levels ---------- */
    private void buildLevel(int idx) {
        platforms.clear();
        collapsers.clear();
        fragiles.clear();
        spikes.clear();
        saws.clear();
        doors.clear();
        teleports.clear();
        secrets.clear();

        int G = H - 60;
        platforms.add(new Rectangle(0, G, W, 60));

        switch (idx) {
            case 0:
                spawn = new Point(70, G - 80);
                platforms.add(new Rectangle(0, G - 140, 260, 16));
                platforms.add(new Rectangle(320, G - 170, 200, 16));
                platforms.add(new Rectangle(600, G - 160, 160, 16));
                platforms.add(new Rectangle(820, G - 180, 170, 16));
                collapsers.add(new CollapsingTile(380, G - 170 - 16, 44, 16, 520));
                spikes.add(new HiddenSpike(600, G - 160 - 18, 160, 18, 800));
                realDoor = new ExitDoor(930, G - 180 - 48, 28, 48, false);
                doors.add(realDoor);
                secrets.add(new SecretPlatform(260, G - 140 - 14, 90, 14));
                break;
            case 1:
                spawn = new Point(70, G - 80);
                platforms.add(new Rectangle(200, G - 180, 160, 14));
                platforms.add(new Rectangle(420, G - 230, 140, 14));
                platforms.add(new Rectangle(640, G - 270, 120, 14));
                platforms.add(new Rectangle(820, G - 250, 180, 14));
                collapsers.add(new CollapsingTile(240, G - 180 - 16, 40, 16, 360));
                collapsers.add(new CollapsingTile(680, G - 270 - 16, 40, 16, 400));
                doors.add(new ExitDoor(860, G - 250 - 48, 28, 48, true));
                realDoor = new ExitDoor(900, G - 250 - 48, 28, 48, false);
                doors.add(realDoor);
                secrets.add(new SecretPlatform(500, G - 230 - 14, 90, 14));
                break;
            case 2:
                spawn = new Point(70, G - 80);
                platforms.add(new Rectangle(160, G - 150, 150, 16));
                platforms.add(new Rectangle(340, G - 170, 150, 16));
                platforms.add(new Rectangle(520, G - 160, 150, 16));
                platforms.add(new Rectangle(720, G - 150, 180, 16));
                fragiles.add(new FragileTile(520, G - 160 - 16, 50, 16));
                spikes.add(new HiddenSpike(720, G - 150 - 18, 180, 18, 1100));
                realDoor = new ExitDoor(880, G - 150 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 3:
                spawn = new Point(70, G - 80);
                platforms.add(new Rectangle(220, G - 140, 180, 16));
                platforms.add(new Rectangle(460, G - 160, 180, 16));
                platforms.add(new Rectangle(540, G - 160, 180, 16));
                saws.add(new MovingSaw(230, G - 140 - 22, 200, 2.8));
                saws.add(new MovingSaw(740, G - 160 - 22, 220, 3.0));
                realDoor = new ExitDoor(900, G - 160 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 4:
                spawn = new Point(70, G - 80);
                platforms.add(new Rectangle(200, G - 160, 140, 16));
                platforms.add(new Rectangle(420, G - 200, 140, 16));
                platforms.add(new Rectangle(640, G - 230, 140, 16));
                platforms.add(new Rectangle(860, G - 260, 160, 16));
                teleports.add(new TeleportTile(420, G - 200 - 16, 40, 16, new Point(860, G - 260 - 60)));
                collapsers.add(new CollapsingTile(640, G - 230 - 16, 40, 16, 300));
                realDoor = new ExitDoor(940, G - 260 - 48, 28, 48, false);
                doors.add(realDoor);
                secrets.add(new SecretPlatform(585, G - 230 - 14, 60, 14));
                break;
            case 5:
                spawn = new Point(70, G - 80);
                platforms.add(new Rectangle(200, G - 160, 140, 14));
                platforms.add(new Rectangle(360, G - 185, 140, 14));
                platforms.add(new Rectangle(520, G - 170, 140, 14));
                platforms.add(new Rectangle(680, G - 190, 140, 14));
                platforms.add(new Rectangle(860, G - 175, 170, 14));
                collapsers.add(new CollapsingTile(200, G - 160 - 16, 40, 16, 300));
                collapsers.add(new CollapsingTile(360, G - 185 - 16, 40, 16, 300));
                collapsers.add(new CollapsingTile(520, G - 170 - 16, 40, 16, 300));
                saws.add(new MovingSaw(300, G - 60, 500, 2.6));
                realDoor = new ExitDoor(950, G - 175 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 6:
                spawn = new Point(60, G - 80);
                platforms.add(new Rectangle(200, G - 160, 160, 16));
                platforms.add(new Rectangle(380, G - 240, 160, 16));
                platforms.add(new Rectangle(600, G - 220, 160, 16));
                platforms.add(new Rectangle(820, G - 260, 170, 16));
                spikes.add(new HiddenSpike(600, G - 220 - 18, 160, 18, 700));
                doors.add(new ExitDoor(820, G - 260 - 48, 28, 48, true));
                realDoor = new ExitDoor(960, G - 260 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 7:
                spawn = new Point(60, G - 80);
                platforms.add(new Rectangle(200, G - 160, 160, 16));
                platforms.add(new Rectangle(420, G - 260, 150, 16));
                platforms.add(new Rectangle(620, G - 240, 150, 16));
                fragiles.add(new FragileTile(620, G - 240 - 16, 50, 16));
                teleports.add(new TeleportTile(420, G - 260 - 16, 40, 16, new Point(770, G - 260 - 60)));
                realDoor = new ExitDoor(900, G - 260 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 8:
                spawn = new Point(60, G - 80);
                platforms.add(new Rectangle(140, G - 160, 160, 14));
                platforms.add(new Rectangle(340, G - 250, 160, 14));
                platforms.add(new Rectangle(540, G - 290, 160, 14));
                platforms.add(new Rectangle(740, G - 250, 160, 14));
                saws.add(new MovingSaw(150, G - 210 - 22, 170, 3.4));
                saws.add(new MovingSaw(350, G - 250 - 22, 170, 3.6));
                saws.add(new MovingSaw(550, G - 290 - 22, 170, 3.8));
                realDoor = new ExitDoor(900, G - 250 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 9:
                spawn = new Point(60, G - 80);
                platforms.add(new Rectangle(160, G - 160, 120, 12));
                platforms.add(new Rectangle(320, G - 260, 120, 12));
                platforms.add(new Rectangle(480, G - 300, 120, 12));
                platforms.add(new Rectangle(640, G - 260, 120, 12));
                platforms.add(new Rectangle(800, G - 220, 160, 12));
                collapsers.add(new CollapsingTile(160, G - 220 - 16, 40, 16, 150));
                collapsers.add(new CollapsingTile(320, G - 260 - 16, 40, 16, 150));
                collapsers.add(new CollapsingTile(480, G - 300 - 16, 40, 16, 150));
                doors.add(new ExitDoor(800, G - 220 - 48, 28, 48, true));
                realDoor = new ExitDoor(920, G - 220 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 10:
                spawn = new Point(70, G - 80);
                platforms.add(new Rectangle(200, G - 180, 140, 14));
                platforms.add(new Rectangle(420, G - 280, 140, 14));
                platforms.add(new Rectangle(640, G - 260, 140, 14));
                platforms.add(new Rectangle(860, G - 240, 160, 14));
                fragiles.add(new FragileTile(420, G - 280 - 16, 60, 16));
                saws.add(new MovingSaw(300, G - 40 - 26, 600, 4.2));
                realDoor = new ExitDoor(960, G - 240 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 11:
                spawn = new Point(60, G - 80);
                platforms.add(new Rectangle(260, G - 160, 160, 16));
                platforms.add(new Rectangle(520, G - 240, 160, 16));
                platforms.add(new Rectangle(760, G - 280, 160, 16));
                teleports.add(new TeleportTile(520, G - 240 - 16, 44, 16, new Point(900, G - 280 - 60)));
                realDoor = new ExitDoor(980, G - 280 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 12:
                spawn = new Point(70, G - 80);
                platforms.add(new Rectangle(220, G - 140, 140, 16));
                platforms.add(new Rectangle(420, G - 270, 140, 16));
                platforms.add(new Rectangle(620, G - 230, 140, 16));
                platforms.add(new Rectangle(820, G - 270, 160, 16));
                collapsers.add(new CollapsingTile(420, G - 270 - 16, 40, 16, 240));
                collapsers.add(new CollapsingTile(620, G - 230 - 16, 40, 16, 240));
                saws.add(new MovingSaw(230, G - 230 - 22, 200, 3.3));
                doors.add(new ExitDoor(820, G - 270 - 48, 28, 48, true));
                realDoor = new ExitDoor(940, G - 270 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 13:
                spawn = new Point(70, G - 80);
                platforms.add(new Rectangle(220, G - 140, 150, 16));
                platforms.add(new Rectangle(420, G - 230, 150, 16));
                platforms.add(new Rectangle(620, G - 230, 150, 16));
                platforms.add(new Rectangle(820, G - 230, 150, 16));
                spikes.add(new HiddenSpike(220, G - 230 - 18, 150, 18, 600));
                spikes.add(new HiddenSpike(420, G - 230 - 18, 150, 18, 600));
                spikes.add(new HiddenSpike(620, G - 230 - 18, 150, 18, 600));
                spikes.add(new HiddenSpike(820, G - 230 - 18, 150, 18, 600));
                realDoor = new ExitDoor(940, G - 230 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
            case 14:
                spawn = new Point(70, G - 80);
                platforms.add(new Rectangle(200, G - 150, 150, 16));
                platforms.add(new Rectangle(400, G - 300, 150, 16));
                platforms.add(new Rectangle(600, G - 260, 150, 16));
                platforms.add(new Rectangle(800, G - 220, 180, 16));
                saws.add(new MovingSaw(210, G - 260 - 22, 180, 3.5));
                fragiles.add(new FragileTile(600, G - 260 - 16, 60, 16));
                doors.add(new ExitDoor(800, G - 220 - 48, 28, 48, true));
                realDoor = new ExitDoor(940, G - 220 - 48, 28, 48, false);
                doors.add(realDoor);
                break;
        }

        // Reset player
        player.x = spawn.x;
        player.y = spawn.y;
        vx = vy = 0;
        onGround = false;
    }

    /* ---------- Quiz ---------- */
    private void askQuestionForLevel() {
        inDialog = true;

        Question q = lastWrongPerLevel.get(levelIndex);
        if (q == null) {
            q = qManager.nextNonRepeatedQuestion(usedQuestionIds);
            usedQuestionIds.add(q.id);
        }

        JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
        JLabel title = new JLabel("Level " + (levelIndex + 1) + " – Java Gate");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        p.add(title);
        p.add(new JLabel("<html><body style='width:460px'>" + q.prompt + "</body></html>"));

        ButtonGroup group = new ButtonGroup();
        JRadioButton[] radios = new JRadioButton[q.options.size()];
        for (int i = 0; i < q.options.size(); i++) {
            radios[i] = new JRadioButton(q.options.get(i));
            group.add(radios[i]);
            p.add(radios[i]);
        }

        int res = JOptionPane.showConfirmDialog(
                this, p, "Answer wisely, " + titleFor(playerGender) + " " + playerName,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        boolean correct = false;
        if (res == JOptionPane.OK_OPTION) {
            for (int i = 0; i < radios.length; i++)
                if (radios[i].isSelected()) {
                    correct = (i == q.correctIndex);
                    break;
                }
        }

        if (correct) {
            JOptionPane.showMessageDialog(this, "Correct! Door unlocks, " + titleFor(playerGender) + " " + playerName + ".");
            lastWrongPerLevel.remove(levelIndex);
            nextLevel();
        } else {
            lastWrongPerLevel.put(levelIndex, q);
            JOptionPane.showMessageDialog(this,
                    "Incorrect, " + titleFor(playerGender) + " " + playerName + ".\nSame question will repeat for this level.",
                    "Try Again", JOptionPane.ERROR_MESSAGE);
            die();
        }

        inDialog = false;
    }

    /* ---------- UI helpers ---------- */
    private String titleFor(String g) {
        if ("Male".equalsIgnoreCase(g)) return "Mr.";
        if ("Female".equalsIgnoreCase(g)) return "Ms.";
        return "Champ";
    }

    private String pronounFor(String g) {
        if ("Male".equalsIgnoreCase(g)) return "bro";
        if ("Female".equalsIgnoreCase(g)) return "sis";
        return "friend";
    }

    /* ---------- Render ---------- */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(new Color(22, 24, 30));
        g2.fillRect(0, 0, W, H);
        g2.setColor(new Color(26, 28, 36));
        g2.fillRect(0, H - 260, W, 260);

        // Platforms (shadow look)
        g2.setColor(new Color(64, 68, 80));
        for (Rectangle r : platforms) g2.fillRect(r.x, r.y, r.width, r.height);

        for (SecretPlatform s : secrets) s.draw(g2);
        for (CollapsingTile c : collapsers) c.draw(g2);
        for (FragileTile f : fragiles) f.draw(g2);
        for (HiddenSpike s : spikes) s.draw(g2);
        for (MovingSaw saw : saws) saw.draw(g2);
        for (TeleportTile t : teleports) t.draw(g2);
        for (ExitDoor d : doors) d.draw(g2);

        // Player (pixel sprite)
        drawPixelDude(g2, player.x, player.y, !onGround ? "jump" : (Math.abs(vx) > 0.1 ? "run" : "idle"),
                facingRight, runFrame);

        // HUD
        g2.setColor(Color.WHITE);
        g2.drawString("Player: " + playerName + "   Level: " + (Math.min(levelIndex + 1, 15)) + "/15   Deaths: " + deaths, 12, 18);
        g2.drawString("Move: A/D or \u2190/\u2192   Jump: Space   Reset: R   Exit: ESC", 12, 36);
        g2.drawString("Tip: Collapsers fall; hairline tiles break; dotted edges bite; saws spin; fake doors shake.", 12, 54);

        // Real-door hint (after 3 deaths)
        if (deaths >= 3 && realDoor != null) {
            int baseY = realDoor.bounds.y - 20;
            int left = realDoor.bounds.x - 20;
            int right = realDoor.bounds.x + realDoor.bounds.width + 20;
            g2.setColor(new Color(255, 240, 120, 160));
            g2.drawLine(left, baseY, right, baseY);

            int sweepX = realDoor.bounds.x + realDoor.bounds.width / 2 + arrowOffsetX;
            Polygon tri = new Polygon(
                    new int[]{sweepX, sweepX + 6, sweepX - 6},
                    new int[]{baseY - 6, baseY + 6, baseY + 6}, 3);
            g2.fillPolygon(tri);
        }

        if (gameFinished) {
            String msg = "ALL LEVELS CLEARED!";
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30f));
            int w = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (W - w) / 2, 100);
        }

        g2.dispose();
    }

    // Draw a simple 16x16 pixel character using rectangles (Electric Neon Blue hoodie)
    private void drawPixelDude(Graphics2D g2, int x, int y, String state, boolean faceRight, int frame) {
        int px = 2; // each pixel is 2x2
        int ox = x + (player.width - 32) / 2; // center
        int oy = y + player.height - 32;    // bottom align

        Color neonBlue = new Color(30, 180, 255);
        Color darkBlue = new Color(10, 70, 120);
        Color shadow = new Color(15, 15, 20, 140);
        Color skin = new Color(240, 210, 180);
        Color black = new Color(0, 0, 0);

        // Simple ground shadow
        g2.setColor(shadow);
        g2.fillOval(ox + 4, oy + 30, 24, 6);

        // Head (hood)
        fill(g2, neonBlue, ox + 6 * px, oy + 0 * px, 4 * px, 2 * px);
        fill(g2, neonBlue, ox + 4 * px, oy + 2 * px, 8 * px, 4 * px);
        // face opening
        fill(g2, skin, ox + 6 * px, oy + 3 * px, 4 * px, 3 * px);
        // outline eyes
        fill(g2, black, ox + 7 * px, oy + 4 * px, 1 * px, 1 * px);
        fill(g2, black, ox + 8 * px, oy + 4 * px, 1 * px, 1 * px);

        // Torso (hoodie)
        fill(g2, neonBlue, ox + 4 * px, oy + 6 * px, 8 * px, 6 * px);
        // hoodie shade
        fill(g2, darkBlue, ox + 4 * px, oy + 6 * px, 2 * px, 6 * px);
        fill(g2, darkBlue, ox + 10 * px, oy + 6 * px, 2 * px, 6 * px);

        // Arms (pose varies)
        if ("jump".equals(state)) {
            if (faceRight) {
                fill(g2, neonBlue, ox + 3 * px, oy + 6 * px, 2 * px, 4 * px);
                fill(g2, neonBlue, ox + 12 * px, oy + 5 * px, 2 * px, 4 * px);
            } else {
                fill(g2, neonBlue, ox + 3 * px, oy + 5 * px, 2 * px, 4 * px);
                fill(g2, neonBlue, ox + 12 * px, oy + 6 * px, 2 * px, 4 * px);
            }
        } else if ("run".equals(state)) {
            int f = frame % 6;
            int offL = (f == 1 || f == 2) ? 1 : 0;
            int offR = (f == 4 || f == 5) ? 1 : 0;
            fill(g2, neonBlue, ox + 3 * px, oy + (6 + offL) * px, 2 * px, 4 * px);
            fill(g2, neonBlue, ox + 12 * px, oy + (6 + offR) * px, 2 * px, 4 * px);
        } else {
            fill(g2, neonBlue, ox + 3 * px, oy + 7 * px, 2 * px, 4 * px);
            fill(g2, neonBlue, ox + 12 * px, oy + 7 * px, 2 * px, 4 * px);
        }

        // Legs (run cycle)
        if ("run".equals(state)) {
            int f = frame % 6;
            if (f <= 2) {
                fill(g2, black, ox + 5 * px, oy + 12 * px, 3 * px, 4 * px);
                fill(g2, black, ox + 9 * px, oy + 12 * px, 3 * px, 4 * px);
            } else {
                fill(g2, black, ox + 5 * px, oy + 12 * px, 3 * px, 4 * px);
                fill(g2, black, ox + 9 * px, oy + 12 * px, 3 * px, 4 * px);
                g2.setColor(black);
                g2.fillRect(ox + 5 * px, oy + 12 * px + ((f == 3 || f == 4) ? 1 : 0), 3 * px, 4 * px);
            }
        } else if ("jump".equals(state)) {
            fill(g2, black, ox + 6 * px, oy + 12 * px, 3 * px, 4 * px);
            fill(g2, black, ox + 9 * px, oy + 12 * px, 3 * px, 4 * px);
        } else {
            fill(g2, black, ox + 6 * px, oy + 12 * px, 3 * px, 4 * px);
            fill(g2, black, ox + 9 * px, oy + 12 * px, 3 * px, 4 * px);
        }
    }

    private void fill(Graphics2D g2, Color c, int x, int y, int w, int h) {
        g2.setColor(c);
        g2.fillRect(x, y, w, h);
    }

    /* ---------- Input ---------- */
    @Override
    public void keyPressed(KeyEvent e) {
        if (inDialog) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                left = true;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                right = true;
                break;
            case KeyEvent.VK_SPACE:
                if (onGround) {
                    vy = JUMP;
                    onGround = false;
                }
                break;
            case KeyEvent.VK_R:
                die();
                break;
            case KeyEvent.VK_ESCAPE:
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Game-a close pannalama, " + titleFor(playerGender) + " " + playerName + "?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) System.exit(0);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                left = false;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                right = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

/* ------------------------- WORLD OBJECTS ------------------------- */
class CollapsingTile {
    Rectangle bounds;
    boolean solid = true;
    private final int delayMs;
    private long landTime = -1;

    CollapsingTile(int x, int y, int w, int h, int delayMs) {
        bounds = new Rectangle(x, y, w, h);
        this.delayMs = delayMs;
    }

    void onLand(Rectangle player) {
        if (!solid) return;
        if (player.intersects(bounds) && landTime < 0) landTime = System.currentTimeMillis();
    }

    void update(Rectangle player) {
        if (!solid) { bounds.y += 6; return; }
        if (landTime > 0 && System.currentTimeMillis() - landTime >= delayMs) solid = false;
    }

    void reset() {
        solid = true;
        landTime = -1;
    }

    void draw(Graphics2D g2) {
        g2.setColor((solid && landTime > 0) ? new Color(180, 90, 90) : new Color(82, 86, 100));
        g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}

class FragileTile {
    Rectangle bounds;
    boolean solid = true;

    FragileTile(int x, int y, int w, int h) {
        bounds = new Rectangle(x, y, w, h);
    }

    boolean update(Rectangle player) {
        if (!solid) { bounds.y += 6; return false; }
        boolean landed = player.intersects(bounds) && Math.abs((player.y + player.height) - bounds.y) <= 2;
        if (landed) {
            solid = false;
            return true;
        }
        return false;
    }

    void reset() {
        solid = true;
    }

    void draw(Graphics2D g2) {
        g2.setColor(new Color(90, 90, 98));
        g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2.setColor(new Color(140, 140, 150));
        int cx = bounds.x + 4, cy = bounds.y + bounds.height / 2;
        g2.drawLine(cx, cy, cx + bounds.width - 8, cy - 4);
        g2.drawLine(cx + 10, cy - 6, cx + bounds.width - 10, cy + 2);
    }
}

class HiddenSpike {
    Rectangle zone;
    private final int idleMs;
    private long enterTime = -1;
    private boolean popped = false;

    HiddenSpike(int x, int y, int w, int h, int idleMs) {
        zone = new Rectangle(x, y, w, h);
        this.idleMs = idleMs;
    }

    boolean update(Rectangle player) {
        if (popped) return false;
        boolean on = player.x + player.width > zone.x && player.x < zone.x + zone.width &&
                Math.abs((player.y + player.height) - zone.y) <= 2;
        if (on) {
            if (enterTime < 0) enterTime = System.currentTimeMillis();
            if (System.currentTimeMillis() - enterTime >= idleMs) {
                popped = true;
                return true;
            }
        } else enterTime = -1;
        return false;
    }

    void reset() {
        popped = false;
        enterTime = -1;
    }

    void draw(Graphics2D g2) {
        Stroke old = g2.getStroke();
        g2.setColor(new Color(120, 120, 130));
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{4f, 4f}, 0f));
        g2.drawLine(zone.x, zone.y, zone.x + zone.width, zone.y);
        g2.setStroke(old);
        if (popped) {
            g2.setColor(new Color(230, 70, 70));
            int teeth = Math.max(3, zone.width / 12), step = zone.width / teeth;
            for (int i = 0; i < teeth; i++) {
                int sx = zone.x + i * step;
                Polygon tri = new Polygon(new int[]{sx, sx + step / 2, sx + step},
                        new int[]{zone.y, zone.y - 14, zone.y}, 3);
                g2.fillPolygon(tri);
            }
        }
    }
}

class MovingSaw {
    double x, y;
    int range;
    double speed;
    int radius = 14;
    private int x0, x1;

    MovingSaw(int startX, int yTop, int range, double speed) {
        this.x = startX;
        this.y = yTop;
        this.range = range;
        this.speed = speed;
        this.x0 = startX;
        this.x1 = startX + range;
    }

    boolean update(Rectangle player) {
        x += speed;
        if (x < x0 || x > x1) speed = -speed;
        double cx = Math.max(player.x, Math.min(x, player.x + player.width));
        double cy = Math.max(player.y, Math.min(y, player.y + player.height));
        double dx = x - cx, dy = y - cy;
        return dx * dx + dy * dy <= radius * radius;
    }

    void reset() {
    }

    void draw(Graphics2D g2) {
        g2.setColor(new Color(200, 200, 210));
        g2.fillOval((int) x - radius, (int) y - radius, radius * 2, radius * 2);
        g2.setColor(new Color(60, 60, 70));
        g2.drawOval((int) x - radius, (int) y - radius, radius * 2, radius * 2);
    }
}

class TeleportTile {
    Rectangle bounds;
    Point dest;
    boolean solid = true;
    private boolean armed = true;

    TeleportTile(int x, int y, int w, int h, Point dest) {
        bounds = new Rectangle(x, y, w, h);
        this.dest = dest;
    }

    void update(Rectangle player) {
        boolean on = player.intersects(bounds) && Math.abs((player.y + player.height) - bounds.y) <= 2;
        if (on && armed) {
            player.x = dest.x;
            player.y = dest.y;
            armed = false;
        }
    }

    void reset() {
        armed = true;
    }

    void draw(Graphics2D g2) {
        g2.setColor(new Color(120, 110, 170));
        g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2.setColor(new Color(200, 190, 240));
        g2.drawRect(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
    }
}

class SecretPlatform {
    Rectangle bounds;
    boolean solid = true;
    private boolean revealed = false;

    SecretPlatform(int x, int y, int w, int h) {
        bounds = new Rectangle(x, y, w, h);
    }

    void update(Rectangle player) {
        if (!revealed && player.intersects(bounds)) revealed = true;
    }

    void reset() {
    }

    void draw(Graphics2D g2) {
        g2.setColor(revealed ? new Color(90, 150, 120) : new Color(40, 40, 40));
        g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}

class ExitDoor {
    Rectangle bounds;
    boolean fake;
    private int shakeTimer = 0;

    ExitDoor(int x, int y, int w, int h, boolean fake) {
        bounds = new Rectangle(x, y, w, h);
        this.fake = fake;
    }

    void shake() {
        if (fake) shakeTimer = 14;
    }

    void draw(Graphics2D g2) {
        int tx = 0;
        if (fake && shakeTimer > 0) {
            tx = (shakeTimer % 4) - 2;
            shakeTimer--;
        }
        g2.translate(tx, 0);
        if (fake) g2.setColor(new Color(120, 120, 120));
        else g2.setColor(new Color(185, 170, 120));
        g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);
        g2.setColor(new Color(45, 35, 20));
        g2.fillRoundRect(bounds.x + 8, bounds.y + 10, bounds.width - 16, bounds.height - 20, 6, 6);
        g2.setColor(fake ? new Color(160, 160, 160) : new Color(185, 170, 120));
        g2.fillOval(bounds.x + bounds.width - 14, bounds.y + bounds.height / 2, 6, 6);
        g2.translate(-tx, 0);
    }
}

/* ------------------------- QUIZ SYSTEM ------------------------- */
class Question {
    final int id;
    final String prompt;
    final List<String> options;
    final int correctIndex;

    Question(int id, String p, List<String> opts, int idx) {
        this.id = id;
        this.prompt = p;
        this.options = opts;
        this.correctIndex = idx;
    }
}

class QuestionManager {
    private final List<Question> pool = new ArrayList<>();

    QuestionManager() {
        int id = 0;
        add(id++, "Which keyword prevents inheritance?", o("static", "final", "const", "sealed"), 1);
        add(id++, "Valid entry point in Java 11?", o("public static void main(String[] args)", "public void main(String[] args)", "static public main(String args[])", "public int main(String[] a)"), 0);
        add(id++, "Which collection allows duplicates?", o("Set", "Map keys", "List", "EnumSet"), 2);
        add(id++, "Which is NOT a primitive?", o("int", "char", "string", "double"), 2);
        add(id++, "Checked exception example:", o("NullPointerException", "IOException", "ArithmeticException", "ClassCastException"), 1);
        add(id++, "JVM stands for:", o("Java Virtual Machine", "Just Very Mad", "Java Vendor Manager", "Jumbo VM"), 0);
        add(id++, "Thread-safe by default?", o("ArrayList", "StringBuilder", "StringBuffer", "HashMap"), 2);
        add(id++, "Access: package + subclass?", o("private", "default", "protected", "public"), 2);
        add(id++, "GC runs…", o("Automatically (nondeterministic)", "Manually only", "Never", "Every 5s"), 0);
        add(id++, "'==' on objects checks…", o("Reference equality", "Deep equality", "hashCode equality", "toString equality"), 0);
        add(id++, "Java compiles to…", o("ELF", "Bytecode (.class)", "EXE", "WASM"), 1);
        add(id++, "Interface with compare(T,T)?", o("Comparable", "Comparator", "Iterable", "Cloneable"), 1);
        add(id++, "Immutable among these:", o("String", "StringBuilder", "StringBuffer", "char[]"), 0);
        add(id++, "final class means…", o("Cannot be instantiated", "Cannot be inherited", "Must be abstract", "Cannot have methods"), 1);
        add(id++, "Default int field value?", o("0", "1", "null", "undefined"), 0);
        add(id++, "Read text lines easily:", o("BufferedReader", "FileOutputStream", "ObjectOutputStream", "DataInputStream"), 0);
        add(id++, "Can main be overloaded?", o("Yes", "No", "Only private", "Only package"), 0);
        add(id++, "Inheritance keyword:", o("this", "super", "extends", "import"), 2);
        add(id++, "Auto-imported package:", o("java.lang", "java.util", "java.io", "none"), 0);
        add(id++, "Faster for string concat in loop:", o("StringBuilder", "String +", "Reflection", "println"), 0);
        add(id++, "Functional interface:", o("Runnable", "List", "Map", "Thread"), 0);
        add(id++, "Create thread:", o("new Thread(r).start()", "new Runnable().run()", "Thread.sleep(0)", "System.gc()"), 0);
        add(id++, "volatile ensures…", o("Visibility across threads", "Serialization", "I/O speed", "JIT hints"), 0);
        add(id++, "Stop overriding:", o("final", "private", "static", "native"), 0);
        add(id++, "HashMap permits:", o("null key and null values", "only null key", "no nulls", "only null values"), 0);
        add(id++, "Default switch in Java is…", o("Optional", "Mandatory", "Deprecated", "Error"), 0);
        add(id++, "Stream terminal operation:", o("collect", "map", "filter", "peek"), 0);
        add(id++, "Optional.ofNullable(x) allows:", o("null or non-null", "only non-null", "only null", "neither"), 0);
        add(id++, "try-with-resources works with:", o("AutoCloseable", "Serializable", "Cloneable", "Runnable"), 0);
        add(id++, "'this' refers to:", o("current object", "parent object", "global object", "class object"), 0);
        add(id++, "String pool located in:", o("Heap", "Stack", "PermGen only", "Registers"), 0);
        add(id++, "equals() + hashCode() contract?", o("must agree", "independent", "irrelevant", "only hashCode matters"), 0);
        add(id++, "ArrayList growth behavior:", o("dynamic (resizes)", "fixed", "linked", "tree"), 0);
        add(id++, "Interface methods default modifier (Java 8+):", o("public abstract", "private", "protected", "package"), 0);
        add(id++, "Interfaces can have default methods?", o("Yes", "No", "Only static", "Only private"), 0);
        add(id++, "Local variable default value:", o("No default", "0", "null", "false"), 0);
        add(id++, "Synchronized on method locks:", o("this", "classloader", "JVM", "nothing"), 0);
        add(id++, "final vs finally vs finalize:", o("keyword / block / method", "all keywords", "all methods", "all blocks"), 0);
        add(id++, "Enum can:", o("have fields & methods", "be extended by class", "have public constructors", "be generic"), 0);
        add(id++, "StringBuilder is:", o("mutable & not thread-safe", "immutable", "thread-safe", "deprecated"), 0);
        add(id++, "Polymorphism mainly via:", o("overriding", "overloading", "shadowing", "casting"), 0);
        add(id++, "Which sorts natural order?", o("All of these", "Collections.sort(list)", "list.sort(null)", "Arrays.sort(arr)"), 0);
        add(id++, "Serializable UID field name:", o("serialVersionUID", "serialUID", "versionUID", "UID"), 0);
        add(id++, "JIT compiles:", o("bytecode to native at runtime", "Java to bytecode at compile-time", "SQL to Java", "HTML to CSS"), 0);
        add(id++, "var is:", o("compile-time inferred", "runtime type", "dynamic variable", "keyword for int"), 0);
        add(id++, "Records (Java 16+) are:", o("immutable data carriers", "GUI components", "network sockets", "JNDI entries"), 0);
        add(id++, "Sealed classes allow:", o("restricted subclassing", "dynamic proxies only", "no inheritance", "multiple parents"), 0);
        add(id++, "Run jar:", o("java -jar app.jar", "java app.jar", "run app.jar", "jar app.jar"), 0);
        add(id++, "Functional interface annotation:", o("@FunctionalInterface", "@Interface", "@Lambda", "@SingleMethod"), 0);
        add(id++, "Stream parallel() does:", o("parallelizes stream", "multiplies results", "sorts", "deduplicates"), 0);
        add(id++, "Files.readAllLines returns:", o("List<String>", "Stream<String>", "String", "byte[]"), 0);
        add(id++, "NIO Path from string:", o("Paths.get(...)", "Files.path(...)", "Path.ofFile(...)", "IO.newPath(...)"), 0);
        // fun
        add(id++, "Fun: How many months have 28 days?", o("12", "1", "6", "Only Feb"), 0);
        add(id++, "Fun: Java stands for?", o("A tasty drink + language", "Just Another Vague Acronym", "Jammu And Vapi Association", "Joint AI Venture Association"), 0);
    }

    private static List<String> o(String... a) {
        return Arrays.asList(a);
    }

    private void add(int id, String p, List<String> opts, int idx) {
        pool.add(new Question(id, p, opts, idx));
    }

    Question nextNonRepeatedQuestion(Set<Integer> used) {
        List<Question> cand = new ArrayList<>();
        for (Question q : pool) if (!used.contains(q.id)) cand.add(q);
        if (cand.isEmpty()) return pool.get(new Random().nextInt(pool.size()));
        return cand.get(new Random().nextInt(cand.size()));
    }
}

