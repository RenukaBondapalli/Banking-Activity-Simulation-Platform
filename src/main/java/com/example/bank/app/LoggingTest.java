package com.example.bank.app;

import org.apache.log4j.Logger;

public class LoggingTest {
    private static final Logger logger = Logger.getLogger(LoggingTest.class);

    public static void main(String[] args) {
        logger.debug("Debugging message");
        logger.info("Bank Simulator Started!");
        logger.warn("This is a warning log");
        logger.error("This is an error log");
    }
}
 

