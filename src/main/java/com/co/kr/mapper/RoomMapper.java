package com.co.kr.mapper;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.RoomContentDomain;
import com.co.kr.domain.RoomFileDomain;
import com.co.kr.domain.RoomListDomain;

@Mapper
public interface RoomMapper {

	//list
	public List<RoomListDomain> roomList();
	public void rmcontentUpload(RoomContentDomain roomContentDomain);
	//file upload
	public void rmfileUpload(RoomFileDomain roomFileDomain);

	//content update
	public void rmContentUpdate(RoomContentDomain roomContentDomain);
	//file updata
	public void rmFileUpdate(RoomFileDomain roomFileDomain);

	//content delete 
	public void rmContentRemove(HashMap<String, Object> map);
	//All Content delete
	public void rmContentAllRemove(HashMap<String, String>map);
	
	//file delete 
	public void rmFileRemove(RoomFileDomain roomFileDomain);
	//All File delete
	public void rmFileAllRemove(HashMap<String, String> map);
	
	//select one
	public RoomListDomain roomSelectOne(HashMap<String, Object> map);

	//select one file
	public List<RoomFileDomain> roomSelectOneFile(HashMap<String, Object> map);
	
	List<RoomListDomain> roomListByMbId(String mbId);
}