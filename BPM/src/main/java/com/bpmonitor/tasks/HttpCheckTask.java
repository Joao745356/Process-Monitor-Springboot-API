package com.bpmonitor.tasks;

import java.net.URL;
import java.time.LocalDateTime;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import com.bpmonitor.enums.*;
import com.bpmonitor.models.*;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * this Task checks whether a web connection is reachable or not.
 * 
 * @author joao7
 *
 */
@Slf4j
@NoArgsConstructor
@Entity
@DiscriminatorValue("HTTPCHECKTASK")
public class HttpCheckTask extends Task {

	/**
	 * task specific variables
	 */

	@Transient
	private String url;

	@Transient
	private final int timeout = 10000; // 10 seconds



	public HttpCheckTask(String taskName, String taskDescription, TaskStatus status, Activity activity, TaskType type,
			TaskRecurrence recurrence, String workload) {

		super(taskName, taskDescription, type, status, activity, recurrence, workload); // crio a super de task

	}

	@PostLoad
	public void extractWorkload() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(this.getWorkload());
			this.url = node.get("URL").asText();
		} catch (Exception e) {
			log.error("Failed to extract URL from workload", e);
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	@Override
	public void run() {
		execute();
	}

	@Override
	public TaskValidationResult execute() {

		if (this.url == null) {
			log.debug("URL not loaded, calling extractWorkload()");
			extractWorkload();
		}

		log.debug("Checking HTTP connectivity to: {}", url);

		String description;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(this.url).openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);

			int responseCode = connection.getResponseCode();

			if (responseCode >= 200 && responseCode < 400) {
				this.setTaskStatus(TaskStatus.SUCCESS);
				description = "Successfully connected to URL: " + url + " (HTTP " + responseCode + ")";
			} else {
				this.setTaskStatus(TaskStatus.FAIL);
				description = "Received error response from URL: " + url + " (HTTP " + responseCode + ")";
			}

		} catch (IOException e) {
			this.setTaskStatus(TaskStatus.FAIL);
			description = "Failed to reach URL: " + url + " - " + e.getMessage();
			log.debug("HTTP check failed", e);
		}

		return new TaskValidationResult(this, this.getTaskStatus(), LocalDateTime.now(), description);
	}

	@Override
	public String toString() {
		return "HttpCheckTask{" + "taskName='" + getTaskName() + '\'' + ", url='" + url + '\'' + ", timeout=" + timeout
				+ '}';
	}

}
