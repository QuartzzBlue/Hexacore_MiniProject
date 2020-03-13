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
	
//	@RequestMapping("/productadd.mc")
//	public ModelAndView padd() {
//		ModelAndView mv = new ModelAndView();
//		mv.addObject("center", "product/add");
//		mv.setViewName("main");
//		return mv;
//	}
	
	// Pad ���� �޾Ƽ� Browser�� Display
	// App notification
	String temp = "";
	@RequestMapping("/httpconnection.mc")
	@ResponseBody
	public void plist(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		// Pad ���� �޾Ƽ� Browser�� Display
		Msg msg = null;
		String id = request.getParameter("id");
		String txt = request.getParameter("txt");
		msg = new Msg(id,txt);
		System.out.println(msg.getId()+":"+msg.getTxt());

		temp =msg.getId()+":"+msg.getTxt();
		
		// App notification
		URL url = new URL("https://fcm.googleapis.com/fcm/send");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestProperty("Authorization", "key="
				+ "서버키입력");
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();
		json.put("to","App토큰값입력");

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
	
	@RequestMapping("/hello.mc")
	@ResponseBody
	public String p2list() throws Exception {

		return temp;
	}
	
	//browser/app -> Pad notification
	//browser/app -> tcpipserver
	@RequestMapping("/webapp.mc")
	@ResponseBody
	public String paddimpl(ModelAndView mv,
			HttpServletRequest request) throws Exception {
		System.out.println("webapp.mc");
		//browser/app -> tcpipserver
		Socket socket;
		boolean flag = false;
		Client client = null;
		String cid="Browser";
		String address = "70.12.113.230";
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
		

		//browser/app -> Pad notification
		URL url = new URL("https://fcm.googleapis.com/fcm/send");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestProperty("Authorization", "key="
				+ "서버키값");
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();
		json.put("to","Pad");

		JSONObject info = new JSONObject();
		info.put("title", ip);
		info.put("body", txt);

		json.put("notification", info);

		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(json.toString());
		out.flush();
		conn.getInputStream();
		
		
		mv.setViewName("main");
		return "return";
	}
	
}












