package org.kettingpowered.ketting.common.betterservergui.components;

import org.kettingpowered.ketting.common.betterservergui.BetterServerGUI;
import org.kettingpowered.ketting.common.betterservergui.GUIColors;
import org.kettingpowered.ketting.types.Player;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListComponent extends JPanel {

    private final JList<Player> playerList;
    private final JScrollPane scrollPane;
    private final TitledBorder border;

    public PlayerListComponent(BetterServerGUI instance) {
        super();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0));

        playerList = new JList<>();
        playerList.setCellRenderer(new PlayerComponent());
        playerList.setModel(new DefaultListModel<>());
        playerList.setFont(instance.getSettings().font);
        playerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    final int i = playerList.locationToIndex(e.getPoint());
                    if (i != -1) {
                        playerList.setSelectedIndex(i);
                        Player player = playerList.getModel().getElementAt(i);
                        onDoubleClick(e, player);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    final int i = playerList.locationToIndex(e.getPoint());
                    if (i != -1) {
                        playerList.setSelectedIndex(i);
                        Player player = playerList.getModel().getElementAt(i);
                        createMenu(e, player, instance);
                    }
                }
            }
        });

        scrollPane = new JScrollPane(playerList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        border = new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, GUIColors.PlayerComponent.BORDER, GUIColors.PlayerComponent.BORDER_SHADOW), "Players");
        border.setTitleFont(instance.getSettings().font);
        border.setTitleColor(GUIColors.PlayerComponent.BORDER);
        setBorder(border);

        add(scrollPane, BorderLayout.CENTER);

        setBackground(GUIColors.PlayerComponent.BACKGROUND);

        playerList.setBackground(GUIColors.PlayerComponent.LIST_BACKGROUND);
        playerList.setForeground(GUIColors.PlayerComponent.LIST_FOREGROUND);

        scrollPane.setBackground(playerList.getBackground());
        scrollPane.setForeground(playerList.getForeground());
    }

    private void onDoubleClick(MouseEvent e, Player selectedPlayer) {
        try {
            Desktop.getDesktop().browse(new URI("https://namemc.com/profile/" + selectedPlayer.name()));
        } catch (Exception ex) {
            BetterServerGUI.LOGGER.warn("Failed to open browser", ex);
        }
    }

    private void createMenu(MouseEvent e, Player selectedPlayer, BetterServerGUI instance) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Msg");
        item.addActionListener(e1 -> {
            String msg = JOptionPane.showInputDialog(null, "Message to send to " + selectedPlayer.name(), "Message", JOptionPane.PLAIN_MESSAGE);
            if (msg != null && !msg.isBlank())
                instance.getChat().sendCommand("tell %s %s".formatted(selectedPlayer.name(), msg));
        });
        menu.add(item);

        item = new JMenuItem("Kick");
        item.addActionListener(e1 -> {
            instance.getChat().sendCommand("kick " + selectedPlayer.name());
        });
        menu.add(item);

        item = new JMenuItem("Ban");
        item.addActionListener(e1 -> {
            instance.getChat().sendCommand("ban " + selectedPlayer.name());
        });
        menu.add(item);

        item = new JMenuItem(selectedPlayer.isOperator().getAsBoolean() ? "Deop" : "Op");
        item.addActionListener(e1 -> {
            if (selectedPlayer.isOperator().getAsBoolean())
                instance.getChat().sendCommand("deop " + selectedPlayer.name());
            else {
                int op = JOptionPane.showConfirmDialog(null, "Are you sure you want to op " + selectedPlayer.name() + "?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if(op == JOptionPane.YES_OPTION)
                    instance.getChat().sendCommand("op " + selectedPlayer.name());
            }
        });
        menu.add(item);

        instance.getSettings().adapter.addRightClickMenuItems(menu, selectedPlayer);

        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    public void updatePlayers(BetterServerGUI.Builder settings) {
        List<Player> players = new ArrayList<>(settings.adapter.getPlayers());
        if (!needsUpdate(players))
            return;

        playerList.setListData(players.toArray(new Player[0]));

        border.setTitle("Players (" + players.size() + ")");
        repaint();
        revalidate();
    }

    public void reload() {
        ((PlayerComponent) playerList.getCellRenderer()).reload();
        repaint();
        revalidate();
    }

    private boolean needsUpdate(List<Player> players) {
        if (playerList.getModel().getSize() != players.size())
            return true;

        for (int i = 0; i < players.size(); i++) {
            if (!playerList.getModel().getElementAt(i).equals(players.get(i)))
                return true;
        }

        return false;
    }

    static class PlayerComponent implements ListCellRenderer<Player> {

        private static final Map<String, JLabel> playerCache = new HashMap<>();
        private static final Map<String, ImageIcon> imageCache = new HashMap<>();

        PlayerComponent() {
            super();
        }

        public void reload() {
            playerCache.clear();
            imageCache.clear();
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Player> list, Player value, int index, boolean isSelected, boolean cellHasFocus) {
            if (playerCache.containsKey(value.name())) {
                final JLabel jLabel = playerCache.get(value.name());
                jLabel.setBackground(isSelected ? GUIColors.PlayerComponent.LIST_ITEM_SELECTED_BACKGROUND : GUIColors.PlayerComponent.LIST_ITEM_BACKGROUND);
                return jLabel;
            }

            JLabel playerImage = new JLabel();

            try {
                ImageIcon icon = imageCache.get(value.name());

                if (icon == null) {
                    URL url = new URL(String.format("https://crafatar.com/avatars/%s?size=32&overlay", value.uuid().toString()));
                    icon = new ImageIcon(url);
                    imageCache.put(value.name(), icon);
                }

                playerImage.setIcon(icon);
            } catch (Exception e) {
                BetterServerGUI.LOGGER.warn("Failed to load player image for " + value.name(), e);
            }

            playerImage.setText(" " + value.name());
            playerImage.setFont(list.getFont().deriveFont(Font.BOLD, 18f));
            playerImage.setPreferredSize(new Dimension(0, 40));
            playerImage.setBackground(GUIColors.PlayerComponent.LIST_ITEM_BACKGROUND);
            playerImage.setForeground(GUIColors.PlayerComponent.LIST_FOREGROUND);
            playerImage.setOpaque(true);
            playerImage.setBorder(new EmptyBorder(5, 10, 5, 10));

            playerCache.put(value.name(), playerImage);
            return playerImage;
        }
    }
}
