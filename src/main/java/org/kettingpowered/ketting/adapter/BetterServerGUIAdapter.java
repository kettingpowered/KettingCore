package org.kettingpowered.ketting.adapter;

import org.kettingpowered.ketting.types.Player;

import javax.swing.*;
import java.util.List;

public interface BetterServerGUIAdapter extends SharedConstants {

    default void reload() {}

    void onWindowClosing();
    void onStart();

    void onCommand(String command);
    void addComponents(JComponent mainWindow);

    List<Player> getPlayers();

    void addRightClickMenuItems(JPopupMenu menu, Player selectedPlayer);
}
