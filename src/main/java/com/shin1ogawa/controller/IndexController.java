package com.shin1ogawa.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author shin1ogawa
 */
public class IndexController extends Controller {

	@Override
	protected Navigation run() throws Exception {
		List<Map<String, Object>> modules = Lists.newArrayList();

		ModulesService service = ModulesServiceFactory.getModulesService();
		Set<String> moduleNames = service.getModules();
		if (moduleNames == null) {
			moduleNames = Sets.newHashSet();
			moduleNames.add(service.getCurrentModule());
		}

		{
			Map<String, Object> moduleAsMap = Maps.newLinkedHashMap();
			modules.add(moduleAsMap);
			moduleAsMap.put("name", "current");
			moduleAsMap.put("module", service.getCurrentModule());
			moduleAsMap.put("version", service.getCurrentVersion());
			moduleAsMap.put("instanceId", service.getCurrentInstanceId());
		}
		for (String module : moduleNames) {
			Map<String, Object> moduleAsMap = Maps.newLinkedHashMap();
			modules.add(moduleAsMap);
			moduleAsMap.put("name", module);
			String defaultVersion = service.getDefaultVersion(module);
			List<Map<String, Object>> versions = Lists.newArrayList();
			moduleAsMap.put("versions", versions);

			Set<String> versionNames = service.getVersions(module);
			for (String version : versionNames) {
				Map<String, Object> versionAsMap = Maps.newLinkedHashMap();
				versions.add(versionAsMap);
				versionAsMap.put("name", version);
				versionAsMap.put("default",
						defaultVersion != null && defaultVersion.equals(version));
				try {
					versionAsMap.put("numInstances", service.getNumInstances(module, version));
				} catch (Exception e) {
					Logger.getLogger(IndexController.class.getName()).log(Level.FINE,
							"fail to get numinstances of:" + module + "." + version, e);

				}
			}
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		new ObjectMapper().writeValue(response.getWriter(), modules);
		response.flushBuffer();
		return null;
	}
}
