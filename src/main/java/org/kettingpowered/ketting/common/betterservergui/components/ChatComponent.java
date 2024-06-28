package org.kettingpowered.ketting.common.betterservergui.components;

import org.kettingpowered.ketting.common.betterservergui.BetterServerGUI;
import org.kettingpowered.ketting.common.betterservergui.GUIColors;
import org.kettingpowered.ketting.common.betterservergui.components.chat.LoggerDocument;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ChatComponent extends JPanel {

    private final BetterServerGUI instance;
    private final JTextPane textPane;

    private final List<String> commandHistory = new ArrayList<>();
    private int commandHistoryIndex = 0;

    public ChatComponent(BetterServerGUI instance) {
        super();
        this.instance = instance;
        setLayout(new BorderLayout());

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(instance.getSettings().font.deriveFont(Font.BOLD));
        JScrollPane scrollPane = new JScrollPane(textPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JTextField textField = new JTextField();
        textField.setFont(instance.getSettings().font);
        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String command = textField.getText();
                    textField.setText("");

                    if (!command.isBlank())
                        sendCommand(command);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (commandHistoryIndex > 0) {
                        commandHistoryIndex--;
                        textField.setText(commandHistory.get(commandHistoryIndex));
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (commandHistoryIndex < commandHistory.size() - 1) {
                        commandHistoryIndex++;
                        textField.setText(commandHistory.get(commandHistoryIndex));
                    } else {
                        commandHistoryIndex = commandHistory.size();
                        textField.setText("");
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    textField.setText("");
                }
            }
        });

        setBackground(GUIColors.ChatComponent.BACKGROUND);
        setForeground(GUIColors.ChatComponent.FOREGROUND);

        textPane.setBackground(GUIColors.ChatComponent.CONSOLE_BACKGROUND);
        textPane.setForeground(GUIColors.ChatComponent.CONSOLE_FOREGROUND);
        scrollPane.setBackground(textPane.getBackground());
        scrollPane.setForeground(textPane.getForeground());

        textField.setBackground(GUIColors.ChatComponent.INPUT_BACKGROUND);
        textField.setForeground(GUIColors.ChatComponent.INPUT_FOREGROUND);
        textField.setCaretColor(textField.getForeground());

        TitledBorder border = new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, GUIColors.ChatComponent.BORDER, GUIColors.ChatComponent.BORDER_SHADOW), "Chat");
        border.setTitleFont(instance.getSettings().font);
        border.setTitleColor(GUIColors.ChatComponent.BORDER);
        setBorder(border);

        add(scrollPane, BorderLayout.CENTER);
        add(textField, BorderLayout.SOUTH);
    }

    private static final java.util.regex.Pattern ANSI = java.util.regex.Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})*)?[m|K]");
    public void print(String message) {
        if (instance.isClosing()) return;
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.print(message));
        } else {
            LoggerDocument document = LoggerDocument.create(textPane.getStyledDocument());
            try {
                document.insertString(document.getLength(), ANSI.matcher(message).replaceAll(""), null);
            } catch (BadLocationException ignored) {}
        }
    }

    public void sendCommand(String command) {
        commandHistory.add(command);
        commandHistoryIndex = commandHistory.size();

        instance.getSettings().adapter.onCommand(command);
    }
}
