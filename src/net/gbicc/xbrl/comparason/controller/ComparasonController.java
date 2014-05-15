package net.gbicc.xbrl.comparason.controller;

import java.util.Map;

import javax.annotation.Resource;

import net.gbicc.xbrl.comparason.service.ComparasonService;
import net.sf.json.JSONArray;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author joephoenix
 * 
 */
@Controller
@Component
public class ComparasonController {

	@Resource(name = "comparasonService")
	private ComparasonService comparasonService;

	@RequestMapping(value = "/compare/{pv1}/{pv2}")
	@ResponseBody
	public String getComparasonResults(@PathVariable String pv1,
			@PathVariable String pv2) {
		Map<String, String> results = comparasonService.getComparasonResult(
				pv1, pv2);
		JSONArray ja = JSONArray.fromObject(results);
		return ja.toString();
	}

}
