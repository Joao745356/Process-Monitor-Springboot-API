package com.bpmonitor.config;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

@Component
public class DatabaseConnectionManager {

	  private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionManager.class);
	    
	    @Autowired
	    private DataSource dataSource;
	    
	    @PreDestroy
	    public void closeConnections() {
	        log.info("PreDestroy: Shutting down database connections...");
	        performCleanup();
	    }
	    
	    @EventListener
	    public void handleShutdown(ContextClosedEvent event) {
	        log.info("ContextClosedEvent: Application context is closing, ensuring all DB connections are closed");
	        performCleanup();
	    }
	    
	    private void performCleanup() {
	        try {
	            if (dataSource instanceof HikariDataSource) {
	                HikariDataSource hikariDS = (HikariDataSource) dataSource;
	                if (!hikariDS.isClosed()) {
	                    log.info("Closing HikariCP connection pool...");
	                    hikariDS.close();
	                    log.info("HikariCP connection pool closed successfully");
	                } else {
	                    log.info("HikariCP connection pool already closed");
	                }
	            }
	        } catch (Exception e) {
	            log.error("Error closing database connections", e);
	        }
	    }
	}


