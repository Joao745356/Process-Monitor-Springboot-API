package com.bpmonitor.tasks;



import com.bpmonitor.enums.*;
import com.bpmonitor.models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * verifies the connection to a STFP(Secure File Transfer Protocol) server.
 * @author joao7
 *
 */
public class SFTPConnectionTask extends Task{
	
	 private String host; // SFTP server host
	 private int port; // SFTP server port (default is 22)
	 private String username; // SFTP username
	 private String password; // SFTP password
	 private String remoteDirectory; // Directory to list (e.g., "/")
	    

	 public SFTPConnectionTask( 
			 String taskName, 
			 String taskDescription, 
			 TaskStatus status, 
			 Activity activity,
			 TaskType type,
			 TaskRecurrence recurrence, 
			 String workload
			 ) {
		 
		 
		super( taskName, taskDescription,type,
				status,activity,recurrence, workload
				);
		
		JsonNode workloadJson = super.getParsedWorkload(); // crio um jsonNode
		String wLoadHost= workloadJson.get("HOST").asText();  // Tiro o valor de "URL" no meu json para uma variavel
		int wLoadPort = workloadJson.get("PORT").asInt();
		String wLoadUsername = workloadJson.get("USERNAME").asText();
		String wLoadPassword = workloadJson.get("PASSWORD").asText();
		String wLoadDirectory = workloadJson.get("DIRECTORY").asText();
		 
	
		this.host = wLoadHost;
		this.port = wLoadPort;
		this.username = wLoadUsername;
		this.password = wLoadPassword;
		this.remoteDirectory = wLoadDirectory;
	}
	 
	
	 //setters and getters
	 public void setHost(String host) {this.host = host;}
	 public void setPort(int port) {this.port = port;}
	 public void setUsername(String username) {this.username = username;}
	 public void setPassword(String password) {this.password = password;}
	 public void setRemoteDirectory(String dir) {this.remoteDirectory = dir;}

	 public String getHost() {return this.host;}
	 public int getPort() {return this.port;}
	 public String getUsername() {return this.username;}
	 public String getPassword() {return this.password;}
	 public String getRemoteDirecotry() {return this.remoteDirectory;}

	 
	@Override
	public void run() {
		execute();
		
	}

	/**
	 * Creates a JSch instance to configure the session.
	 * connects to the SFTP server with a 5 seconds timeout.
	 * if the connection and listing directory isn't done within 5 seconds, it'll auto fail.
	 */
	@Override
	public TaskValidationResult execute() {
		Session session = null;
        ChannelSftp channelSftp = null;

        try {
            // Create a JSch instance and configure the session
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            // Avoid asking for key confirmation
            session.setConfig("StrictHostKeyChecking", "no");

            // Set a timeout for the connection 
            session.setTimeout(1000); // 1 seconds

            // Connect to the SFTP server
            session.connect();

            // Open an SFTP channel
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
         

            // Attempt to list the remote directory to verify the connection
            channelSftp.ls(remoteDirectory);

    ;
            //this.getTaskResult().setStatus(TaskStatus.SUCCESS);
            //this.getTaskResult().setResultDescription("SFTP connection successful.");

        } catch (JSchException e) {
        } catch (SftpException e) {
        } finally {
            // Disconnect the channel and session
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
		return null;
	}

	
	

}
