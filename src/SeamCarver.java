import java.awt.Color;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
	
	private Picture picture;
	private int widht, height;
	private Color[][] matrixOfPixels; //Color has 3 coordinates: red,, blue and green  (RBG)
	private int[][] energy; //the energy represents a distance	
	
	public SeamCarver(Picture picture) {
		this.picture = picture;
		widht = picture.width();
		height =  picture.height();
		for (int i = 0; i < widht ; i++) {
			for (int j = 0; j < height; j++) {
				matrixOfPixels[i][j] = picture.get(j, i);	
			}			
		}
	}
	
	public int widht() {
		return widht;
	}
	
	public int height() {
		return height;
	}
	
	
//	private int squareOfTheXGradient(int row, int col) {
//		int deltaRedSquared = matrixOfPixels[row][col-1]
//		return 
//	}

	public static void main(String[] args) {	
		
		//put file in root directory of the project root directory here
		Picture picture = new Picture("HJoceanSmall.png"); 
		SeamCarver seamCarver = new SeamCarver(picture);
		
		picture.show();
		System.out.println(picture.width());
		System.out.println(picture.height());
		
		

	}

}