package gbicc.xbrl.validate.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 校验结果展示实现类
 * 
 * @author joephoenix
 */
@Service("resultService")
public class ResultService {

	public List<String> returnErrorList() {
		List<String> errorList = new ArrayList<String>();
		errorList.add("Error1:XBRL-001:分类标准引用不对.");
		errorList.add("Error1:XBRL-002:引用了不存在的元素.");
		errorList.add("Error1:XBRL-003:tuple表中引用元素错误.");
		return errorList;

	}
}
