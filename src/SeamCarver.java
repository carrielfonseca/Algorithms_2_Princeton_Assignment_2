import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
	
	private Picture picture;
	
	public SeamCarver(Picture picture) {
		this.picture = picture;
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