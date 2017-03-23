package pl.agh.edu.to.neuronpicture.analizer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.UrlValidator;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image.ImageFormatFilter;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image.ImageSizeFilter;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.Crawler;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.MultithreadingCrawler;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.strategy.SingleDirectoryPersistenceStrategy;
import pl.agh.edu.to.neuronpicture.webcrawler.utils.Utils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.*;

public class Controller implements ActionListener{

    public static final Logger LOGGER = Logger.getAnonymousLogger();
    private static final String EXCEPTION_1 = "An exception was thrown";

    private Pane pane;
    private JLabel label;

    void createAndShowGui() {

        JFrame frame;

        // Set the look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            LOGGER.log(Level.SEVERE, EXCEPTION_1, e);
        }

        // Create the window for our app
        frame = new JFrame("Analyzer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(850,600);
        frame.setLocation(100, 100);

        // Create and add action listeners to our panel
        pane = new Pane();
        pane.getImportPictures().addActionListener(this);
        pane.getOpen().addActionListener(this);
        pane.getOpen2().addActionListener(this);
        pane.getCrawlButton().addActionListener(this);
        pane.getGetDescription().addActionListener(this);

        // Show the GUI
        frame.setLayout(new BorderLayout());
        frame.add(pane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    @Override
    // Perform actions when buttons in the GUI window are clicked
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == pane.getImportPictures()){
            if (Files.isDirectory(Paths.get(pane.getCatalogPathField().getText()))) {
                pane.getModel().setRowCount(0);
                LOGGER.info("Loading files from directory");
                File dir = new File(pane.getCatalogPathField().getText());
                File[] directoryListing = dir.listFiles();
                if (directoryListing != null) {
                    for (File file: directoryListing) {

                        if ("jpg".equals(FilenameUtils.getExtension(file.getAbsolutePath())) |
                                "png".equals(FilenameUtils.getExtension(file.getAbsolutePath()))) {
                            LOGGER.info("It's an image: \t" + file.getAbsolutePath());
                            Object[] row = {resizeIcon(new ImageIcon(file.getPath())), ""};
                            pane.getModel().addRow(row);
                            pane.getPaths().add(file.getAbsolutePath());
                            pane.getTable().repaint();
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "This is not a valid URL or directory path!", "Warning!",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if(e.getSource() == pane.getOpen()){
            pane.getFc().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            pane.getFc().setAcceptAllFileFilterUsed(false);
            pane.getFc().setCurrentDirectory(new java.io.File("."));
            if (pane.getFc().showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
                pane.getCatalogPathField().setText(pane.getFc().getSelectedFile().toString());
            }
        } else if(e.getSource() == pane.getOpen2()){
            pane.getFc().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            pane.getFc().setAcceptAllFileFilterUsed(false);
            pane.getFc().setCurrentDirectory(new java.io.File("."));
            if (pane.getFc().showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
                pane.getSavePath().setText(pane.getFc().getSelectedFile().toString());
            }
        } else if(e.getSource() == pane.getCrawlButton()){

            pane.getCrawlButton().setVisible(false);
            String stupidpath = Paths.get("").toAbsolutePath().resolve("src\\main\\resources").toString() + "\\animal.gif";
            System.out.println(stupidpath);
            Icon icon = new ImageIcon(stupidpath);
            label = new JLabel(icon);

            GridBagConstraints tmp = new GridBagConstraints();
            tmp.gridx = 8;
            tmp.gridy = 3;
            tmp.gridwidth = 2;

            pane.getPicturesImportPanel().add(label, tmp);
            pane.getPicturesImportPanel().revalidate();
            pane.getPicturesImportPanel().repaint();

            SwingWorker worker = new SwingWorker<String, Void>() {
                @Override
                public String doInBackground() {

                    if (UrlValidator.getInstance().isValid(pane.getCatalogPathField().getText())) {
                        try {
                            int concurrencyLevel = pane.getConcurrencyFieldValue();
                            java.util.List<URL> urls = parseUrls(Collections.singletonList(pane.getCatalogPathField().getText()));
                            ArrayList formats = new ArrayList();
                            if(pane.isPNG()){
                                formats.add("png");
                            }
                            if(pane.isBMP()){
                                formats.add("bmp");
                            }
                            if(pane.isGIF()){
                                formats.add("gif");
                            }
                            if(pane.isJPG()){
                                formats.add("jpg");
                            }

                            SingleDirectoryPersistenceStrategy strategy;
                            Path p1;

                            if(pane.getSavePath().getText() != null && !pane.getSavePath().getText().isEmpty()){
                                p1 = Paths.get(pane.getSavePath().getText());
                                strategy = new SingleDirectoryPersistenceStrategy(p1, false);
                            } else {
                                p1 = Paths.get("").toAbsolutePath().resolve("images");
                                strategy = new SingleDirectoryPersistenceStrategy();
                            }

                            Crawler crawler = new MultithreadingCrawler.Builder(strategy, urls)
                                    .concurrencyLevel(concurrencyLevel)
                                    .imageCount(pane.getMaxFieldValue())
                                    //.maxDepth(1)
                                    .imageFilters(new ImageFormatFilter(formats), new ImageSizeFilter(pane.getWidthFieldValue(), pane.getHeightFieldValue()))
                                    .build();
                            crawler.crawl().join();
                            crawler.dispose();
                            pane.getCatalogPathField().setText(p1.toString());
                        } catch (Exception exc) {
                            LOGGER.log(Level.SEVERE, EXCEPTION_1, exc);
                        }
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "This is not a valid URL or directory path!", "Warning!", JOptionPane.ERROR_MESSAGE);
                    }
                    return "";
                }

                @Override
                public void done() {
                    pane.getCrawlButton().setVisible(true);
                    label.setVisible(false);
                }
            };
            worker.execute();

        } else if(e.getSource() == pane.getGetDescription()){

            pane.remove(pane.getGetDescription());
            String stupidpath = Paths.get("").toAbsolutePath().resolve("src\\main\\resources").toString() + "\\134.gif";
            System.out.println(stupidpath);
            Icon icon = new ImageIcon(stupidpath);
            label = new JLabel(icon);

            pane.add(label, BorderLayout.PAGE_END);
            pane.revalidate();
            pane.repaint();


            SwingWorker worker = new SwingWorker<String, Void>() {
                @Override
                public String doInBackground() throws InterruptedException {
                    Thread.sleep(2000);
                    int index = pane.getTable().getSelectedRow();
                    Analizer analizer = Analizer.getInstance();
                    try {
                        String fullpath = pane.getPaths().get(index);
                        int p = fullpath.lastIndexOf('\\');
                        String path = fullpath.substring(0, p);
                        String name = fullpath.substring(p + 1);
                        String description = analizer.getDescriptionTest(new Image(Paths.get(path), name));
                        pane.getTable().setValueAt(description, index, 1);
                        pane.getTable().repaint();
                        return description;
                    } catch (IOException e1) {
                        LOGGER.log(Level.SEVERE, EXCEPTION_1, e1);
                    }
                    return "error";
                }

                @Override
                public void done() {
                    pane.add(pane.getGetDescription(), BorderLayout.PAGE_END);
                    pane.remove(label);
                    pane.revalidate();
                    pane.repaint();
                }
            };
            worker.execute();
        }
    }

    private ImageIcon resizeIcon(ImageIcon icon) {
        java.awt.Image image = icon.getImage();
        java.awt.Image resized = image.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
    }

    private java.util.List<URL> parseUrls(java.util.List<String> args) {
        return args.stream()
                .map(url -> Utils.toURLOpt(url, System.out::println))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
