package com.shin1ogawa.controller;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;
import org.slim3.util.StringUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.apphosting.api.ApiProxy;
import com.google.common.collect.Lists;

/**
 * @author shin1ogawa
 */
public class CreateTestDataController extends Controller {

	static final String QUEUE_NAME = "queue-shin1-vm";

	static final String PARAM_END_NUMBER = "endNumber";

	static final String PARAM_START_NUMBER = "startNumber";

	static final Logger logger = Logger.getLogger(CreateTestDataController.class.getName());


	@Override
	protected Navigation run() throws Exception {

		logEnvironment(request);

		if (StringUtil.isEmpty(request.getParameter(PARAM_START_NUMBER))) {
			return addTasks();
		}
		return runTask();
	}

	Navigation addTasks() {
		logger.log(Level.INFO, "add tasks");
		ApiProxy.flushLogs();
		for (int i = 0; i < 5000; i += 500) {
			addTask(i + 1, i + 500);
		}
		return null;
	}

	Navigation runTask() {
		int startNumber = Integer.parseInt(request.getParameter(PARAM_START_NUMBER));
		int endNumber = Integer.parseInt(request.getParameter(PARAM_END_NUMBER));

		List<Entity> entities = Lists.newArrayList();

		for (int i = startNumber; i <= endNumber; i++) {
			Entity e = new Entity(Datastore.createKey("TestEntity", startNumber));
			e.setProperty("p1", String.valueOf(i));
			entities.add(e);
		}

		logger.log(Level.INFO, String.format("prepared entities: %d~%d", startNumber, endNumber));
		ApiProxy.flushLogs();
		Datastore.put(entities);
		logger.log(Level.INFO, String.format("put entities: %d~%d", startNumber, endNumber));
		ApiProxy.flushLogs();
		return null;
	}

	static void addTask(int startNumber, int endNumber) {
		QueueFactory.getQueue(QUEUE_NAME).add(
				TaskOptions.Builder.withUrl("/CreateTestData").method(Method.GET)
					.param(PARAM_START_NUMBER, String.valueOf(startNumber))
					.param(PARAM_END_NUMBER, String.valueOf(endNumber)));
	}

	static void logEnvironment(HttpServletRequest request) {
		logRequestHeaders(request);
		logCurrentEnvironmentAttributes();
	}

	static void logRequestHeaders(HttpServletRequest request) {
		logger.log(Level.CONFIG, "Request Headers --");
		@SuppressWarnings("unchecked")
		Enumeration<String> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			logger.log(Level.CONFIG, String.format("%s=%s", name, request.getHeader(name)));
		}
	}

	static void logCurrentEnvironmentAttributes() {
		logger.log(Level.CONFIG, "ApiProxy.getCurrentEnvironment().getAttributes() --");
		Map<String, Object> attributes = ApiProxy.getCurrentEnvironment().getAttributes();
		if (attributes != null) {
			Set<Entry<String, Object>> entrySet = attributes.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				logger.log(Level.CONFIG, String.format("%s=%s", entry.getKey(), entry.getValue()));
			}
		}
	}
}
