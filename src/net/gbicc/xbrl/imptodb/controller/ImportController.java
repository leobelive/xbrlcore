package net.gbicc.xbrl.imptodb.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.gbicc.xbrl.imptodb.service.ImportService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class ImportController {
	@Resource(name = "importService")
	private ImportService importService;

	protected static Logger logger = Logger.getLogger("controller");

	/**
	 * 导库首页跳转
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/importdb")
	public String validateIndex(Model model) {
		return "importdb";
	}

	@RequestMapping(value = "/importdb/{imp}")
	@ResponseBody
	public String getImportResults(@PathVariable String imp) {
		try {
			String result = importService.importInstance(imp);
			List<String> results = new ArrayList<String>();
			results.add(result);
			JSONArray ja = JSONArray.fromObject(results);
			return ja.toString();
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return JSONObject.fromObject("读取文件失败").toString();
		}
	}
}
