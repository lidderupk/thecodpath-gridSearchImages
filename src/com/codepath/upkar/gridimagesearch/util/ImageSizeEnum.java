package com.codepath.upkar.gridimagesearch.util;

public enum ImageSizeEnum {
	ICON(1, "icon"), MEDIUM(2, "medium"), XXLARGE(3, "xxlarge"), HUGE(4, "huge");
	private int num;
	private String value;

	private ImageSizeEnum(int num, String value) {
		this.num = num;
		this.value = value;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
