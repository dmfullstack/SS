package com.stackroute.service;

import java.io.IOException;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.stackroute.messaging.Publish;
import com.stackroute.model.Model;



@Component
public class Service {

	private Publish publisher;
	@Autowired
	public void setpublisher(Publish publisher) {
		this.publisher = publisher;
	}
	private HashMap<String,Model> modelMap = new HashMap<>();
	private HashMap<String,Integer> uniqueList = new HashMap<>();





	public void integration(JSONObject fetchedObj){


		try {
			String domain = fetchedObj.getString("domain");
			String concept = fetchedObj.getString("concept");
			String url = fetchedObj.getString("url");

			String unique= domain+concept+url;

			if(uniqueList.containsKey(unique) && uniqueList.get(unique)< 3)	
			{	Model model=modelMap.get(unique);
			if (fetchedObj.has("terms")) { 
				model.setTerms(fetchedObj.getJSONObject("terms"));
				System.out.println(fetchedObj.getJSONObject("terms"));
				System.out.println(model.getTerms());
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
					System.out.println(fetchedObj.getJSONObject("terms"));
					System.out.println(model.getTerms());
					//						model.setTerms((fetchedObj.getJSONObject("terms").toString()));
					//						String termObj = fetchedObj.getString("terms");
					//						model.setTerms( new JSONObject(termObj));
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

				String json = jo.toString();

				modelMap.remove(unique);
				uniqueList.remove(unique);
				publisher.publishMsg(json);

			}
			else{
				uniqueList.put(unique,1);
				Model model = new Model();
				model.setDomain(fetchedObj.getString("domain"));
				model.setConcept(fetchedObj.getString("concept"));
				model.setUrl(fetchedObj.getString("url"));

				if (fetchedObj.has("terms")) {
					model.setTerms(fetchedObj.getJSONObject("terms"));
					System.out.println(fetchedObj.getJSONObject("terms"));
					System.out.println(model.getTerms());
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

	}



}
