package com.co.kr.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderMethodName="builder")
public class RoomListDomain {
	private String rmSeq;
	private String mbId;
	private String rmTitle;
	private String rmType;
	private String rmContent;
	private String rmMemo;
	private String rmCreateAt;
	private String rmUpdateAt;

}