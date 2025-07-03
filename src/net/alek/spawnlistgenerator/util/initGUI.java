package net.alek.spawnlistgenerator.util;

import net.alek.spawnlistgenerator.core.ErrorHandler;
import net.alek.spawnlistgenerator.core.SpawnlistHandler;
import net.alek.spawnlistgenerator.data.AFileChooser;
import net.alek.spawnlistgenerator.data.EntryConsumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class initGUI {
    public static JFrame window = new JFrame();

    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);
    private static final Font DRAG_LABEL_FONT = new Font("SansSerif", Font.BOLD, 16);

    public static final JTextField nameField = new JTextField("Spawnlist");
    public static final JTextField idField = new JTextField("46");
    public static final JTextField iconField = new JTextField("icon16/page.png");
    public static final JTextField parentIdField = new JTextField("0");
    public static final JTextField gmodPathField = new JTextField("");

    public static DefaultTableModel tableModel;
    private static JTable modelTable;

    public static void setupGUI(){
        window.setTitle("GMod Spawnlist Creator");
        window.setSize(1280, 720);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout(15, 15));
        window.setIconImage(AssetsLoader.getAppIcon().getImage());

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(new Color(50,50,50));

        contentPanel.add(initGUI.buildTopPanel(), BorderLayout.NORTH);
        contentPanel.add(initGUI.buildTablePanel(), BorderLayout.CENTER);
        contentPanel.add(initGUI.buildButtonPanel(), BorderLayout.SOUTH);

        window.add(contentPanel);
        initGUI.configureDragAndDrop();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                window.requestFocus();
            }
        });
        window.getRootPane().setBackground(Color.BLACK);

        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Button.arc", 25);
    }

    private static DocumentFilter createNumberFilter() {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null && string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    public static JPanel buildTopPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(7, 3, 10, 10));
        inputPanel.setBackground(new Color(50,50,50));

        inputPanel.add(createLabel("📁 Spawnlist Name:"));
        inputPanel.add(nameField);

        inputPanel.add(createLabel("🖼️ Icon Path (In VPK/resource):"));
        inputPanel.add(iconField);

        inputPanel.add(createLabel("🆔 Spawnlist ID:"));
        ((AbstractDocument) idField.getDocument()).setDocumentFilter(createNumberFilter());
        inputPanel.add(idField);

        inputPanel.add(createLabel("📂 Parent ID:"));
        ((AbstractDocument) parentIdField.getDocument()).setDocumentFilter(createNumberFilter());
        inputPanel.add(parentIdField);

        inputPanel.add(new JSeparator());
        inputPanel.add(new JSeparator());

        inputPanel.add(createLabel("🔵 GMod Installation Path:"));

        JPanel gmodPathPanel = new JPanel(new BorderLayout(5, 0));
        gmodPathPanel.setBackground(new Color(50,50,50));
        gmodPathPanel.add(gmodPathField, BorderLayout.CENTER);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Select Garry's Mod Installation Folder");

            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                gmodPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        gmodPathPanel.add(browseButton, BorderLayout.EAST);
        inputPanel.add(gmodPathPanel);

        JLabel dragLabel = new JLabel("Drag & Drop .mdl Files or Folders Here", SwingConstants.CENTER);
        dragLabel.setFont(DRAG_LABEL_FONT);
        dragLabel.setForeground(new Color(60, 120, 200));
        dragLabel.setBorder(BorderFactory.createDashedBorder(new Color(120, 120, 120), 2, 5));
        dragLabel.setPreferredSize(new Dimension(800, 50));
        dragLabel.setOpaque(true);
        dragLabel.setBackground(new Color(245, 245, 245));
        dragLabel.setName("dragLabel");

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(50,50,50));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(dragLabel, BorderLayout.SOUTH);

        return topPanel;
    }

    public static JScrollPane buildTablePanel() {

        tableModel = new DefaultTableModel(new String[]{"Model File (.mdl)", "Header"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelTable = new JTable(tableModel);
        modelTable.setAutoCreateRowSorter(true);
        modelTable.setRowHeight(24);
        modelTable.getTableHeader().setFont(HEADER_FONT);

        return new JScrollPane(modelTable);
    }

    public static JPanel buildButtonPanel() {
        JButton generateBtn = new JButton("⚙️ Generate");
        JButton newEntryBtn = new JButton("➕ New Entry");
        JButton deleteEntryBtn = new JButton("❌ Delete");
        JButton editModelBtn = new JButton("✏️ Edit Model Path");
        JButton editHeaderBtn = new JButton("✏️ Edit Header");

        generateBtn.addMouseListener(AudioUtil.buttonSounds());
        newEntryBtn.addMouseListener(AudioUtil.buttonSounds());
        deleteEntryBtn.addMouseListener(AudioUtil.buttonSounds());
        editModelBtn.addMouseListener(AudioUtil.buttonSounds());
        editHeaderBtn.addMouseListener(AudioUtil.buttonSounds());

        deleteEntryBtn.setEnabled(false);
        editModelBtn.setEnabled(false);
        editHeaderBtn.setEnabled(false);

        modelTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = modelTable.getSelectedRowCount() > 0;
            deleteEntryBtn.setEnabled(hasSelection);
            editModelBtn.setEnabled(hasSelection);
            editHeaderBtn.setEnabled(hasSelection);
        });

        newEntryBtn.addActionListener(e -> showEntryDialog((model, header) ->
                tableModel.addRow(new Object[]{model, header})));

        deleteEntryBtn.addActionListener(e -> {
            int[] selectedRows = modelTable.getSelectedRows();
            Arrays.sort(selectedRows);
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                tableModel.removeRow(modelTable.convertRowIndexToModel(selectedRows[i]));
            }
        });

        editModelBtn.addActionListener(e -> editSelectedColumn(0, "Enter new Model Path:"));
        editHeaderBtn.addActionListener(e -> editSelectedColumn(1, "Enter new Header:"));
        generateBtn.addActionListener(e -> SpawnlistHandler.generateSpawnlist());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftPanel.setBackground(new Color(50,50,50));
        leftPanel.add(newEntryBtn);
        leftPanel.add(deleteEntryBtn);
        leftPanel.add(editModelBtn);
        leftPanel.add(editHeaderBtn);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(new Color(50,50,50));
        rightPanel.add(generateBtn);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(50,50,50));
        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);

        return buttonPanel;
    }

    public static void configureDragAndDrop() {
        Component dragLabel = findComponentByName(window, "dragLabel");
        if (dragLabel != null) {
            new DropTarget(dragLabel, new DropTargetAdapter() {
                @Override
                public void drop(DropTargetDropEvent dropTargetDropEvent) {
                    try {
                        dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY);
                        Object data = dropTargetDropEvent.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        if (data instanceof java.util.List<?> list) {
                            boolean allFiles = list.stream().allMatch(item -> item instanceof File);
                            if (allFiles) {
                                for (Object obj : list) {
                                    File file = (File) obj;

                                    if (file.isDirectory()) {
                                        SpawnlistHandler.addModelsFromFolderDeep(file);
                                    } else if (file.getName().toLowerCase().endsWith(".mdl")) {
                                        addModelEntry(file);
                                    }
                                }
                            }
                        }
                    } catch (UnsupportedFlavorException e) {
                        ErrorHandler.UnsupportedFlavorException();
                    } catch (IOException e) {
                        ErrorHandler.IOException();
                    }
                }
            });
        }
    }

    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        return label;
    }

    private static void showEntryDialog(EntryConsumer consumer) {
        new Thread(() -> {
            JTextField modelField = new JTextField();
            JTextField headerField = new JTextField();

            JPanel modelPanel = new JPanel(new BorderLayout(5, 0));
            modelPanel.add(modelField, BorderLayout.CENTER);

            JButton browseBtn = new JButton("Browse...");
            modelPanel.add(browseBtn, BorderLayout.EAST);

            browseBtn.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".mdl");
                    }

                    @Override
                    public String getDescription() {
                        return "Source Engine Models (*.mdl)";
                    }
                });
                if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                    modelField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            });

            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            panel.add(new JLabel("Model Path:"));
            panel.add(modelPanel);
            panel.add(new JLabel("Header:"));
            panel.add(headerField);

            JButton createButton = new JButton("Create");
            createButton.setEnabled(false);

            DocumentListener listener = new DocumentListener() {
                void update() {
                    createButton.setEnabled(!modelField.getText().trim().isEmpty());
                }

                public void insertUpdate(DocumentEvent e) { update(); }
                public void removeUpdate(DocumentEvent e) { update(); }
                public void changedUpdate(DocumentEvent e) { update(); }
            };

            modelField.getDocument().addDocumentListener(listener);
            headerField.getDocument().addDocumentListener(listener);

            JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION, null, new Object[]{createButton, "Cancel"}, createButton);

            JDialog dialog = optionPane.createDialog(window, "New Entry");
            dialog.setSize(500, 180);
            dialog.setResizable(true);

            dialog.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    dialog.requestFocus();
                }
            });

            createButton.addActionListener(e -> {
                dialog.dispose();
                String model = modelField.getText().trim();
                String header = headerField.getText().trim();
                if (!model.toLowerCase().endsWith(".mdl")) {
                    JOptionPane.showMessageDialog(window, "The selected file is not a .mdl file!",
                            "Invalid File", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                consumer.accept(model, header);
            });

            dialog.setVisible(true);
        }).start();
    }

    public static void addModelEntry(File file) {
        String modelPath = file.getAbsolutePath();
        String headerName = SpawnlistHandler.capitalize(file.getParentFile().getName());
        tableModel.addRow(new Object[]{modelPath, headerName});
    }

    private static void editSelectedColumn(int columnIndex, String message) {
        int[] selectedRows = modelTable.getSelectedRows();
        if (selectedRows.length == 0) return;

        String input = JOptionPane.showInputDialog(window, message);
        if (input != null && !input.trim().isEmpty()) {
            for (int row : selectedRows) {
                int modelIndex = modelTable.convertRowIndexToModel(row);
                tableModel.setValueAt(input.trim(), modelIndex, columnIndex);
            }
        }
    }

    private static Component findComponentByName(Container container, String name) {
        for (Component c : container.getComponents()) {
            if (name.equals(c.getName())) return c;
            if (c instanceof Container) {
                Component result = findComponentByName((Container) c, name);
                if (result != null) return result;
            }
        }
        return null;
    }

    public static File showSpawnlistSaveDialog() {
        AFileChooser chooser = new AFileChooser();
        chooser.setDialogTitle("Save Spawnlist");
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Text Documents (*.txt)";
            }
        });
        String idText = initGUI.idField.getText();
        String fileName = (Integer.parseInt(idText) >= 100 ? idText : "0" + idText) + "-" + initGUI.nameField.getText().toLowerCase() + ".txt";
        chooser.setSelectedFile(new File(fileName));

        int result = chooser.showSaveDialog(initGUI.window);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }

        return null;
    }
}
