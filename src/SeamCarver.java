import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
	
	private Picture picture;
	private int widht, height;	
	
	public SeamCarver(Picture picture) {
		this.picture = picture;
		widht = picture.width();
		height =  picture.height();
	}
	
	public int widht() {
		return widht;
	}
	
	public int height() {
		return height;
	}

	public static void main(String[] args) {	
		
		//put file in root directory of the project root directory here
		Picture picture = new Picture("HJoceanSmall.png"); 
		SeamCarver seamCarver = new SeamCarver(picture);
		
		picture.show();
		System.out.println(picture.width());
		System.out.println(picture.height());
		
		

	}

}