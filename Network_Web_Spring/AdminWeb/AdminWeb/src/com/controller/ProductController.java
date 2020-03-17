package com.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.frame.Service;
import com.vo.Product;

import msg.Msg;
import tcpip.Client;

@Controller
public class ProductController {

	@Resource(name = "pbiz")
	Service<Integer, Product> biz;

//	@RequestMapping("/productadd.mc")
//	public ModelAndView padd() {
//		ModelAndView mv = new ModelAndView();
//		mv.addObject("center", "product/add");
//		mv.setViewName("main");
//		return mv;
//	}

	// Pad 에서 받아서 Browser에 Display
	// App notification
	String temp;

	@RequestMapping("/httpconnection.mc")
	@ResponseBody
	public void plist(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Pad 에서 받아서 Browser에 Display
		Msg msg = null;
		String id = request.getParameter("id");
		String txt = request.getParameter("txt");
		
		temp=id+" : "+txt;
		
		// App notification
		URL url = new URL("https://fcm.googleapis.com/fcm/send");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestProperty("Authorization", "key="
				+ "AAAAkrZviEI:APA91bF2uQpFdLDwapUtPMhXvmYCOcAg-fDOMkdUJSGVJky5DDOyXjoP4GeO_8cPuNaNNg_yiJ-bBTAlHnqTTAK0Vql2UhawqAcJ9jejEtfMp4HLblYbv7Enwv_X7pnDsUuAKmlq5cmW");
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();
		json.put("to",
				"dG5t9CSIHaM:APA91bHbhlLdobwmigpimMMdo2zgPZuP9g56yb1UnJEcnvh1lgvGNdDHdOwDfpwgJFR2K-45zKN6lW0YiqBaBsvWVdMJwfMHD-BetFnloHJEq6EGCUUnWqnQAeG5x-1AD0uhOY7SCLcL");

		JSONObject info = new JSONObject();
		info.put("title", id);
		info.put("body", txt);

		json.put("notification", info);

		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(json.toString());
		out.flush();
		conn.getInputStream();

		return;
	}

	@RequestMapping("/ReturnData.mc")
	@ResponseBody
	public String p2list() throws Exception {

		return temp;
	}

	// browser/app -> Pad notification
	// browser/app -> tcpipserver
	@RequestMapping("/webapp.mc")
	@ResponseBody
	public String paddimpl(ModelAndView mv, HttpServletRequest request) throws Exception {

		String ip = request.getParameter("ip");
		String txt = request.getParameter("txt");

		// browser/app -> Pad notification
		URL url = new URL("https://fcm.googleapis.com/fcm/send");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestProperty("Authorization", "key="
				+ "AAAAkrZviEI:APA91bF2uQpFdLDwapUtPMhXvmYCOcAg-fDOMkdUJSGVJky5DDOyXjoP4GeO_8cPuNaNNg_yiJ-bBTAlHnqTTAK0Vql2UhawqAcJ9jejEtfMp4HLblYbv7Enwv_X7pnDsUuAKmlq5cmW");
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();
		json.put("to",
				"du5CYdNW4A4:APA91bGZ_zaLBmH-7mpdQkiaLBrAjttRh2m_m62zEek99-WLX6AKR-txi_K4-bmp2K0fS7vknM8xxc9OxqgvCWitIApIZScu3Bh4fy9KkKdztlcMo_GqcAb1I1Eu8jlHNx7a3-c8RFDK");

		JSONObject info = new JSONObject();
		info.put("title", ip);
		info.put("body", txt);

		json.put("notification", info);

		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(json.toString());
		out.flush();
		conn.getInputStream();
		System.out.println("Sent to Firebase : "+ip+","+txt);

		// briwser/app -> tcpipserver
		Socket socket;
		Client client = null;
		String cid = "WebServer";
		String address = "70.12.113.230";
		int port = 8888;
		try {
			client = new Client(address, port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Msg msg = new Msg(cid, txt, ip);
		client.setMsg(msg);

		client.sender.setMsg(msg);
		System.out.println("Sent to :"+address+" Msg Object : "+cid+","+txt+","+txt);

		new Thread(client.sender).start();

		mv.setViewName("main");
		return "return";
	}

}
