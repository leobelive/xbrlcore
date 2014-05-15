package net.gbicc.xbrl.validate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.gbicc.xbrl.ent.model.ValidateObject;
import net.gbicc.xbrl.validate.service.ValidateService;
import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 处理的控制器
 * 
 * @author joephoenix
 */
@Controller
@Component
public class ValidateController {
	@Resource(name = "validateService")
	private ValidateService validateService;

	protected static Logger logger = Logger.getLogger("controller");

	/**
	 * 首页跳转
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/")
	public String IndexPage(Model model) {
		return "welcome";
	}

	/**
	 * 校验首页跳转
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/validate")
	public String validateIndex(Model model) {
		return "validate";
	}

	@RequestMapping(value = "/validateresult")
	public String validateDeal(Model model) {
		return "validateResults";
	}

	/**
	 * 校验功能方法
	 * 
	 * @param p
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/validate/{p1}")
	@ResponseBody
	public String FirstPage(@PathVariable Integer p1, Model model) {
		List<ValidateObject> result = new ArrayList<ValidateObject>();
		try {
			result = validateService.returnErrorList();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		JSONArray ja = JSONArray.fromObject(result);
		return ja.toString();
	}
}
