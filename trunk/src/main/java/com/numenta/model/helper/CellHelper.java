package com.numenta.model.helper;

public class CellHelper {
	private int time;
	private int cellNumber;
	public CellHelper(int cellNumber , int time) {
		this.time=time;
		this.cellNumber=cellNumber;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getCellNumber() {
		return cellNumber;
	}
	public void setCellNumber(int cellNumber) {
		this.cellNumber = cellNumber;
	}

}
