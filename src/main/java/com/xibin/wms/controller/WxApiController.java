package com.xibin.wms.controller;

import com.xibin.core.costants.WXConstants;
import com.xibin.core.pojo.JsApiTicket;
import com.xibin.core.pojo.Message;
import com.xibin.core.utils.AccessTokenUtil;
import com.xibin.core.utils.SignUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Map;

@Controller
@RequestMapping(value = "/wxApi", produces = { "application/json;charset=UTF-8" })
public class WxApiController {
	@RequestMapping("/getAccessToken")
	@ResponseBody
	public Message getAccessToken(HttpServletRequest request, Model model) {
		Message msg = new Message();
		JsApiTicket jsApiTicket = AccessTokenUtil.jsApiTicket;
		if(jsApiTicket == null){
			msg.setCode(0);
		}else{
			Map<String, String> result = SignUtil.sign(jsApiTicket.getTicket(),
					URLDecoder.decode(request.getParameter("url")));
			result.put("appId", WXConstants.APP_ID);
			msg.setCode(200);
			msg.setData(result);
		}
		return msg;
	}

}
