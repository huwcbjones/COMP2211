package t16.components;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import t16.AdDashboard;
import t16.controller.DataController;
import t16.exceptions.ImportException;
import t16.model.ClickLog;
import t16.model.ImpressionLog;
import t16.model.ServerLog;
import t16.utils.AbortableCountDownLatch;

import java.io.File;
import java.util.List;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 06/03/2017
 */
public class Importer {
    protected static final Logger log = LogManager.getLogger(DataController.class);

    private File[] files;

    private File clickFile;
    private File impressionsFile;
    private File serverFile;

    private Task<List<ClickLog>> clickLogTask;
    private Task<List<ServerLog>> serverLogTask;
    private Task<List<ImpressionLog>> impressionLogTask;

    private Task<Void> clickInsertTask;
    private Task<Void> serverInsertTask;
    private Task<Void> impressionInsertTask;

    private List<ClickLog> clickLog;
    private List<ImpressionLog> impressionLog;
    private List<ServerLog> serverLog;

    private AbortableCountDownLatch parseLatch = new AbortableCountDownLatch(3);
    private AbortableCountDownLatch importLatch = new AbortableCountDownLatch(3);

    private Throwable exception = null;

    public Importer(File file1, File file2, File file3) {
        this.files = new File[]{file1, file2, file3};
    }

    public void parseAndImport() throws ImportException {
        log.debug("Checking environment...");
        if(AdDashboard.getWorkerPool() == null){
            throw new ImportException("Application not initialised, have you called AdDashboard::initialise()?");
        }

        log.debug("Identifying files...");
        for (File f : files) {
            Parser p = new Parser(f);
            try {
                switch (p.parseHeader()) {
                    case SERVER:
                        if (serverFile == null) {
                            serverFile = f;
                        } else {
                            throw new ImportException("Multiple server files provided.");
                        }
                        break;
                    case CLICK:
                        if (clickFile == null) {
                            clickFile = f;
                        } else {
                            throw new ImportException("Multiple click files provided.");
                        }
                        break;
                    case IMPRESSION:
                        if (impressionsFile == null) {
                            impressionsFile = f;
                        } else {
                            throw new ImportException("Multiple impression files provided.");
                        }
                        break;
                }
            } catch (ImportException e) {
                throw e;
            } catch (Exception e) {
                log.catching(e);
                throw new ImportException("File identification failed.", e);
            }
        }

        log.debug("Creating parse tasks...");
        createParseTasks();

        log.debug("Attaching parse handlers...");
        attachParseHandlers();

        log.debug("Starting parsers...");
        startParsers();

        try {
            log.debug("Waiting for parsers to complete...");
            parseLatch.await();
        } catch (AbortableCountDownLatch.AbortedException e) {
            log.warn("Parsing aborted.");
        } catch (InterruptedException e) {
            log.catching(e);
        }
        if (this.exception != null) {
            log.error("Exception during parsing!");
            log.catching(exception);
            stop();
            throw new ImportException("Error whilst parsing. Failed to import.", this.exception);
        }
        log.debug("All parsers complete!");

        try {
            log.debug("Waiting for inserters to complete...");
            importLatch.await();
        } catch (AbortableCountDownLatch.AbortedException e) {
            log.warn("Insertion aborted.");
        } catch (InterruptedException e) {
            log.catching(e);
        }
        if (this.exception != null) {
            log.error("Exception during inserting!");
            log.catching(exception);
            stop();
            throw new ImportException("Error whilst inserting. Failed to import.", this.exception);
        }
        log.debug("All inserters complete!");
    }

