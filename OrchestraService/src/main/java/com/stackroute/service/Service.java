package com.stackroute.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.stackroute.messaging.Publish;
import com.stackroute.model.Model;



@Component
public class Service {

	private Publish publish;
	@Autowired
	public void setPublish(Publish publish) {
		this.publish = publish;
	}
	private HashMap<String,Model> modelMap = new LinkedHashMap<>();
	private HashMap<String,Integer> uniqueList = new LinkedHashMap<>();
	
	private ArrayList<JSONObject> joList =new ArrayList<JSONObject>();




	public  ArrayList<JSONObject> integration(JSONObject fetchedObj){

		try {
			String domain = fetchedObj.getString("domain");
			String concept = fetchedObj.getString("concept");
			String url = fetchedObj.getString("url");

			String unique= domain+concept+url;

			if(uniqueList.containsKey(unique) && uniqueList.get(unique)< 3)	
			{	Model model=modelMap.get(unique);
			if (fetchedObj.has("terms")) { 
				model.setTerms(fetchedObj.getJSONObject("terms"));
				int count = uniqueList.get(unique);
				uniqueList.replace(unique,count+1);
			}
			else if(fetchedObj.has("ImgCount")){
				model.setImgCount(fetchedObj.getInt("ImgCount"));
				int count = uniqueList.get(unique);
				uniqueList.replace(unique,count+1);
			}else if(fetchedObj.has("codecount")){
				model.setCodecount(fetchedObj.getInt("codecount"));
				int count = uniqueList.get(unique);
				uniqueList.replace(unique,count+1);
			} 
			else{
				model.setVideoCount(fetchedObj.getInt("VideoCount"));
				int count = uniqueList.get(unique);
				uniqueList.replace(unique,count+1);
			} 
			modelMap.put(unique, model);
			//		
			}
			else if(uniqueList.containsKey(unique) && uniqueList.get(unique)== 3){
				Model model=modelMap.get(unique);
				if (fetchedObj.has("terms")) {
					model.setTerms(fetchedObj.getJSONObject("terms"));
					//model.setTerms((fetchedObj.getJSONObject("terms").toString()));
					//String termObj = fetchedObj.getString("terms");
					//model.setTerms( new JSONObject(termObj));
					int count = uniqueList.get(unique);
					uniqueList.replace(unique,count+1);
				}
				else if(fetchedObj.has("ImgCount")){
					model.setImgCount(fetchedObj.getInt("ImgCount"));
					int count = uniqueList.get(unique);
					uniqueList.replace(unique,count+1);
				}else if(fetchedObj.has("codecount")){
					model.setCodecount(fetchedObj.getInt("codecount"));
					int count = uniqueList.get(unique);
					uniqueList.replace(unique,count+1);
				} 
				else{
					model.setVideoCount(fetchedObj.getInt("VideoCount"));
					int count = uniqueList.get(unique);
					uniqueList.replace(unique,count+1);
				} 

				Model finalModel =modelMap.get(unique);
				JSONObject jo = new JSONObject();
				jo.put("concept",finalModel.getConcept());
				jo.put("domain", finalModel.getDomain());
				jo.put("url", finalModel.getUrl());
				jo.put("terms", finalModel.getTerms());
				jo.put("imgCount", finalModel.getImgCount());
				jo.put("codeCount",finalModel.getCodecount());
				jo.put("videoCount", finalModel.getVideoCount()); 
				Document doc = Jsoup.connect(finalModel.getUrl()).userAgent("Mozilla").get();
					Elements title = doc.getElementsByTag("title");
					Elements meta =doc.select("meta[name=description]");
					String titleUrl = title.text();
					String metaUrl = "no description";
					if(meta.size()!=0){
						metaUrl=meta.get(0).attr("content");
					}
					jo.put("titleUrl",titleUrl );
					jo.put("metaUrl",metaUrl );
					
				joList.add(jo);
				String json = jo.toString();

				modelMap.remove(unique);
				uniqueList.remove(unique);
				publish.publishMsg(json);

			}
			else{
				uniqueList.put(unique,1);
				Model model = new Model();
				model.setDomain(fetchedObj.getString("domain"));
				model.setConcept(fetchedObj.getString("concept"));
				model.setUrl(fetchedObj.getString("url"));

				if (fetchedObj.has("terms")) {
					model.setTerms(fetchedObj.getJSONObject("terms"));
					//						model.setTerms((fetchedObj.getJSONObject("terms").toString()));
					//						String termObj = fetchedObj.getString("terms");
					//						model.setTerms( new JSONObject(termObj));
				}
				else if(fetchedObj.has("ImgCount")){
					model.setImgCount(fetchedObj.getInt("ImgCount"));

				}else if(fetchedObj.has("codecount")){
					model.setCodecount(fetchedObj.getInt("codecount"));

				} 
				else{
					model.setVideoCount(fetchedObj.getInt("VideoCount"));
				} 
				modelMap.put(unique, model);			
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (AmqpException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return joList;

	}
	public ArrayList<JSONObject> getJoList() {
		return joList;
	}



}
