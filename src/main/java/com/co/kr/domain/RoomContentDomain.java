package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class RoomContentDomain {

	private Integer rmSeq;
	private String mbId;
	private String rmType;
	private String rmTitle;
	private String rmContent;
	private String rmMemo;
}