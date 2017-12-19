import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
	
	private Picture picture;
	
	public SeamCarver(Picture picture) {
		this.picture = picture;
	}

	public static void main(String[] args) {	
		
		
		Picture picture = new Picture("C:/Users/ffonseca/Desktop/Fabio/HJoceanSmall.png");
		SeamCarver seamCarver = new SeamCarver(picture);
		
		picture.show();
		System.out.println(picture.width());
		System.out.println(picture.height());
		
		

	}

}
