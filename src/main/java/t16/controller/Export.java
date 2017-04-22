package t16.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by James on 19-Apr-17.
 */
public class Export
{
    private Pane node;

    private final FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.png", "*.bmp");
    private final FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF files", "*.pdf");

    //<editor-fold desc="View Controls">
    @FXML
    private Button saveScreenButton;

    @FXML
    private Button pdfScreenButton;

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
        File file = this.browseFile(event, this.imageFilter);
        try
        {
            //Awkwardly get extension to save as
            String[] fileNameParts = file.getName().split("\\.");
            String ext = fileNameParts[fileNameParts.length - 1];
            ImageIO.write(SwingFXUtils.fromFXImage(this.node.snapshot(new SnapshotParameters(), null), null), ext, file);
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

    @FXML
    private void pdfScreen(ActionEvent event) {
        File file = this.browseFile(event, this.pdfFilter);
        try
        {
            Document doc = new Document();
            FileOutputStream fos = new FileOutputStream(file);
            PdfWriter pdfw = PdfWriter.getInstance(doc, fos);
            pdfw.open();
            doc.open();
            ByteArrayOutputStream  byteOutput = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(this.node.snapshot(new SnapshotParameters(), null), null), "png", byteOutput);
            doc.add(com.itextpdf.text.Image.getInstance(byteOutput.toByteArray()));
            doc.close();
            byteOutput.close();
            pdfw.close();
            fos.close();
            InfoDialog id = new InfoDialog("Success", "Screenshot was saved in "+file.getAbsolutePath()+".");
            id.showAndWait();
        }
        catch(DocumentException de)
        {
            ExceptionDialog ed = new ExceptionDialog("Screenshot Save Failed", "Failed to open the .pdf file for writing.", de);
            ed.showAndWait();
        }
        catch(IOException ioe)
        {
            ExceptionDialog ed = new ExceptionDialog("Screenshot Save Failed", "Failed to save the screenshot image.\n" +
                    "The save may have been interrupted, or you do not have permission to save the file here.", ioe);
            ed.showAndWait();
        }
    }

    private File browseFile(ActionEvent event, FileChooser.ExtensionFilter filter) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Screenshot");
        fc.getExtensionFilters().add(filter);

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
