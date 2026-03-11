package com.jdgame;

import com.jdgame.db.DBManager;
import com.jdgame.ui.LoginWindow;

public class Main {
    public static void main(String[] args) {
        DBManager db = new DBManager();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new com.jdgame.LoginWindow(db).setVisible(true);
        });
    }
}
