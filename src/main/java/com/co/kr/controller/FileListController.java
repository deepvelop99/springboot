package com.co.kr.controller;

import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.co.kr.service.UploadService;
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

import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class FileListController {
	@RequestMapping(value = "detail", method = RequestMethod.GET)
	public ModelAndView bdSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO,
			@RequestParam("bdSeq") String bdSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain = uploadService.boardSelectOne(map);
		List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);

		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);
		mav.setViewName("/board/boardList.html");
		session.setAttribute("files", fileList);
		return mav;
	}
	
	@Autowired
	private UploadService uploadService;

	@RequestMapping(value = "upload")
	public ModelAndView bdUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {

		ModelAndView mav = new ModelAndView();
		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle("");
		mav = bdSelectOneCall(fileListVO, String.valueOf(bdSeq), request);
		mav.setViewName("board/boardList.html");
		return mav;
	}

	@RequestMapping(value = "/remove", method = RequestMethod.GET)
	public ModelAndView remove(BoardFileDomain boardFileDomain, String bdSeq, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println(bdSeq + "번째 게시글 삭제.");
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("bdSeq", Integer.parseInt(bdSeq));
		uploadService.bdContentRemove(map);

		boardFileDomain.setBdSeq(Integer.parseInt(bdSeq));
		System.out.println(bdSeq + "번째 게시글 사진 삭제");
		uploadService.bdFileRemove(boardFileDomain);
		mav.setViewName("/board/boardList.html");
		String alertText = "게시글이 삭제되었습니다.";
		String redirectPath = "/main/bdList";
		CommonUtils.redirect(alertText, redirectPath, response);
		return mav;
	}

	@RequestMapping(value = "edit", method = RequestMethod.GET)
