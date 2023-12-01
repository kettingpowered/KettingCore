package org.kettingpowered.ketting.common.betterservergui;

import org.jetbrains.annotations.CheckReturnValue;
import org.kettingpowered.ketting.adapter.BetterServerGUIAdapter;
import org.kettingpowered.ketting.common.betterservergui.components.ChatComponent;
import org.kettingpowered.ketting.common.betterservergui.components.PlayerListComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public final class BetterServerGUI extends JComponent {
    public static class Builder {
        public Font font = new Font("Monospaced", Font.PLAIN, 12);
        public Image icon = new ImageIcon(BetterServerGUI.class.getResource("/icon.png")).getImage();
        private String windowTitle = "Minecraft server";
        private String shutdownTitle = "Minecraft server - shutting down!";
        public BetterServerGUIAdapter adapter;
        private final List<Runnable> finalizers = new ArrayList<>();

        @CheckReturnValue
        public Builder setFont(Font font) {
            this.font = font;
            return this;
        }

        @CheckReturnValue
        public Builder setIcon(Image icon) {
            this.icon = icon;
            return this;
        }

        @CheckReturnValue
        public Builder setWindowTitle(String windowTitle) {
            this.windowTitle = windowTitle;
            return this;
        }

        @CheckReturnValue
        public Builder setShutdownTitle(String shutdownTitle) {
            this.shutdownTitle = shutdownTitle;
            return this;
        }

        @CheckReturnValue
        public Builder setAdapter(BetterServerGUIAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder addFinalizer(Runnable finalizer) {
            this.finalizers.add(finalizer);
            return this;
        }

        public BetterServerGUI build() {
            if (adapter == null) {
                throw new IllegalStateException("adapter cannot be null");
            }
            return new BetterServerGUI(this);
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(BetterServerGUI.class);
    private final Builder settings;
    private final AtomicBoolean isClosing = new AtomicBoolean();

    private final JFrame frame;
    private ChatComponent chatComponent;
    private PlayerListComponent playerListComponent;

    private BetterServerGUI(Builder builder) {
        this.settings = builder;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.warn("Failed to set system look and feel", e);
        }

        createComponents();

        frame = new JFrame(settings.windowTitle);
        if (settings.icon != null)
            frame.setIconImage(settings.icon);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        settings.addFinalizer(frame::dispose);
        start();
    }

    private void createComponents() {
        setPreferredSize(new Dimension(854, 480));
        setLayout(new BorderLayout());

        chatComponent = new ChatComponent(this);
        add(chatComponent, BorderLayout.CENTER);

        playerListComponent = new PlayerListComponent(this);
        add(playerListComponent, BorderLayout.EAST);

        settings.adapter.addComponents(this);
        setVisible(true);
    }

    private final CountDownLatch readyLatch = new CountDownLatch(1);
    private void start() {
        settings.adapter.onStart();
        readyLatch.countDown();
        reload();
    }

    private int tickCount;
    public void tick() {
        if (tickCount++ % 20 == 0) {
            playerListComponent.updatePlayers(settings);
        }
    }

    public void print(String message) {
        try {
            readyLatch.await();
        } catch (InterruptedException ignored) {}
        chatComponent.print(message);
    }

    public void reload() {
        frame.setTitle(settings.windowTitle);
        playerListComponent.reload();
    }

    public void close() {
        if (!isClosing.getAndSet(true)) {
            frame.setTitle(settings.shutdownTitle);
            settings.adapter.onWindowClosing();
            runFinalizers();
        }
    }

    private void runFinalizers() {
        settings.finalizers.forEach(Runnable::run);
    }

    public Builder getSettings() {
        return settings;
    }

    public ChatComponent getChat() {
        return chatComponent;
    }

    public PlayerListComponent getPlayerList() {
        return playerListComponent;
    }
}
