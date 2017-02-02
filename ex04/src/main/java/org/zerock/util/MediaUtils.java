package org.zerock.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

public class MediaUtils {

	/*'
	 * MediaUtils는 확장자를 가지고 이미지 타입인지를 판단해 준즌 역활을 하는데, 이를
	 * 별도의 클래스로 구성한 이유는 브라우저에서 파일을 다운로드 할 것 인지, 보여줄것인지를 결정하기 위해서이다.
	 */
	private static Map<String, MediaType> mediaMap;
	
	static{
		
		mediaMap = new HashMap<String, MediaType>();
		mediaMap.put("JPG", MediaType.IMAGE_JPEG);
		mediaMap.put("GIF", MediaType.IMAGE_GIF);
		mediaMap.put("PNG", MediaType.IMAGE_PNG);
	}
	
	public static MediaType getMediaType(String type){
		
		return mediaMap.get(type.toUpperCase());
	}
}
