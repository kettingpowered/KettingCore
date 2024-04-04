package org.kettingpowered.ketting.common.betterservergui.components.chat;

import org.kettingpowered.ketting.common.betterservergui.GUIColors;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggerDocument implements Document {

    public static LoggerDocument instance;

    public static LoggerDocument create(Document origin) {
        if (instance == null || instance.origin != origin)
            instance = new LoggerDocument(origin);

        return instance;
    }

    private final Document origin;

    public LoggerDocument(Document origin) {
        this.origin = origin;
    }

    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        origin.insertString(offset, str, determineLogColor(str));
    }

    private AttributeSet determineLogColor(String str) {
        //Example chat message: [12:53:54] [Server thread/INFO] [/]: Hello, world!
        //We are grabbing the INFO part of the message
        Matcher matcher = Pattern.compile("\\[.*?/(.*?)]").matcher(str);
        return getLogLevelColor(matcher.find() ? matcher.group(1) : "default");
    }

    private AttributeSet getLogLevelColor(String logLevel) {
        Color col = switch (logLevel) {
            case "INFO" -> GUIColors.ChatComponent.ChatColors.INFO;
            case "WARN" -> GUIColors.ChatComponent.ChatColors.WARN;
            case "ERROR" -> GUIColors.ChatComponent.ChatColors.ERROR;
            case "FATAL" -> GUIColors.ChatComponent.ChatColors.FATAL;
            default -> GUIColors.ChatComponent.ChatColors.DEFAULT;
        };

        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setForeground(set, col);
        return set;
    }

    //Redirect methods to origin
    public int getLength() {
        return origin.getLength();
    }

    public void addDocumentListener(DocumentListener listener) {
        origin.addDocumentListener(listener);
    }

    public void removeDocumentListener(DocumentListener listener) {
        origin.removeDocumentListener(listener);
    }

    public void addUndoableEditListener(UndoableEditListener listener) {
        origin.addUndoableEditListener(listener);
    }

    public void removeUndoableEditListener(UndoableEditListener listener) {
        origin.removeUndoableEditListener(listener);
    }

    public Object getProperty(Object key) {
        return origin.getProperty(key);
    }

    public void putProperty(Object key, Object value) {
        origin.putProperty(key, value);
    }

    public void remove(int offs, int len) throws BadLocationException {
        origin.remove(offs, len);
    }

    public String getText(int offset, int length) throws BadLocationException {
        return origin.getText(offset, length);
    }

    public void getText(int offset, int length, Segment txt) throws BadLocationException {
        origin.getText(offset, length, txt);
    }

    public Position getStartPosition() {
        return origin.getStartPosition();
    }

    public Position getEndPosition() {
        return origin.getEndPosition();
    }

    public Position createPosition(int offs) throws BadLocationException {
        return origin.createPosition(offs);
    }

    public Element[] getRootElements() {
        return origin.getRootElements();
    }

    public Element getDefaultRootElement() {
        return origin.getDefaultRootElement();
    }

    public void render(Runnable r) {
        origin.render(r);
    }
}
