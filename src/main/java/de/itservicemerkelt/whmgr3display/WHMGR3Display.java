package de.itservicemerkelt.whmgr3display;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
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
    private Float lastVolume = 0f;

    public WHMGR3Display() throws AWTException {
        trayIcon = new TrayIcon(TextConverter.textToImage("", 13), "", createPopupMenu());
        SystemTray.getSystemTray().add(trayIcon);
        update();
        startTimer();
    }

    private void startTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 60 * 1000, 60 * 1000);
    }

    private void update() {
        Float volume = getVolume();
        if (lastVolume == null &&volume == null) {
            return;
        }
        if (volume == null) {
            trayIcon.setImage(TextConverter.textToImage("X", 13));
            trayIcon.setToolTip("WH-MGR 3 Display\rFehler beim Abruf!");
            return;
        } else if (lastVolume == null || lastVolume.intValue() != volume.intValue()) {
            trayIcon.setImage(TextConverter.textToImage(String.valueOf(volume.intValue()), 13));
            trayIcon.setToolTip("WH-MGR 3 Display\rDatenvolumen: " + volume + " GB");
        }
        if (lastVolume != null &&((volume < 2 && lastVolume > 2) || (volume < 5 && lastVolume > 5))) {
            trayIcon.displayMessage("WH-MGR 3 Display", "Verbleibendes Volumen: " + volume + " GB", TrayIcon.MessageType.INFO);
        }
        lastVolume = volume;
    }

    private PopupMenu createPopupMenu() {
        PopupMenu popupMenu = new PopupMenu("WH-MGR 3 Display");
        MenuItem exit = new MenuItem("Beenden");
        exit.addActionListener((e) -> System.exit(0));
        MenuItem refresh = new MenuItem("Neuabrufen");
        refresh.addActionListener((e) -> update());
        popupMenu.add(refresh);
        popupMenu.add(exit);
        return popupMenu;
    }

    private Float getVolume() {
        try {
            Document doc = Jsoup.connect("http://141.46.245.1/").get();
            Element traffic = doc.select("tr").get(1);
            Element volume = traffic.select("td").get(1);
            return Float.parseFloat(volume.html().replace(" GB", ""));
        } catch (IOException ex) {
            Logger.getLogger(WHMGR3Display.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String[] args) throws AWTException {
        new WHMGR3Display();
    }

}
