package org.smg.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
	public static Map<String, Object> parse(String json) {
		ObjectMapper mapper = new ObjectMapper();
		// read JSON from a file
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = mapper.readValue(json,
					new TypeReference<Map<String, Object>>() {
					});
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	public static Player jSON2Player(String json){
		Map<String, Object> jsonMap = JSONUtil.parse(json);
		Player player = new Player();
		for(String key : jsonMap.keySet())
		{
			boolean success = player.setProperty(PlayerProperty.findByValue(key), (String)jsonMap.get(key));
			if(!success)
				throw new IllegalArgumentException();
		}
		return player;
	}
}