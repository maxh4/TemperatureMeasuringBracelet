package com.futongware.temperaturemeasuringbracelet.entity;

public class TagRam {
	private String itemId;
	private String item;
	private Integer bankId;
	private Integer bankStart;
	private Integer bytes;
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public Integer getBankId() {
		return bankId;
	}
	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}
	public Integer getBankStart() {
		return bankStart;
	}
	public void setBankStart(Integer bankStart) {
		this.bankStart = bankStart;
	}
	public Integer getBytes() {
		return bytes;
	}
	public void setBytes(Integer bytes) {
		this.bytes = bytes;
	}
	@Override
	public String toString() {
		return "TagRam [itemId=" + itemId + ", item=" + item + ", bankId=" + bankId + ", bankStart=" + bankStart
				+ ", bytes=" + bytes + "]";
	}
	
}
