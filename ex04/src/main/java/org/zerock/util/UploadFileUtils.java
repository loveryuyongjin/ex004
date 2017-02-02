package org.zerock.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

public class UploadFileUtils {

  private static final Logger logger = 
      LoggerFactory.getLogger(UploadFileUtils.class);

//  public static String uploadFile(String uploadPath, 
//      String originalName, 
//      byte[] fileData)throws Exception{
//    
//    return null;
//  }
//  


	/*
	 * 파일의 저장 경로
	 * 원본 파일의 이름
	 * 파일데이터 (byte[])
	 */
	
	/*별도의 데이터가 보관될 필요가 없기 때문에 static으로 설계되었다*/
  public static String uploadFile(String uploadPath, 
                              String originalName, 
                              byte[] fileData)throws Exception{
    
    UUID uid = UUID.randomUUID();
    
 		
 	// 저장될 경로를 계산한다.	
    String savedName = uid.toString() +"_"+originalName;
    
    String savedPath = calcPath(uploadPath);
    
    File target = new File(uploadPath +savedPath,savedName);
   //원본 파일을 저장하는다.
    FileCopyUtils.copy(fileData, target);
    //원본 파일의 확장자를 의미한다.
	//이를 이용해서 MediaUtils 클래스의 getMediaType()을 이용해서 이미지 파일인 경우와 그렇지 않은 경우를 나누어 처리한다.
    String formatName = originalName.substring(originalName.lastIndexOf(".")+1);
    
    // 이미지 타입의 파일인 경우네는 썸네일을 생성하고, 그렇지 않는 경우에는 makeIcon를 통해서 결과를 만들어 낸다.
  	// makeIcon은 경로 처리를 하는 문자열의 치화뇽ㅇ도에 불과하다.
    String uploadedFileName = null;
    
    if(MediaUtils.getMediaType(formatName) != null){
      uploadedFileName = makeThumbnail(uploadPath, savedPath, savedName);
    }else{
      uploadedFileName = makeIcon(uploadPath, savedPath, savedName);
    }
    
    return uploadedFileName;
    
  }
  
  private static  String makeIcon(String uploadPath, 
      String path, 
      String fileName)throws Exception{

    String iconName = 
        uploadPath + path + File.separator+ fileName;
    
    return iconName.substring(
        uploadPath.length()).replace(File.separatorChar, '/');
  }
  
  
  private static  String makeThumbnail(
              String uploadPath, 
              String path, 
              String fileName)throws Exception{
   
	//BufferedImage는 실제 이미지가 아닌 메모리상의 이미지를 의미하는 객체
	//원본 파일을 메모리상으로 로딩하고, 정해진 크기에 맞게 작은 이미지 파일에 원본이미지를 복사한다.
    BufferedImage sourceImg = 
        ImageIO.read(new File(uploadPath + path, fileName));
    
  //FIT_TO_HEIGHT 썸네일 이미지 파일의 높이를 뒤에 지정된 100px로 동일하게 만들어 주는 역할을 한다.
    BufferedImage destImg = 
        Scalr.resize(sourceImg, 
            Scalr.Method.AUTOMATIC, 
            Scalr.Mode.FIT_TO_HEIGHT,100);
    
  //썸네일 이미지의 파일명에는 UUID 값이 사용된 이후에 반드시  's_'로 시작하도록 설정되었다.
  		//'s_'로 시작하면 썸네일 이미지이고 's_'를 제외했을 때는 원본 파일의 이름이 되도록 하기위해서다.
    String thumbnailName = 
        uploadPath + path + File.separator +"s_"+ fileName;
    
    File newFile = new File(thumbnailName);
    String formatName = 
        fileName.substring(fileName.lastIndexOf(".")+1);
    
  //메소드의 리턴 시 문자열을 치환하는 이유는 브라우저에서 윈도우의 경로로 사용하는 \문자가 정상적인 경로로 인식되지 않기 때문에 '/' 로 치환한다.
    ImageIO.write(destImg, formatName.toUpperCase(), newFile);
    return thumbnailName.substring(
        uploadPath.length()).replace(File.separatorChar, '/');
  } 
  
  
  private static String calcPath(String uploadPath){
    
    Calendar cal = Calendar.getInstance();
    
    String yearPath = File.separator+cal.get(Calendar.YEAR);
    
    String monthPath = yearPath + 
        File.separator + 
        new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1);

    String datePath = monthPath + 
        File.separator + 
        new DecimalFormat("00").format(cal.get(Calendar.DATE));
    
    makeDir(uploadPath, yearPath,monthPath,datePath);
    
    logger.info(datePath);
    
    return datePath;
  }
  
  
  private static void makeDir(String uploadPath, String... paths){
    
    if(new File(paths[paths.length-1]).exists()){
      return;
    }
    
    for (String path : paths) {
      
      File dirPath = new File(uploadPath + path);
      
      if(! dirPath.exists() ){
        dirPath.mkdir();
      } 
    }
  }
  
  
}
