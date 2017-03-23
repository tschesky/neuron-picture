package pl.agh.edu.to.neuronpicture.analizer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.*;
import java.text.*;


public class Pane extends JPanel{

    private Vector<Object> data = new Vector<>(3, 2);
    private Vector<String> paths = new Vector<>(3, 2);
    private Vector<String> columnNames = new Vector<>();
    private JTextField catalogPathField;
    private JButton importPictures;
    private JButton crawlButton;
    private JButton getDescription;
    private JButton open;
    private JButton open2;
    private JFileChooser fc;
    private DefaultTableModel model;
    private JTable table;
    private JFormattedTextField concurrencyField;
    private JFormattedTextField maxField;
    private NumberFormat concurenncyFormat;
    private JPanel picturesImportPanel;
    private JCheckBox isPNG;
    private JCheckBox isJPG;
    private JCheckBox isBMP;
    private JCheckBox isGIF;
    private JLabel widthLabel;
    private JLabel heightLabel;
    private JLabel saveDirectory;
    private JTextField savePath;
    private JFormattedTextField widthField;
    private JFormattedTextField heightField;


    Pane() {
        // Set pane look and feel
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        picturesImportPanel = new JPanel();
        picturesImportPanel.setLayout(new GridBagLayout());
        picturesImportPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        // Populate the elements
        fc = new JFileChooser();
        catalogPathField = new JTextField(50);
        catalogPathField.setMinimumSize(new Dimension(460, 20));
        savePath = new JTextField(50);
        savePath.setMinimumSize(new Dimension(300, 20));
        JLabel catalogLabel = new JLabel("Catalog/URL ");
        catalogLabel.setMinimumSize(new Dimension(150, 20));
        catalogLabel.setLabelFor(catalogPathField);
        importPictures = new JButton("Import pictures");
        crawlButton = new JButton("Crawl!");
        concurenncyFormat = NumberFormat.getNumberInstance();
        concurrencyField = new JFormattedTextField(concurenncyFormat);
        maxField = new JFormattedTextField(concurenncyFormat);
        JLabel concurrencyLabel = new JLabel("Conc. level");
        JLabel maxPicLabel = new JLabel("       Max. pic.");
        open = new JButton("Choose a folder...");
        open.setMinimumSize(new Dimension(150, 20));
        open2 = new JButton("Choose a folder...");
        open2.setMinimumSize(new Dimension(150, 20));
        getDescription = new JButton("Analyze!");
        isJPG = new JCheckBox("JPG");
        isPNG = new JCheckBox("PNG");
        isBMP = new JCheckBox("BMP");
        isGIF = new JCheckBox("GIF");
        heightLabel = new JLabel("Height");
        widthLabel = new JLabel("Width");
        saveDirectory = new JLabel("Save pictures in:");
        heightField = new JFormattedTextField(concurenncyFormat);
        widthField = new JFormattedTextField(concurenncyFormat);

        // Add the import elements
        picturesImportPanel.add(catalogLabel, getConstraints(0,0,2));
        picturesImportPanel.add(catalogPathField, getConstraints(2,0,6));
        picturesImportPanel.add(open, getConstraints(8,0,2));
        picturesImportPanel.add(importPictures, getConstraints(0,1,10));

        // Filler
        picturesImportPanel.add(new Box.Filler(new Dimension(10,40), new Dimension(10,30), new Dimension(10,100)), getConstraints(0,2,10));

        // Add the crawler components
        picturesImportPanel.add(concurrencyLabel, getConstraints(0,3,2));
        picturesImportPanel.add(concurrencyField, getConstraints(2,3,2));
        picturesImportPanel.add(maxPicLabel, getConstraints(4,3,2));
        picturesImportPanel.add(maxField, getConstraints(6,3,2));
        picturesImportPanel.add(crawlButton, getConstraints(8,3,2));


        picturesImportPanel.add(isBMP, getConstraints(0,4,1));
        picturesImportPanel.add(isPNG, getConstraints(1,4,1));
        picturesImportPanel.add(isJPG, getConstraints(2,4,1));
        picturesImportPanel.add(isGIF, getConstraints(3,4,1));

        picturesImportPanel.add(widthLabel, getConstraints(5,4,1));
        picturesImportPanel.add(widthField, getConstraints(6,4,2));
        picturesImportPanel.add(heightLabel, getConstraints(8,4,1));
        picturesImportPanel.add(heightField, getConstraints(9,4,1));

        picturesImportPanel.add(saveDirectory, getConstraints(0,5,3));
        picturesImportPanel.add(savePath, getConstraints(2,5,3));
        picturesImportPanel.add(open2, getConstraints(5,5,3));

        // Initialize table data
        data = new Vector<>(3, 2);
        paths = new Vector<>(3, 2);
        columnNames = new Vector<>();
        columnNames.add("Picture");
        columnNames.add("Descritpion");

        // Override table model, so we can get ImageIcons on-click
        model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return ImageIcon.class;
                } else {
                    return Object.class;
                }
            }
        };
        table = new JTable(model);
        table.setRowHeight(100);
        JScrollPane tablePanel = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Add all the panels to the our panel
        this.add(picturesImportPanel, BorderLayout.PAGE_START);
        this.add(tablePanel, BorderLayout.CENTER);
        this.add(getDescription, BorderLayout.PAGE_END);
    }


    // Helper methods to reduce the amount of code
    private GridBagConstraints getConstraints(int gridx, int gridy, int gridwidth) {
        GridBagConstraints tmp = new GridBagConstraints();
        tmp.fill = GridBagConstraints.HORIZONTAL;
        tmp.gridx = gridx;
        tmp.gridy = gridy;
        tmp.gridwidth = gridwidth;
        return tmp;
    }

    // A S*tload of getters, so that the controller can take over commanding action listening

    Vector<String> getPaths() {
        return paths;
    }
    JTextField getCatalogPathField() {
        return catalogPathField;
    }
    JButton getImportPictures() {
        return importPictures;
    }
    JButton getCrawlButton() {
        return crawlButton;
    }
    JButton getGetDescription() {
        return getDescription;
    }
    JButton getOpen() {
        return open;
    }
    JFileChooser getFc() {
        return fc;
    }
    DefaultTableModel getModel() {
        return model;
    }
    JTable getTable() {
        return table;
    }
    JButton getOpen2() {return open2;}
    JTextField getSavePath(){return savePath;}
    int getConcurrencyFieldValue() {
        return ((Number)concurrencyField.getValue()).intValue();
    }
    int getMaxFieldValue() {
        return ((Number)maxField.getValue()).intValue();
    }

    int getWidthFieldValue() {
        return ((Number)widthField.getValue()).intValue();
    }

    int getHeightFieldValue() {
        return ((Number)heightField.getValue()).intValue();
    }

    public JPanel getPicturesImportPanel() {
        return picturesImportPanel;
    }
    boolean isPNG() {
        return isPNG.isSelected();
    }

    boolean isJPG() {
        return isJPG.isSelected();
    }

    boolean isBMP() {
        return isBMP.isSelected();
    }

    boolean isGIF() {
        return isGIF.isSelected();
    }
}
