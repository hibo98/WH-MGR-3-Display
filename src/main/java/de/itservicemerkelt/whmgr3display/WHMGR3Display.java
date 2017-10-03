package de.itservicemerkelt.whmgr3display;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WHMGR3Display {

    private final TrayIcon trayIcon;
    private Timer timer;
    private double lastVolume;

    public WHMGR3Display() throws AWTException {
        double volume = lastVolume = getVolume();
        BufferedImage img = TextConverter.textToImage(String.valueOf((int) volume), 13);
        trayIcon = new TrayIcon(img, "WH-MGR 3 Display\rDatenvolumen: " + volume + " GB", createPopupMenu());
        SystemTray.getSystemTray().add(trayIcon);
        startTimer();
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 60 * 1000, 60 * 1000);
    }

    private void update() {
        double volume = getVolume();
        trayIcon.setToolTip("WH-MGR 3 Display\rDatenvolumen: " + volume + " GB");
        if ((int) lastVolume != (int) volume) {
            trayIcon.setImage(TextConverter.textToImage(String.valueOf((int) volume), 13));
        }
        if ((volume < 2 && lastVolume > 2) || (volume < 5 && lastVolume > 5)) {
            trayIcon.displayMessage("WH-MGR 3 Display", "Verbleibendes Volumen: " + volume + " GB", TrayIcon.MessageType.INFO);
        }
        lastVolume = volume;
    }

    private PopupMenu createPopupMenu() {
        PopupMenu popupMenu = new PopupMenu("WH-MGR 3 Display");
        MenuItem exit = new MenuItem("Schließen");
        exit.addActionListener((e) -> {
            System.exit(0);
        });
        MenuItem refresh = new MenuItem("Neuabrufen");
        refresh.addActionListener((e) -> {
            update();
        });
        popupMenu.add(refresh);
        popupMenu.add(exit);
        return popupMenu;
    }

    private double getVolume() {
        try {
            Document doc = Jsoup.connect("http://141.46.245.1/").get();
            Element traffic = doc.select("tr").get(1);
            Element volume = traffic.select("td").get(1);
            return Double.parseDouble(volume.html().replace(" GB", ""));
        } catch (IOException ex) {
            Logger.getLogger(WHMGR3Display.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static void main(String[] args) throws AWTException {
        new WHMGR3Display();
    }

}
