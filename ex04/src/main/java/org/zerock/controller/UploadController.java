package org.zerock.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.util.MediaUtils;
import org.zerock.util.UploadFileUtils;

@Controller
public class UploadController {

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	@Resource(name = "uploadPath")
	private String uploadPath;
	
	@RequestMapping(value = "/uploadForm", method = RequestMethod.GET)
	public void uploadForm( ){
		
	}
	
	@RequestMapping(value = "/uploadForm", method = RequestMethod.POST)
	public String uploadForm(MultipartFile file, Model model) throws Exception{
		
		logger.info("originalName : " + file.getOriginalFilename());
		logger.info("size : " + file.getSize());
		logger.info("contentType : "  + file.getContentType());
		
		String savedName = uploadFile(file.getOriginalFilename(), file.getBytes());
		
		model.addAttribute("savedName", savedName);
		
		return "uploadResult";
	}
	
	private String uploadFile(String originalName, byte[] fileData)throws Exception{
		
		UUID uid = UUID.randomUUID();
		
		String savedName = uid.toString() +"_"+ originalName;
		
		File target = new File(uploadPath, savedName);
		
		FileCopyUtils.copy(fileData, target);
		
		return savedName;
		
	}
	
	
	/*Ajax*/
	@RequestMapping(value = "/uploadAjax", method = RequestMethod.GET)
	public void uploadAjax(){
		
	}
	
	/*produces 설정은 한국어를 정상적으로 전송하기 위한 간단한 설정
	 * HttpStatus.CREATED  == HttpStatus.OK 해도 무방 :  정상적으로 생성되었다는 상태 코드
	 * */
	@ResponseBody
	@RequestMapping(value = "/uploadAjax",  method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> uploadAjax(MultipartFile file)throws Exception{
		
		logger.info("originalName : " + file.getOriginalFilename());
		
		return new ResponseEntity<>( UploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(),  file.getBytes()),  HttpStatus.CREATED);
		
	}
	
	  
	  @ResponseBody
	  @RequestMapping("/displayFile")
	  public ResponseEntity<byte[]>  displayFile(String fileName)throws Exception{
	    
	    InputStream in = null; 
	    ResponseEntity<byte[]> entity = null;
	    
	    logger.info("FILE NAME: " + fileName);
	    
	    try{
	      
	      String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
	    //확장자를 추출하고, 이미지 타입의 파일인 경우는 적절한 MIME 타입을 지정한다.
	      MediaType mType = MediaUtils.getMediaType(formatName);
	      
	      HttpHeaders headers = new HttpHeaders();
	      
	      in = new FileInputStream(uploadPath+fileName);
	      //MIME 타입 지정
	      if(mType != null){
	        headers.setContentType(mType);
	      }else{
	    	  //이미지가 아닌경우 다운로드 용으로 사용  application/octet-stream
	        fileName = fileName.substring(fileName.indexOf("_")+1);       
	        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	        headers.add("Content-Disposition", "attachment; filename=\""+ 
	          new String(fileName.getBytes("UTF-8"), "ISO-8859-1")+"\"");
	      }
	      //다운로드 할때 사용자에게 보이는 파일의 이름이므로 한글 처리를 해서 전송한다.
	      //한글 파일의 경우 다운로드 하면 파일의 이름이 깨져서 나오기 때문에 반드시 인코딩 처리가 필요함.
	        entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), 
	          headers, 
	          HttpStatus.CREATED);
	    }catch(Exception e){
	      e.printStackTrace();
	      entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
	    }finally{
	      in.close();
	    }
	      return entity;    
	  }
	  
	  @ResponseBody
	  @RequestMapping(value = "/deleteFile" , method=RequestMethod.POST)
	  public ResponseEntity<String> deleteFile(String fileName){
		  
		  logger.info("deleteFile : " + fileName);
		  
		  String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
		  
		  MediaType mType = MediaUtils.getMediaType(formatName);
		  
		  if(mType != null){
			  
			  String front = fileName.substring(0,12);
			  String end = fileName.substring(14);
			  
			  new File(uploadPath  + fileName.replace('/', File.separatorChar)).delete();
		  }
				  
		 new File(uploadPath + fileName.replace('/', File.separatorChar)).delete();
		 
		 return new ResponseEntity<String>("deleted", HttpStatus.OK);
	  }
}
