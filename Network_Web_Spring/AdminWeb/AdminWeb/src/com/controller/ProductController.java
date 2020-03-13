package com.controller;

import java.io.IOException;

import java.io.PrintWriter;
import java.net.Socket;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;


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

	@Resource(name="pbiz")
	Service<Integer,Product> biz;
	
	@RequestMapping("/productadd.mc")
	public ModelAndView padd() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("center", "product/add");
		mv.setViewName("main");
		return mv;
	}
	
	String temp = "";
	@RequestMapping("/httpconnection.mc")
	@ResponseBody
	public ModelAndView plist(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		Msg msg = null;
		String id = request.getParameter("id");
		String txt = request.getParameter("txt");
		msg = new Msg(id,txt);
		System.out.println(msg.getId()+":"+msg.getTxt());

		temp =msg.getId()+":"+msg.getTxt();
		
		URL url = new URL("https://fcm.googleapis.com/fcm/send");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestProperty("Authorization", "key="
				+ "AAAAkrZviEI:APA91bF2uQpFdLDwapUtPMhXvmYCOcAg-fDOMkdUJSGVJky5DDOyXjoP4GeO_8cPuNaNNg_yiJ-bBTAlHnqTTAK0Vql2UhawqAcJ9jejEtfMp4HLblYbv7Enwv_X7pnDsUuAKmlq5cmW");
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();
		json.put("to","dG5t9CSIHaM:APA91bHbhlLdobwmigpimMMdo2zgPZuP9g56yb1UnJEcnvh1lgvGNdDHdOwDfpwgJFR2K-45zKN6lW0YiqBaBsvWVdMJwfMHD-BetFnloHJEq6EGCUUnWqnQAeG5x-1AD0uhOY7SCLcL");

		JSONObject info = new JSONObject();
		info.put("title", id);
		info.put("body", txt);

		json.put("notification", info);

		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(json.toString());
		out.flush();
		conn.getInputStream();

		
		mv.addObject("center", "product/list");

		mv.setViewName("main");
		return mv;
	}
	
	@RequestMapping("/hello.mc")
	@ResponseBody
	public String p2list() throws Exception {

		return temp;
	}
	
	@RequestMapping("/webapp.mc")
	public ModelAndView paddimpl(ModelAndView mv,
			HttpServletRequest request) throws Exception {
		Socket socket;
		boolean flag = false;
		Client client = null;
		String cid="Browser";
		String address = "70.12.113.200";
		int port = 8888;
		try {
			client = new Client(address, port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String ip = request.getParameter("ip");
		String txt = request.getParameter("txt");
		System.out.println(ip+" "+txt);
		Msg msg = new Msg(cid, txt, ip);
		client.setMsg(msg);
		
		client.sender.setMsg(msg);
		
		new Thread(client.sender).start();
		
		
		URL url = new URL("https://fcm.googleapis.com/fcm/send");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestProperty("Authorization", "key="
				+ "AAAAkrZviEI:APA91bF2uQpFdLDwapUtPMhXvmYCOcAg-fDOMkdUJSGVJky5DDOyXjoP4GeO_8cPuNaNNg_yiJ-bBTAlHnqTTAK0Vql2UhawqAcJ9jejEtfMp4HLblYbv7Enwv_X7pnDsUuAKmlq5cmW");
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();
		json.put("to","e3am97uVKPw:APA91bGfjp0s2QvYn8s7FhyyRtjhXxH3KrfbHz5lTxGCeTNQxo4A5nG8Dm52RNemxzFtLuej-5BmhAHqB04jzz_qZlyH6ca1lzo7jFVr01OTIJcvqsvRYXCYpsY0dzTM8OCfWbRBtf-r");

		JSONObject info = new JSONObject();
		info.put("title", ip);
		info.put("body", txt);

		json.put("notification", info);

		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(json.toString());
		out.flush();
		conn.getInputStream();
		
		
		mv.setViewName("main");
		return mv;
	}
	
}