    private void createParseTasks() {
        clickLogTask = new Task<List<ClickLog>>() {
            @Override
            protected List<ClickLog> call() throws Exception {
                Parser parser = new Parser(clickFile);
                parser.parse();
                parseLatch.countDown();
                return parser.getClickLog();
            }
        };

        impressionLogTask = new Task<List<ImpressionLog>>() {
            @Override
            protected List<ImpressionLog> call() throws Exception {
                Parser parser = new Parser(impressionsFile);
                parser.parse();
                parseLatch.countDown();
                return parser.getImpressionLog();
            }
        };
        serverLogTask = new Task<List<ServerLog>>() {
            @Override
            protected List<ServerLog> call() throws Exception {
                Parser parser = new Parser(serverFile);
                parser.parse();
                parseLatch.countDown();
                return parser.getServerLog();
            }
        };
    }

    private void attachParseHandlers() {
        // Click Parser
        clickLogTask.setOnRunning(e -> log.trace("Click Parser running"));
        clickLogTask.setOnSucceeded(e -> {
            clickLog = (List<ClickLog>) e.getSource().getValue();
            createClickInsertTask();
            AdDashboard.getWorkerPool().queueTask(clickInsertTask);
        });
        // Click Handler
        clickLogTask.setOnFailed(e -> {
            exception = e.getSource().getException();
            parseLatch.abort();
        });

        // Impression Parser
        impressionLogTask.setOnRunning(e -> log.trace("Impression Parser running"));
        impressionLogTask.setOnSucceeded(e -> {
            impressionLog = (List<ImpressionLog>) e.getSource().getValue();
            createImpressionInsertTask();
            AdDashboard.getWorkerPool().queueTask(impressionInsertTask);
        });
        // Impression Handler
        impressionLogTask.setOnFailed(e -> {
            exception = e.getSource().getException();
            parseLatch.abort();
        });

        // Server Parser
        serverLogTask.setOnRunning(e -> log.trace("Server Parser running"));
        serverLogTask.setOnSucceeded(e -> {
            serverLog = (List<ServerLog>) e.getSource().getValue();
            createServerInsertTask();
            AdDashboard.getWorkerPool().queueTask(serverInsertTask);
        });
        // Server Handler
        serverLogTask.setOnFailed(e -> {
            exception = e.getSource().getException();
            parseLatch.abort();
        });
    }

    private void startParsers() {
        WorkerPool p = AdDashboard.getWorkerPool();
        p.queueTask(clickLogTask);
        p.queueTask(serverLogTask);
        p.queueTask(impressionLogTask);
    }

    public void stop() {
        log.warn("Stopping all tasks...");
        clickLogTask.cancel();
        serverLogTask.cancel();
        impressionLogTask.cancel();
        clickInsertTask.cancel();
        serverInsertTask.cancel();
        impressionInsertTask.cancel();
        log.info("All tasks stopped!");
    }

    private void createClickInsertTask() {
        clickInsertTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                AdDashboard.getDataController().insertClicks(clickLog);
                return null;
            }
        };
        clickInsertTask.setOnSucceeded(e1 -> {
            log.info("Finished INSERT -> Clicks");
            importLatch.countDown();
        });
        clickInsertTask.setOnFailed(e1 -> {
            exception = e1.getSource().getException();
            importLatch.abort();
        });
    }

    private void createImpressionInsertTask() {
        impressionInsertTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                AdDashboard.getDataController().insertImpressions(impressionLog);
                return null;
            }
        };
        impressionInsertTask.setOnSucceeded(
                e1 -> {
                    log.info("Completed INSERT -> Impressions");
                    importLatch.countDown();
                });
        impressionInsertTask.setOnFailed(e1 -> {
            exception = e1.getSource().getException();
            importLatch.abort();
        });
    }

    private void createServerInsertTask() {
        serverInsertTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                AdDashboard.getDataController().insertServer(serverLog);
                return null;
            }
        };
        serverInsertTask.setOnSucceeded(e1 -> {
            log.info("Finished INSERT -> Server");
            importLatch.countDown();
        });
        serverInsertTask.setOnFailed(e1 -> {
            exception = e1.getSource().getException();
            importLatch.abort();
        });
    }
}
