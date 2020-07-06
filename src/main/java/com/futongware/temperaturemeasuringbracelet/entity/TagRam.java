package com.futongware.temperaturemeasuringbracelet.entity;

import lombok.Data;

@Data
public class TagRam {
	private String itemId;
	private String item;
	private Integer bankId;
	private Integer bankStart;
	private Integer bytes;
}
