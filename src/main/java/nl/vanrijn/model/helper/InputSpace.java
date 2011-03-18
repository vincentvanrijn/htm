package nl.vanrijn.model.helper;

public class InputSpace {

	private int	xPos;

	private int	yPos;

	private int	sourceInput;

	public InputSpace(int xPos, int yPos, int sourceInput) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.sourceInput = sourceInput;
	}

	@Override
	public boolean equals(Object obj) {
		boolean returnValue = false;
		if (this == obj) {
			returnValue = true;
		} else
			if (!(obj instanceof InputSpace)) {
				returnValue = false;
			} else {

				InputSpace inputSpace = (InputSpace) obj;
				if (this.xPos == inputSpace.getxPos() && (this.yPos == inputSpace.getyPos())
						&& this.sourceInput == inputSpace.getSourceInput()) {
					returnValue = true;

				}
			}
		return returnValue;

	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public int getSourceInput() {
		return sourceInput;
	}

	public void setSourceInput(int sourceInput) {
		this.sourceInput = sourceInput;
	}
}
