package com.co.algomoko.diary.controller;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.filters.AddDefaultCharsetFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.co.algomoko.admin.domain.AdminVO;
import com.co.algomoko.diary.domain.DiaryVO;
import com.co.algomoko.diary.domain.DiaryVO1;
import com.co.algomoko.diary.domain.DiaryVO2;
import com.co.algomoko.diary.domain.RecipeVO;
import com.co.algomoko.diary.domain.ReqVO;
import com.co.algomoko.diary.mapper.DiaryMapper;
import com.co.algomoko.food.domain.FoodVO;
import com.co.algomoko.food.mapper.FoodMapper;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import groovyjarjarantlr4.v4.parse.ANTLRParser.action_return;
import lombok.Data;

@RequestMapping("/diary")
@Controller
public class DiaryController {
   
   @Autowired DiaryMapper dao;
   @Autowired FoodMapper dao1;
   
   @RequestMapping("")
    public String sicmain(Model model, DiaryVO diaryVO,Authentication authentication){
      Calendar calendar= Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      diaryVO.setMid(mid.getUsername());
      diaryVO.setDdate(calendar.getTime());
      
      
      model.addAttribute("todaysic",dao.findDay(diaryVO));
      model.addAttribute("resultCal",dao.jukcal(diaryVO));
      
      
        return "contents/diary/sicmain";
    }
   @RequestMapping("cal")
    public String cal(){
        return "contents/diary/cal";
    }
   @RequestMapping("myre")
    public String myre(Model model , RecipeVO recipeVO,Authentication authentication){
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      recipeVO.setMid(mid.getUsername());
      
      model.addAttribute("rlist",dao.rlist(recipeVO));
      
      
      return "contents/diary/myre";
   }
   @RequestMapping("redetail")
       public String redetail(@RequestParam("rname") String rname ,Model model , RecipeVO recipeVO,Authentication authentication){
         
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      recipeVO.setMid(mid.getUsername());
      
      //System.out.println(dao.onelist(recipeVO).get(0).getRrecipe());
      
      
      	
      
      	String[] rearry = dao.onelist(recipeVO).get(0).getRrecipe().split(",");
      	List<String> rearrys = new ArrayList<String>();
      		for(int i=0;i< rearry.length;i++) {
      			if(rearry[i].equals("")||rearry[i].isBlank()||rearry[i].isEmpty()) {
				continue;
				
			}rearrys.add(rearry[i]);
      			
			
      	}
      	 
			
			
         
         model.addAttribute("rrecp",dao.onelist(recipeVO));
         model.addAttribute("redetail",dao.redetail(recipeVO));
         model.addAttribute("rname",rname);
         model.addAttribute("rearrys",rearrys);
         return "contents/diary/redetail";
      
      
      
    }
   @RequestMapping("remodify") 
	public String remodify(HttpServletResponse response,RecipeVO recipeVO,Authentication authentication)throws IOException, ParseException { 
		UserDetails mid = (UserDetails) authentication.getPrincipal();
		recipeVO.setMid(mid.getUsername());
		
		recipeVO.setNick(dao.tomem(recipeVO.getMid()));
		
		dao.redelete(recipeVO);
		dao.rededelete(recipeVO);
		int cal = 0;//총칼
		int carb = 0;//총탄
		int prot = 0;//총단
		int rfat = 0;//총지
				
		String[] fings = recipeVO.getFing().split(",");
		List<String> ddnameList = new ArrayList<String>();	
		DiaryVO res = new DiaryVO();
		for(int i=0;i< fings.length;i++) {
			
			ddnameList.add(fings[i]);
			
			
		}

		  for(int i=0;i< ddnameList.size();i++) { 
			  res = dao.fonlist(fings[i]);
			
			 recipeVO.setMid(mid.getUsername());
			 recipeVO.setFing(res.getDdname());
			 recipeVO.setCal(res.getCal());
			 recipeVO.setAamount(res.getAmount());
			  dao.redeinsert(recipeVO);
			  cal = cal+res.getCal();
			 carb = carb+res.getCarb();
			 prot = prot+res.getProt();
			 rfat = rfat+res.getFat();
			  		  
		  }
		  recipeVO.setCal(cal);
		  recipeVO.setCarb(carb);
		  recipeVO.setProt(prot);
		  recipeVO.setRfat(rfat);
		 
		  
		  dao.reinsert(recipeVO);

               return "redirect:/diary/myre"; 

	}
   @RequestMapping("succ")
    public String succ(){
      
        return "contents/diary/succ";
    }
   @RequestMapping("todaysic")
    public String todaysic(Model model, DiaryVO diaryVO,Authentication authentication) throws ParseException{
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String std = sdf.format(date);
      
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      diaryVO.setMid(mid.getUsername());
      diaryVO.setDdate(date);
      
      
      
      
      diaryVO.setDddo("aa");
      model.addAttribute("aade",dao.detail(diaryVO));
      diaryVO.setDddo("bb");
      model.addAttribute("bbde",dao.detail(diaryVO));
      diaryVO.setDddo("cc");
      model.addAttribute("tcal",dao.tcal(diaryVO.getMid()));
      model.addAttribute("ccde",dao.detail(diaryVO));
      model.addAttribute("std",std);
      model.addAttribute("resultCal",dao.resultCal(diaryVO));
      
      
      return "contents/diary/daysic";
    }
   
   
   @RequestMapping(value="daysic") 
    public String daysic(@RequestParam("date") String date, Model model, DiaryVO diaryVO,Authentication authentication ,HttpServletResponse response)
       throws IOException, ParseException{
      DiaryVO2 diaryVO2 = new DiaryVO2();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = date;
      String std = date;
      Date date1 = new Date(sdf.parse(strDate).getTime());
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      diaryVO.setMid(mid.getUsername());
      diaryVO.setDdate(date1);
      
     
      
      
      diaryVO.setDddo("aa");
      model.addAttribute("aade",dao.detail(diaryVO));
      diaryVO.setDddo("bb");
      model.addAttribute("bbde",dao.detail(diaryVO));
      diaryVO.setDddo("cc");
      model.addAttribute("tcal",dao.tcal(diaryVO.getMid()));
      model.addAttribute("ccde",dao.detail(diaryVO));
      model.addAttribute("std",std);
      
      if(dao.resultCal(diaryVO).isEmpty()) {
    	  diaryVO2.setMid(mid.getUsername());
    	  diaryVO2.setDdate(date1);
    	  diaryVO2.setDddo("aa");
          model.addAttribute("aade",dao.detail(diaryVO));
          diaryVO2.setDddo("bb");
          model.addAttribute("bbde",dao.detail(diaryVO));
          diaryVO2.setDddo("cc");
          model.addAttribute("tcal",dao.tcal(diaryVO.getMid()));
          model.addAttribute("ccde",dao.detail(diaryVO));
          model.addAttribute("std",std);
          model.addAttribute("resultCal",dao.resultCal1(diaryVO2));
      }else {
    	  model.addAttribute("resultCal",dao.resultCal(diaryVO));
	}
      
         	 
    	    
      
      
      
      
      
      
      
      return "contents/diary/daysic";
    }
   @RequestMapping("weeklybest")
    public String weeklybest(Model model,RecipeVO recpvo,Authentication authentication){
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      recpvo.setMid(mid.getUsername());
      model.addAttribute("rank",dao.rerank(recpvo));
      
      
        return "contents/diary/weeklybest";
    }
   @RequestMapping("write")
    public String write(Authentication authentication){
        return "contents/diary/write";
    }
   @RequestMapping("writema")
    public String writema(Authentication authentication){
        return "contents/diary/writema";
    }
   @RequestMapping("modify")
    public String modify(Model model, DiaryVO diaryVO, 
          @RequestParam("dddo") String dddo,
          @RequestParam("date") String date,Authentication authentication) throws ParseException{
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = date;
      
      Date date1 = new Date(sdf.parse(strDate).getTime());
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      diaryVO.setMid(mid.getUsername());
      
      diaryVO.setDdate(date1);
      diaryVO.setDddo(dddo);
       
      
      model.addAttribute("modify",dao.detail(diaryVO));
      model.addAttribute("con",dao.con(diaryVO));
      model.addAttribute("dat",date);
      return "contents/diary/modify";
        
    }
   @RequestMapping("modifyde")
public String modifyde(HttpServletResponse response,DiaryVO diaryVO,DiaryVO1 diaryVO1,Authentication authentication)throws IOException, ParseException { 
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      diaryVO.setMid(mid.getUsername());
      
      
      dao.diaryde(diaryVO);
      dao.detade(diaryVO);
      dao.insert(diaryVO);
 
      
      
      
      
      //칼로리
      String[] cals= diaryVO1.getCal().split(",");
      int[] nums = new int[cals.length];
      
      	for(int i=0;i< cals.length;i++) {
          
      		nums[i] = Integer.parseInt(cals[i]);
                
       }
      	
      	
      String[] amounts = diaryVO1.getAmount().split(",");
      int[] ams = new int[amounts.length];
      
      for(int i=0;i< amounts.length;i++) {
    	  
    	  ams[i] = Integer.parseInt(amounts[i]);
    	  
       }
      
      String[] ddnames = diaryVO.getDdname().split(",");
      List<String> ddnameList = new ArrayList<String>();   
      DiaryVO res = new DiaryVO();
      for(int i=0;i< ddnames.length;i++) {
         
         ddnameList.add(ddnames[i]);
         
         
      }

        for(int i=0;i< ddnameList.size();i++) { 
           res = dao.fonlist(ddnames[i]);
           
           res.setMid(mid.getUsername());
           res.setDdate(diaryVO.getDdate());
           res.setDddo(diaryVO.getDddo());
           res.setDdname(ddnames[i]);
           res.setCal(nums[i]);
           res.setAmount(ams[i]);
           dao.insertdetail(res);
        }

                return "redirect:/diary"; 

   }
   @RequestMapping("delete")
public String delete(HttpServletResponse response,DiaryVO diaryVO,Authentication authentication)throws IOException, ParseException { 
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      diaryVO.setMid(mid.getUsername());
      dao.diaryde(diaryVO);
      dao.detade(diaryVO);
      
      
                return "redirect:/diary"; 

   }
   @RequestMapping("redelete") 
   public String redelete(HttpServletResponse response,RecipeVO recipeVO,Authentication authentication)throws IOException, ParseException { 
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      recipeVO.setMid(mid.getUsername());
      recipeVO.setNick(dao.tomem(recipeVO.getMid()));
      
      dao.redelete(recipeVO);
      dao.rededelete(recipeVO);
      

                return "redirect:/diary/myre"; 

   }
   
