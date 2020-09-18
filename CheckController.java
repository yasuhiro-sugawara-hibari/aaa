package com.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.ValueObject.IndexVo;
import com.springboot.entity.PurchaseEntity;
import com.springboot.form.IndexForm;
import com.springboot.mapper.CheckMapper;


@Controller
public class CheckController {

	@Autowired
	private CheckMapper mapper;

	@RequestMapping("/check")
	public ModelAndView check (ModelAndView mav,IndexForm form) {

		IndexVo DBinsert = new IndexVo();

		List<PurchaseEntity>getinfo = mapper.getinfo();

		String date = form.getDate().replace("-", "");
		DBinsert.setPurdate(date);
		DBinsert.setPurfee(Integer.parseInt(form.getFee()));
		DBinsert.setPurgenre(form.getGenre());
		DBinsert.setPurinfo(form.getInfo());
		DBinsert.setPurpayment(form.getPayment());
		DBinsert.setPurplace(form.getPlace());

		PurchaseEntity ent = mapper.getpurNo();
		String no = ent.getPurid();

		String noo = no.substring(1);
		int number = Integer.parseInt(noo) + 1;

		String DBnum = "P"+ String.format("%04d",number);

		DBinsert.setPurid(DBnum);

		mapper.insert(DBinsert);



		mav.addObject("getinfo", getinfo);

		mav.addObject("view", form);

		mav.setViewName("check");

		return mav;
	}

}
