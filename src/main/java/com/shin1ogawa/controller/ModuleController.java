package com.shin1ogawa.controller;

import java.io.IOException;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.StringUtil;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;

/**
 * @author shin1ogawa
 */
public class ModuleController extends Controller {

	@Override
	protected Navigation run() throws Exception {

		String start = request.getParameter("startVersion");
		String stop = request.getParameter("stopVersion");

		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");

		ModulesService service = ModulesServiceFactory.getModulesService();
		String module = service.getCurrentModule();

		if (StringUtil.isEmpty(stop) == false) {
			stop(stop, service, module);
		} else if (StringUtil.isEmpty(start) == false) {
			start(start, service, module);
		} else {
			stop = service.getCurrentVersion();
			stop(stop, service, module);
		}

		response.flushBuffer();
		return null;
	}

	void start(String start, ModulesService service, String module) throws IOException {
		service.startVersionAsync(module, start);
		response.getWriter().println(
				String.format("start version async:module=%s,version=%s", module, start));
	}

	void stop(String stop, ModulesService service, String module) throws IOException {
		service.stopVersionAsync(module, stop);
		response.getWriter().println(
				String.format("stop version async:module=%s,version=%s", module, stop));
	}
}