   @RequestMapping("weekwrite")
    public String weekwrite(Model model, 
          @RequestParam("dddo") String dddo,
          @RequestParam("date") String date,Authentication authentication) throws ParseException{
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = date;
      
      Date date1 = new Date(sdf.parse(strDate).getTime());
      
      model.addAttribute("dddo",dddo);
      model.addAttribute("date",date);
      return "contents/diary/weekwrite";
        
    }
   
   @RequestMapping(value="insert") 
   public String insert(HttpServletResponse response,DiaryVO diaryVO, DiaryVO1 diaryVO1 ,Authentication authentication)throws IOException, ParseException { 
      
	   UserDetails mid = (UserDetails) authentication.getPrincipal();
	      diaryVO.setMid(mid.getUsername());
	      System.out.println(diaryVO);
	      dao.insert(diaryVO);
	 
	      
	      
	      
	      
	      //칼로리
	      String[] cals= diaryVO1.getCal().split(",");
	      int[] nums = new int[cals.length];
	      
	      	for(int i=0;i< cals.length;i++) {
	          
	      		nums[i] = Integer.parseInt(cals[i]);
	                
	       }
	      	
	      	
	      String[] amounts = diaryVO1.getAmount().split(",");
	      int[] ams = new int[amounts.length];
	      
	      for(int i=0;i< amounts.length;i++) {
	    	  
	    	  ams[i] = Integer.parseInt(amounts[i]);
	    	  
	       }
	      
	      String[] ddnames = diaryVO.getDdname().split(",");
	      List<String> ddnameList = new ArrayList<String>();   
	      DiaryVO res = new DiaryVO();
	      for(int i=0;i< ddnames.length;i++) {
	         
	         ddnameList.add(ddnames[i]);
	         
	         
	      }

	        for(int i=0;i< ddnameList.size();i++) { 
	           res = dao.fonlist(ddnames[i]);
	           
	           res.setMid(mid.getUsername());
	           res.setDdate(diaryVO.getDdate());
	           res.setDddo(diaryVO.getDddo());
	           res.setDdname(ddnames[i]);
	           res.setCal(nums[i]);
	           res.setAmount(ams[i]);
	           dao.insertdetail(res);
	        }

	                return "redirect:/diary"; 

   }
   
   
   
   
   @RequestMapping("reinsert") 
   public String reinsert(DiaryVO diaryVO,Authentication authentication)throws IOException, ParseException { 
      
      UserDetails mid = (UserDetails) authentication.getPrincipal();
      diaryVO.setMid(mid.getUsername());
      


                return "contents/diary/reinsert"; 

   }
   
