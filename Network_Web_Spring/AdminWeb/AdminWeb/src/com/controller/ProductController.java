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
				String FLid = request.getParameter("FLid");
				String ecuid = request.getParameter("ecuid");
				String data = request.getParameter("data");
				String state = request.getParameter("state").toString();
				
				//System.out.println("state : "+state);
				temp=FLid+","+ecuid+","+data;
				
				// App notification
				URL url = new URL("https://fcm.googleapis.com/fcm/send");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				conn.setUseCaches(false);
				conn.setDoInput(true);
				conn.setDoOutput(true);

				conn.setRequestProperty("Authorization", "key="
						+ " 서버키값 ");
				conn.setRequestProperty("Content-Type", "application/json");

				JSONObject json = new JSONObject();
				json.put("to",
						"AdminApp 기기 토큰 값");

				JSONObject info = new JSONObject();
				info.put("title", FLid);
				info.put("body", ecuid+" "+data);

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

		String FLid = request.getParameter("FLid");
		String ecuid = request.getParameter("ecuid");
		String data = request.getParameter("data");

		// browser/app -> Pad notification
		URL url = new URL("https://fcm.googleapis.com/fcm/send");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestProperty("Authorization", "key="
				+ " 서버키값");
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();
		json.put("to",
				"Pad 기기 토큰값");
		
		JSONObject info = new JSONObject();
		info.put("title", ecuid);
		info.put("body", data);

		json.put("notification", info);

		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(json.toString());
		out.flush();
		//conn.getInputStream();
		System.out.println("Sent to Firebase : "+FLid+ ", " + ecuid +","+data);

		// briwser/app -> tcpipserver
		Socket socket;
		Client client = null;
		//String address = "13.209.113.212";
		String address = "70.12.113.200"; // TCP/IP Server IP
		int port = 8888;
		try {
			client = new Client(address, port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Msg msg = new Msg(ecuid, data, FLid);
		client.setMsg(msg);

		client.sender.setMsg(msg);
		System.out.println("Sent to TCT/IP :"+address+" Msg Object :" + ecuid+","+ data+","+ FLid);

		new Thread(client.sender).start();

		mv.setViewName("main");
		return "return";
	}

}