//	public ModelAndView edit(FileListVO fileListVO, HttpServletRequest request, HttpServletRequest httpReq,
//			HttpServletResponse response, String bdSeq) throws Exception {
//		ModelAndView mav = new ModelAndView();
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		HttpSession session = request.getSession();
//
//		map.put("bdSeq", Integer.parseInt(bdSeq));
//		BoardListDomain boardListDomain = uploadService.boardSelectOne(map);
//		List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);
//		fileListVO.setSeq(boardListDomain.getBdSeq());
//		fileListVO.setTitle(boardListDomain.getBdTitle());
//		fileListVO.setContent(boardListDomain.getBdContent());
//		System.out.println(fileListVO.getSeq());
//		System.out.println(fileListVO.getTitle());
//		System.out.println(fileListVO.getContent());
//		System.out.println("boardListDomain" + boardListDomain);
//		for (BoardFileDomain list : fileList) {
//			String path = list.getUpFilePath().replaceAll("\\\\", "/");
//			list.setUpFilePath(path);
//		}
//		System.out.println(fileList.size());
//		mav.addObject("detail", boardListDomain);
//		mav.addObject("files", fileList);
//		mav.addObject("fileLen", fileList.size());
//		mav.setViewName("/board/boardEditList.html");
//		session.setAttribute("files", fileList);
//		return mav;
//	}
	public ModelAndView edit(FileListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();

		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		
		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain =uploadService.boardSelectOne(map);
		List<BoardFileDomain> fileList =  uploadService.boardSelectOneFile(map);
		
		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}

		fileListVO.setSeq(boardListDomain.getBdSeq());
		fileListVO.setContent(boardListDomain.getBdContent());
		fileListVO.setTitle(boardListDomain.getBdTitle());
		fileListVO.setIsEdit("edit");  // upload 재활용하기위해서
		
	
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);
		mav.addObject("fileLen",fileList.size());
		
		mav.setViewName("board/boardEditList.html");
		return mav;
	}
	@PostMapping(value="/editSave")
	public ModelAndView editSave(BoardFileDomain boardFileDomain, FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {
		ModelAndView mav = new ModelAndView();
		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq);
		System.out.println(bdSeq+"번째 게시물 수정완료");
		
		uploadService.bdFileRemove(boardFileDomain);
		fileListVO.setContent("");
		fileListVO.setTitle("");
		mav = bdSelectOneCall(fileListVO, String.valueOf(bdSeq), request);
		mav.setViewName("board/boardList.html");
		response.setContentType("text/html; charset=euc-kr");
		PrintWriter out = response.getWriter();
		out.println("수정 완료");
		out.flush();

		return mav;
	}
	@RequestMapping(value = "levdetail", method = RequestMethod.GET)
	public ModelAndView levbdSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO,
			@RequestParam("bdSeq") String bdSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain = uploadService.boardSelectOne(map);
		System.out.println("boardListDomain" + boardListDomain);
		List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);

		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);
		mav.setViewName("levboard/levList.html");
		session.setAttribute("files", fileList);
		return mav;
	}

	@RequestMapping(value = "levedit", method = RequestMethod.GET)
	public ModelAndView lvedit(FileListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();

		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		
		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain =uploadService.boardSelectOne(map);
		List<BoardFileDomain> fileList =  uploadService.boardSelectOneFile(map);
		
		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}

		fileListVO.setSeq(boardListDomain.getBdSeq());
		fileListVO.setContent(boardListDomain.getBdContent());
		fileListVO.setTitle(boardListDomain.getBdTitle());
		fileListVO.setIsEdit("edit");  // upload 재활용하기위해서
		
	
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);
		mav.addObject("fileLen",fileList.size());
		
		mav.setViewName("/levboard/boardEditList.html");
		return mav;
	}
	@PostMapping(value="/leveditSave")
	public ModelAndView leveeditSave(BoardFileDomain boardFileDomain, FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {
		ModelAndView mav = new ModelAndView();
		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq);
		System.out.println(bdSeq+"번째 게시물 수정완료");
		
		uploadService.bdFileRemove(boardFileDomain);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle("");
		mav = levbdSelectOneCall(fileListVO, String.valueOf(bdSeq), request);
		mav.setViewName("levboard/levList.html");
		return mav;
	}
	@RequestMapping(value = "levupload")
	public ModelAndView mypagebdUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {

		ModelAndView mav = new ModelAndView();
		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq);
		fileListVO.setContent("");
		fileListVO.setTitle("");
		mav = levbdSelectOneCall(fileListVO, String.valueOf(bdSeq), request);
		mav.setViewName("levboard/levList.html");
		return mav;
	}

	@RequestMapping(value = "mydetail", method = RequestMethod.GET)
	public ModelAndView mybdSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO,
			@RequestParam("bdSeq") String bdSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain = uploadService.boardSelectOne(map);
		System.out.println("boardListDomain" + boardListDomain);
		List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);

		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);
		mav.setViewName("/mypage/myPageList.html");
		session.setAttribute("files", fileList);
		return mav;
	}

	@RequestMapping(value = "mypageedit", method = RequestMethod.GET)
	public ModelAndView mypageedit(FileListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();

		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		
		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain =uploadService.boardSelectOne(map);
		List<BoardFileDomain> fileList =  uploadService.boardSelectOneFile(map);
		
		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}

		fileListVO.setSeq(boardListDomain.getBdSeq());
		fileListVO.setContent(boardListDomain.getBdContent());
		fileListVO.setTitle(boardListDomain.getBdTitle());
		fileListVO.setIsEdit("edit");  // upload 재활용하기위해서
		
	
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);
		mav.addObject("fileLen",fileList.size());
		
		mav.setViewName("/mypage/boardEditList.html");
		return mav;
	}
	@PostMapping(value="/mypageeditSave")
	public ModelAndView mypageeditSave(BoardFileDomain boardFileDomain, FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {
		ModelAndView mav = new ModelAndView();
		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq);
		System.out.println(bdSeq+"번째 게시물 수정완료");
		
		uploadService.bdFileRemove(boardFileDomain);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle("");
		mav = levbdSelectOneCall(fileListVO, String.valueOf(bdSeq), request);
		mav.setViewName("mypage/myPageList.html");
		return mav;
	}
	@RequestMapping(value = "mypageupload")
	public ModelAndView mypageUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {

		ModelAndView mav = new ModelAndView();
		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq);
		fileListVO.setContent("");
		fileListVO.setTitle("");
		mav = levbdSelectOneCall(fileListVO, String.valueOf(bdSeq), request);
		mav.setViewName("mypage/myPageList.html");
		return mav;
	}
}
