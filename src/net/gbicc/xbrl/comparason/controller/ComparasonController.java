package net.gbicc.xbrl.comparason.controller;

import java.util.Map;
import java.io.IOException;

import javax.annotation.*;

import net.sf.json.*;

import org.apache.log4j.Logger;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import net.gbicc.xbrl.comparason.service.ComparasonService;

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

	protected static Logger logger = Logger.getLogger("controller");

	/**
	 * 比较首页跳转
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/compare")
	public String validateIndex(Model model) {
		return "compare";
	}

	@RequestMapping(value = "/compare/{pv1}/{pv2}")
	@ResponseBody
	public String getComparasonResults(@PathVariable String pv1,
			@PathVariable String pv2) {
		try {
			Map<String, Integer> results = comparasonService
					.getComparasonResult(pv1, pv2);
			JSONArray ja = JSONArray.fromObject(results);
			return ja.toString();
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return JSONObject.fromObject("读取文件失败").toString();
		}
	}

}