   @RequestMapping("reinsertde") 
   public String reinsertde(HttpServletResponse response,RecipeVO recipeVO,Authentication authentication)throws IOException, ParseException { 
	   int cal = 0;//총칼
		int carb = 0;//총탄
		int prot = 0;//총단
		int rfat = 0;//총지
		UserDetails mid = (UserDetails) authentication.getPrincipal();
		recipeVO.setMid(mid.getUsername());
		recipeVO.setNick(dao.tomem(recipeVO.getMid()));
		System.out.println(recipeVO);
		String[] fings = recipeVO.getFing().split(",");
		List<String> ddnameList = new ArrayList<String>();	
		DiaryVO res = new DiaryVO();
		
			
		
		
		
		
		for(int i=0;i< fings.length;i++) {
			
			ddnameList.add(fings[i]);
			
			
		}
		
		  for(int i=0;i< ddnameList.size();i++) { 
			  res = dao.fonlist(fings[i]);
			 
			 recipeVO.setMid(mid.getUsername());
			 recipeVO.setFing(res.getDdname());
			 recipeVO.setCal(res.getCal());
			 recipeVO.setAamount(res.getAmount());
			  dao.redeinsert(recipeVO);
			  cal = cal+res.getCal();
			 carb = carb+res.getCarb();
			 prot = prot+res.getProt();
			 rfat = rfat+res.getFat();
			  		  
		  }
		  recipeVO.setCal(cal);
		  recipeVO.setCarb(carb);
		  recipeVO.setProt(prot);
		  recipeVO.setRfat(rfat);
		  System.out.println("2");
		  
		  dao.reinsert(recipeVO);

               return "redirect:/diary/myre"; 

	}
   
   
   @RequestMapping("flist")
   @ResponseBody
   public List<FoodVO> flist(@RequestParam("btnbtn") String btnbtn, Model model){
      
      FoodVO foodvo = new FoodVO();
      foodvo.setIng(btnbtn);
      
      return dao1.fList(foodvo);
      
    }
   
   
}