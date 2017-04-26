package t16.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Window;
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
public class Export {
    private static final FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("PNG Image", "*.png");
    private static final FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF", "*.pdf");


    public static void saveChart(Window w, Node node) {
        File file = browseFile(w, imageFilter);
        if (file == null) return;
        try {
            //Awkwardly get extension to save as
            String[] fileNameParts = file.getName().split("\\.");
            String ext = fileNameParts[fileNameParts.length - 1];
            ImageIO.write(SwingFXUtils.fromFXImage(node.snapshot(new SnapshotParameters(), null), null), ext, file);
            InfoDialog id = new InfoDialog("Success", "Screenshot was saved in " + file.getAbsolutePath() + ".");
            id.showAndWait();
        } catch (IOException ioe) {
            ExceptionDialog ed = new ExceptionDialog("Screenshot Save Failed", "Failed to save the screenshot image.\n" +
                    "The save may have been interrupted, or you do not have permission to save the file here.", ioe);
            ed.showAndWait();
        }
    }

    public static void savePDF(Window w, Node node) {
        File file = browseFile(w, pdfFilter);
        try {
            Document doc = new Document();
            FileOutputStream fos = new FileOutputStream(file);
            PdfWriter pdfw = PdfWriter.getInstance(doc, fos);
            pdfw.open();
            doc.open();
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(node.snapshot(new SnapshotParameters(), null), null), "png", byteOutput);
            doc.add(com.itextpdf.text.Image.getInstance(byteOutput.toByteArray()));
            doc.close();
            byteOutput.close();
            pdfw.close();
            fos.close();
            InfoDialog id = new InfoDialog("Success", "Screenshot was saved in " + file.getAbsolutePath() + ".");
            id.showAndWait();
        } catch (DocumentException de) {
            ExceptionDialog ed = new ExceptionDialog("Screenshot Save Failed", "Failed to open the .pdf file for writing.", de);
            ed.showAndWait();
        } catch (IOException ioe) {
            ExceptionDialog ed = new ExceptionDialog("Screenshot Save Failed", "Failed to save the screenshot image.\n" +
                    "The save may have been interrupted, or you do not have permission to save the file here.", ioe);
            ed.showAndWait();
        }
    }

    protected static File browseFile(Window w, FileChooser.ExtensionFilter filter) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Screenshot");
        fc.getExtensionFilters().add(filter);
        return fc.showSaveDialog(w);
    }

    public static void printChart(Window w, Node node) {
        //Fit to landscape A4
        ImageView imageView = new ImageView(node.snapshot(new SnapshotParameters(), null));
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
        double scaleX = pageLayout.getPrintableWidth() / imageView.getBoundsInParent().getWidth();
        double scaleY = pageLayout.getPrintableHeight() / imageView.getBoundsInParent().getHeight();
        imageView.getTransforms().add(new Scale(scaleX, scaleY));

        PrinterJob job = PrinterJob.createPrinterJob();

        if (job == null) {
            InfoDialog id = new InfoDialog("Failed to print", "No printers were detected.");
            id.showAndWait();
            return;
        }

        if (!job.showPrintDialog(w)) {
            return;
        }

        if (!job.printPage(pageLayout, imageView)) {
            InfoDialog id = new InfoDialog("Failed to print", "Couldn't print page. Please check your paper and ink.");
            id.showAndWait();
            return;
        }

        job.endJob();
        InfoDialog id = new InfoDialog("Success", "Print job started successfully.");
        id.showAndWait();

    }
}
