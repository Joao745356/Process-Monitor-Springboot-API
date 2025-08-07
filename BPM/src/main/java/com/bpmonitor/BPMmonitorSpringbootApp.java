package com.bpmonitor;
import java.util.Enumeration;

import javax.activation.DataSource;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;


/**
 * By default, Spring Boot uses an embedded Tomcat server, but WildFly provides its own server.
 * You need to change your @SpringBootApplication class to extend SpringBootServletInitializer and override configure().
 * @author joao7
 *
 */
@ComponentScan("com.edpr.remsweb.bpmonitor")
@EnableJpaRepositories(basePackages = "com.edpr.remsweb.bpmonitor.repositories" )
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class BPMmonitorSpringbootApp extends SpringBootServletInitializer {

	 private static ConfigurableApplicationContext applicationContext;
	    
	    @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	        return builder.sources(BPMmonitorSpringbootApp.class);
	    }
	    
	    public static void main(String[] args) {
	        // Store the context for shutdown handling
	        applicationContext = SpringApplication.run(BPMmonitorSpringbootApp.class, args);
	        
	        // Add shutdown hook to ensure proper cleanup
	        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	           // System.out.println("Shutdown hook triggered - cleaning up database connections...");
	            try {
	                cleanupDatabaseConnections();
	                if (applicationContext != null && applicationContext.isActive()) {
	                    applicationContext.close();
	                }
	            } catch (Exception e) {
	                System.err.println("Error during shutdown cleanup: " + e.getMessage());
	                e.printStackTrace();
	            }
	        }));
	        
	       // System.out.println("BPM Monitor application started successfully");
	    }
	    
	    /**
	     * Clean up database connections and drivers
	     */
	    private static void cleanupDatabaseConnections() {
	        try {
	            if (applicationContext != null && applicationContext.isActive()) {
	                // Get the DataSource bean and close it if it's HikariCP
	                DataSource dataSource = applicationContext.getBean(DataSource.class);
	                if (dataSource instanceof HikariDataSource) {
	                    HikariDataSource hikariDS = (HikariDataSource) dataSource;
	                    if (!hikariDS.isClosed()) {
	                     //   System.out.println("Closing HikariCP connection pool...");
	                        hikariDS.close();
	                     //   System.out.println("HikariCP connection pool closed successfully");
	                    }
	                }
	                
	                // Deregister Oracle drivers to prevent memory leaks
	                Enumeration<Driver> drivers = DriverManager.getDrivers();
	                while (drivers.hasMoreElements()) {
	                    Driver driver = drivers.nextElement();
	                    if (driver.getClass().getName().contains("oracle")) {
	                        System.out.println("Deregistering Oracle driver: " + driver.getClass().getName());
	                        DriverManager.deregisterDriver(driver);
	                    }
	                }
	            }
	        } catch (Exception e) {
	            System.err.println("Error cleaning up database connections: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
}
