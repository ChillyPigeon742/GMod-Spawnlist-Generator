package net.alek.spawnlistgenerator.data;

import net.alek.spawnlistgenerator.core.Logger;
import net.alek.spawnlistgenerator.util.RegistryReader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AFileChooser extends JFileChooser {

    private final JCheckBox exportToGModCheckbox = new JCheckBox("Export to GMod?");
    private File lastDirectory;

    public AFileChooser() {
        exportToGModCheckbox.setSelected(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                requestFocus();
            }
        });

        exportToGModCheckbox.addActionListener(e -> {
            if (exportToGModCheckbox.isSelected()) {
                lastDirectory = getCurrentDirectory();
                Path gmodDir = Paths.get(RegistryReader.installPath + "\\garrysmod\\settings\\spawnlist");
                if (gmodDir.toFile().exists()) {
                    setCurrentDirectory(gmodDir.toFile());
                }
            } else {
                setCurrentDirectory(lastDirectory);
            }
        });

        SwingUtilities.invokeLater(this::setupCustomUI);
    }

    private void setupCustomUI() {
        JPanel fileTypePanel = findFileTypePanel(this);
        if (fileTypePanel == null) {
            Logger.Log.info("Couldn't find 'Files of Type' panel!");
            return;
        }

        JLabel label = null;
        JComboBox<?> oldCombo = null;

        for (Component comp : fileTypePanel.getComponents()) {
            if (comp instanceof JLabel l && l.getText() != null && l.getText().contains("Files of Type")) {
                label = l;
            } else if (comp instanceof JComboBox<?>) {
                oldCombo = (JComboBox<?>) comp;
            }
        }

        if (label == null) {
            Logger.Log.info("Couldn't find 'Files of Type' label!");
            return;
        }

        JComboBox<FileFilter> filterCombo = createFilterComboBox();

        JPanel layoutPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 0, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        layoutPanel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        layoutPanel.add(filterCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        layoutPanel.add(exportToGModCheckbox, gbc);

        if (oldCombo != null) fileTypePanel.remove(oldCombo);
        fileTypePanel.removeAll();
        fileTypePanel.setLayout(new BorderLayout());
        fileTypePanel.add(layoutPanel, BorderLayout.CENTER);
        fileTypePanel.revalidate();
        fileTypePanel.repaint();
    }

    private JComboBox<FileFilter> createFilterComboBox() {
        DefaultComboBoxModel<FileFilter> model = new DefaultComboBoxModel<>();
        for (FileFilter filter : getChoosableFileFilters()) model.addElement(filter);

        JComboBox<FileFilter> comboBox = new JComboBox<>(model);
        comboBox.setSelectedItem(getFileFilter());
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboBox.getPreferredSize().height));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof FileFilter f ? f.getDescription() : String.valueOf(value));
                return this;
            }
        });

        comboBox.addActionListener(e -> {
            FileFilter selected = (FileFilter) comboBox.getSelectedItem();
            if (selected != null) setFileFilter(selected);
        });

        return comboBox;
    }

    private JPanel findFileTypePanel(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel panel) {
                boolean hasLabel = false, hasCombo = false;

                for (Component child : panel.getComponents()) {
                    if (child instanceof JLabel label && label.getText() != null && label.getText().contains("Files of Type")) {
                        hasLabel = true;
                    } else if (child instanceof JComboBox<?>) {
                        hasCombo = true;
                    }
                }

                if (hasLabel && hasCombo) return panel;

                JPanel nested = findFileTypePanel(panel);
                if (nested != null) return nested;
            }
        }
        return null;
    }
}