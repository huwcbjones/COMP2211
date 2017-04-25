package t16.controller;

import com.sun.prism.j2d.print.J2DPrinter;
import com.sun.prism.j2d.print.J2DPrinterJob;
import javafx.embed.swing.SwingFXUtils;
import javafx.print.*;
import javafx.scene.SnapshotParameters;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import t16.components.dialogs.ExceptionDialog;
import t16.components.dialogs.InfoDialog;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Created by James on 19-Apr-17.
 */
public class Export
{
    private Pane node;

    private FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Portable Network Graphic file", "*.png");

    //<editor-fold desc="View Controls">
    @FXML
    private Button saveScreenButton;

    @FXML
    private Button printScreenButton;

    @FXML
    private Button cancelButton;
    //</editor-fold>

    @FXML
    public void initialize() {}

    //<editor-fold desc="View Methods">
    @FXML
    private void saveScreen(ActionEvent event) {
        File file = this.browseFile(event);
        try
        {
            //Awkwardly get extension to save as
            String name = file.getName();
            String[] fileNameParts = name.split("\\.");
            String ext = fileNameParts[fileNameParts.length - 1];
            if(!ext.equals("png"))
            {
                file.renameTo(new File(name + ".png"));
            }
            ImageIO.write(SwingFXUtils.fromFXImage(this.node.snapshot(new SnapshotParameters(), null), null), "png", file);
            InfoDialog id = new InfoDialog("Success", "Screenshot was saved in "+file.getAbsolutePath()+".");
            id.showAndWait();
        }
        catch(IOException ioe)
        {
            ExceptionDialog ed = new ExceptionDialog("Screenshot Save Failed", "Failed to save the screenshot image.\n" +
                    "The save may have been interrupted, or you do not have permission to save the file here.", ioe);
            ed.showAndWait();
        }
    }

    private File browseFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Screenshot");
        fc.getExtensionFilters().add(this.imageFilter);

        return fc.showSaveDialog(((Control) event.getSource()).getScene().getWindow());
    }

    /**
     * Prints out the Pane, fitted to landscape A4 paper.
     */
    @FXML
    private void printScreen(ActionEvent event) {
        //Fit to landscape A4
        ImageView imageView =new ImageView(this.node.snapshot(new SnapshotParameters(), null));
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
        double scaleX = pageLayout.getPrintableWidth() / imageView.getBoundsInParent().getWidth();
        double scaleY = pageLayout.getPrintableHeight() / imageView.getBoundsInParent().getHeight();
        imageView.getTransforms().add(new Scale(scaleX, scaleY));


        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            InfoDialog id = new InfoDialog("Failed to print", "No printers were detected.");
            id.showAndWait();
        } else {
            if (job.showPrintDialog(this.node.getScene().getWindow())) {
                if (job.printPage(pageLayout, imageView)) {
                    job.endJob();
                    InfoDialog id = new InfoDialog("Success", "Print job started successfully.");
                    id.showAndWait();
                }
                else
                {
                    InfoDialog id = new InfoDialog("Failed to print", "Couldn't print page. Please check your paper and ink.");
                    id.showAndWait();
                }
            }
            else
            {
                InfoDialog id = new InfoDialog("Failed to print", "Print was cancelled.");
                id.showAndWait();
            }
        }
    }
    //</editor-fold>

    @FXML
    private void cancelButtonAction(ActionEvent event) {
        ((Stage) ((Control) event.getSource()).getScene().getWindow()).close();
    }

    public void setNodeToPrint(Pane node)
    {
        this.node = node;
    }
}
