package com.co.kr.service;

import java.util.HashMap;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.RoomContentDomain;
import com.co.kr.domain.RoomFileDomain;
import com.co.kr.domain.RoomListDomain;
import com.co.kr.vo.FileListVO;

public interface RoomService {
	
	public int rmfileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
	
	// 전체 리스트 조회
	public List<RoomListDomain> roomList();

	// 하나 삭제
	public void rmContentRemove(HashMap<String, Object> map);
	// 전체 삭제
	public void rmContentAllRemove(HashMap<String, String>map);
	// 하나 삭제
	public void rmFileRemove(RoomFileDomain todoFileDomain);
	
	// 전체 삭제
	public void rmFileAllRemove(HashMap<String, String> map);
	//select one
	public RoomListDomain roomSelectOne(HashMap<String, Object> map);

	//select one file
	public List<RoomFileDomain> roomSelectOneFile(HashMap<String, Object> map);
	
	public void rmContentUpdate(RoomContentDomain roomContentDomain);

	public void rmFileUpdate(RoomFileDomain roomFileDomain);
	
}