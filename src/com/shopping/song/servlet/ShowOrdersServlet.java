package com.shopping.song.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shopping.empory.service.ProductService;
import com.shopping.empory.service.impl.ProductServiceImpl;
import com.shopping.liao.pojo.User;
import com.shopping.song.pojo.SalesItem;
import com.shopping.song.pojo.SalesOrder;
import com.shopping.song.service.SalesOrderService;
import com.shopping.song.service.impl.SalesOrderServiceImpl;

public class ShowOrdersServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//显示用户订单
		SalesOrderService salesOrderService = SalesOrderServiceImpl.getInstance();
		ProductService productService = ProductServiceImpl.getInstance();
		
		int page = Integer.parseInt(request.getParameter("page"));
		int singlePageCount = Integer.parseInt(request.getParameter("singlePageCount"));
		
		PrintWriter out = response.getWriter();
		StringBuffer jsonString = new StringBuffer("[");
		
		//迭代订单
		int userid = ((User)request.getSession().getAttribute("user")).getId();
		List<SalesOrder> salesOrders = salesOrderService.getSalesOrdersByUid(userid,page,singlePageCount);
		for(Iterator<SalesOrder> i = salesOrders.iterator();i.hasNext();) {
			SalesOrder salesOrder = i.next();
			String orderid = salesOrder.getId();
			String pdate = salesOrder.getPdate().toString();
			String recievename = salesOrder.getRecievename();
			
			//拼订单项的字符串
			List<SalesItem> salesItems = salesOrder.getSalesItems();
			StringBuffer itemsString = new StringBuffer();
			for(Iterator<SalesItem> j = salesItems.iterator();j.hasNext();) {
				SalesItem salesItem = j.next();
				String salesItemName = productService.getProductById(salesItem.getProductId()).getName();
				int amount = salesItem.getPcount();
				itemsString.append(salesItemName+"("+amount+")<br>");
			}
			String items = itemsString.toString();
			
			int status = salesOrder.getStatus();
			
			
			//拼订单字符串
			jsonString.append("{\"orderid\":\""+orderid+"\",\"pdate\":\""+pdate+"\",\"recievename\":\""+recievename+"\",\"items\":\""+items+"\",\"status\":"+status+"},");
		}
		jsonString.deleteCharAt(jsonString.length()-1);
		jsonString.append("]");
		out.print(jsonString);

		out.flush();
		out.close();
	}

}
