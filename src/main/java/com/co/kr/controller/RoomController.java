package com.co.kr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.co.kr.service.RoomService;
import com.co.kr.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

import com.co.kr.vo.FileListVO;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.domain.RoomFileDomain;
import com.co.kr.domain.RoomListDomain;
import com.co.kr.mapper.RoomMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class RoomController {

	@Autowired
	private RoomService roomService;

	@RequestMapping(value = "rmdetail", method = RequestMethod.GET)
	public ModelAndView rmSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO,
			@RequestParam("rmSeq") String rmSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("rmSeq", Integer.parseInt(rmSeq));
		RoomListDomain roomListDomain = roomService.roomSelectOne(map);
		System.out.println("rmListDomain" + roomListDomain);
		List<RoomFileDomain> fileList = roomService.roomSelectOneFile(map);

		for (RoomFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("detail", roomListDomain);
		mav.addObject("files", fileList);
		mav.setViewName("/room/roomList.html");
		session.setAttribute("files", fileList);
		return mav;
	}

	@RequestMapping(value = "rmList", method = RequestMethod.GET)
	public ModelAndView roomList(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		System.out.println(session.getId());
		List<RoomListDomain> items = roomService.roomList();
		System.out.println("items ==> " + items);
		mav.addObject("items", items);
		mav.setViewName("room/roomList.html");
		return mav;
	}
	
	@RequestMapping(value = "rmupload")
	public ModelAndView rmUpload(@RequestParam("rmType") String rmType, FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {
		System.out.println(httpReq.getAttribute("rmType"));
		ModelAndView mav = new ModelAndView();
		fileListVO.setType(rmType);
		int rmSeq = roomService.rmfileProcess(fileListVO, request, httpReq);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle(""); // 초기화
		mav = rmSelectOneCall(fileListVO, String.valueOf(rmSeq), request);
		mav.setViewName("room/roomList.html");
		return mav;
	}

	@RequestMapping(value = "/rmremove", method = RequestMethod.GET)
	public ModelAndView remove(RoomFileDomain roomFileDomain, String rmSeq, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println(rmSeq + "번째 방 정보 삭제.");
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("rmSeq", Integer.parseInt(rmSeq));
		roomService.rmContentRemove(map);

		roomFileDomain.setRmSeq(Integer.parseInt(rmSeq));
		System.out.println(rmSeq + "번째 방 사진 삭제");
		roomService.rmFileRemove(roomFileDomain);
		mav.setViewName("/room/roomList.html");
		String alertText = "방 정보가 삭제되었습니다.";
		String redirectPath = "/main/rmList";
		CommonUtils.redirect(alertText, redirectPath, response);
		return mav;
	}

	@RequestMapping(value = "/rmedit", method = RequestMethod.GET)
	public ModelAndView edit(FileListVO fileListVO, @RequestParam("rmSeq") String rmSeq, HttpServletRequest request)
			throws IOException {
		ModelAndView mav = new ModelAndView();

		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("rmSeq", Integer.parseInt(rmSeq));
		RoomListDomain roomListDomain = roomService.roomSelectOne(map);
		List<RoomFileDomain> fileList = roomService.roomSelectOneFile(map);

		for (RoomFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		System.out.println("rmContent : "+roomListDomain.getRmContent());
		System.out.println("rmType : "+roomListDomain.getRmType());
		System.out.println("rmMemo : "+roomListDomain.getRmMemo());
		fileListVO.setSeq(roomListDomain.getRmSeq());
		fileListVO.setContent(roomListDomain.getRmContent());
		fileListVO.setTitle(roomListDomain.getRmTitle());
		fileListVO.setType(roomListDomain.getRmType());
		fileListVO.setMemo(roomListDomain.getRmMemo());
		fileListVO.setIsEdit("rmedit"); // upload 재활용하기위해서

		mav.addObject("detail", roomListDomain);
		mav.addObject("files", fileList);
		mav.addObject("fileLen", fileList.size());

		mav.setViewName("room/roomEditList.html");
		return mav;
	}

	@PostMapping(value = "/rmeditSave")
	public ModelAndView rmeditSave(@ModelAttribute("fileListVO") FileListVO fileListVO,
			MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
		ModelAndView mav = new ModelAndView();
		roomService.rmfileProcess(fileListVO, request, httpReq);
		mav = rmSelectOneCall(fileListVO, fileListVO.getSeq(), request);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle(""); // 초기화
		mav.setViewName("room/roomList.html");
		return mav;
	}
	@RequestMapping(value="/rmRemove", method = RequestMethod.GET)
	public String tdRemove(RoomFileDomain roomFileDomain, String rmSeq, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println(rmSeq + "번째 게시글 삭제.");
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("rmSeq", Integer.parseInt(rmSeq));
		roomService.rmContentRemove(map);

		roomFileDomain.setRmSeq(Integer.parseInt(rmSeq));
		System.out.println(rmSeq + "번째 게시글 사진 삭제");
		return "redirect:/rmList";
	}
}
