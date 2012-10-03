package hci;

public final class SerializableImage implements java.io.Serializable {

	static final long serialVersionUID = 0x17E30AFA13BE7L;

	private int width;
	private int height;
	private int imageType;
	private int[][] pixels;

	public SerializableImage(int width, int height, int imageType, int[][] pixels) {
	this.width = width;
	this.height = height;
	this.imageType = imageType;
	this.pixels = pixels;
	}

	public int getWidth() {
	return width;
	}

	public int getHeight() {
	return height;
	}

	public int getImageType() {
	return imageType;
	}

	public int[][] getPixels() {
	return pixels;
	}

	}