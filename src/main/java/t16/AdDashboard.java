package t16;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import t16.components.WorkerPool;
import t16.controller.DataController;
import t16.utils.RunnableAdapter;

/**
 * Ad Dashboard Application
 *
 * @author Huw Jones
 * @since 21/02/2017
 */
public class AdDashboard extends Application {
    protected static final Logger log = LogManager.getLogger(AdDashboard.class);

    private static AdDashboard application;
    private static DataController dataController;
    private static WorkerPool workerPool = null;

    public AdDashboard() {
        super();
        application = this;
    }

    public static void main(String[] args) {
        log.info("Welcome to Ad Dashboard!");
        log.info("Launching application...");
        launch(args);
    }

    /**
     * Dispatches an event handler in the EDT (any worker pool thread).
     * Or, if the server isn't running, just run the event handler in the current thread.
     *
     * @param event Event to dispatch
     */
    public static void dispatchEvent(RunnableAdapter event) {
        log.trace("Dispatching event");
        if (workerPool.isRunning()) {
            workerPool.dispatchEvent(event);
        } else {
            event.run();
        }
    }

    /**
     * Gets the Database instance from a static context
     *
     * @return Database instance
     */
    public static DataController getDataController() {
        return dataController;
    }

    /**
     * Gets the Application instance from a static context
     *
     * @return Application instance
     */
    public static AdDashboard getApplication() {
        return application;
    }

    /**
     * Gets the WorkerPool instance from a static context
     *
     * @return WorkerPool instance
     */
    public static WorkerPool getWorkerPool() {
        return AdDashboard.workerPool;
    }

    /**
     * The application initialization method. This method is called immediately
     * after the Application class is loaded and constructed. An application may
     * override this method to perform initialization prior to the actual starting
     * of the application.
     */
    @Override
    public void init() throws Exception {
        log.info("Initialising application");

        log.info("Initialising dataController...");
        dataController = new DataController();

        log.info("Initialising worker pool...");
        workerPool = new WorkerPool();

        log.info("Initialising complete!");
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting application...");

        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("Ad Dashboard");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        log.info("Start complete!");
    }

    /**
     * This method is called when the application should stop, and provides a
     * convenient place to prepare for application exit and destroy resources.
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     */
    @Override
    public void stop() throws Exception {
        log.info("Stopping application...");

        log.info("Stopping data controller...");
        dataController.shutdown();

        log.info("Stopping worker pool...");
        if (workerPool != null) {
            workerPool.shutdown();
        }

        log.info("Application ready to stop!");
    }
}
