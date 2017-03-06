package t16.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RunnableAdapter Adapter
 *
 * @from COMP1206 - Assignment 2
 * @author Huw Jones
 * @since 23/04/2016
 */
public abstract class RunnableAdapter implements java.lang.Runnable {
    protected static final Logger log = LogManager.getLogger(RunnableAdapter.class);

    @Override
    public void run() {
        try {
            this.runSafe();
        } catch (Exception ex){
            log.catching(ex);
        }
    }

    public abstract void runSafe() throws Exception;
}