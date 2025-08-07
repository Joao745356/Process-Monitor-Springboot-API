package com.bpmonitor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

@Component
public class OracleDriverCleanup implements DisposableBean {
    
    private static final Logger log = LoggerFactory.getLogger(OracleDriverCleanup.class);
    
    @Override
    public void destroy() throws Exception {
        log.info("Starting Oracle driver cleanup...");
        try {
            // Deregister all Oracle drivers to prevent memory leaks
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                if (driver.getClass().getName().contains("oracle")) {
                    log.info("Deregistering Oracle driver: {}", driver.getClass().getName());
                    DriverManager.deregisterDriver(driver);
                }
            }
            log.info("Oracle driver cleanup completed");
        } catch (Exception e) {
            log.error("Error during Oracle driver cleanup", e);
            throw e;
        }
    }
}