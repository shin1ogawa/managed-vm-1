package com.shin1ogawa.controller;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;

import com.google.appengine.api.datastore.Entity;
import com.google.common.collect.Maps;

/**
 * @author shin1ogawa
 */
public class RpcController extends Controller {

	@Override
	protected Navigation run() throws Exception {
		Map<String, Object> responseAsMap = Maps.newLinkedHashMap();

		long start = System.currentTimeMillis();
		List<Entity> entities = Datastore.query("TestEntity").limit(1000).asList();
		int size = entities.size();
		long end = System.currentTimeMillis();
		responseAsMap.put("Datastore#Get[" + size + "]", end - start);

		start = System.currentTimeMillis();
		Memcache.put("entities", entities);
		end = System.currentTimeMillis();
		responseAsMap.put("Memcache#Put[1000]", end - start);

		start = System.currentTimeMillis();
		List<Entity> entitiesInCache = Memcache.get("entities");
		size = entitiesInCache.size();
		end = System.currentTimeMillis();
		responseAsMap.put("Memcache#Get[" + size + "]", end - start);

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		new ObjectMapper().writeValue(response.getWriter(), responseAsMap);
		response.flushBuffer();
		return null;
	}
}
