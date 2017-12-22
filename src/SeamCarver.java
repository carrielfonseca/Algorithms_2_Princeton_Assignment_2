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
		matrixOfPixels = new Color[widht][height];
		// Although Picure Matrix is upside down, calculations will not be different
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
	
	private int squareOfTheXGradient(int row, int col) {
		int deltaRed = matrixOfPixels[row][col-1].getRed() - matrixOfPixels[row][col+1].getRed();
		int deltaGreen = matrixOfPixels[row][col-1].getGreen() - matrixOfPixels[row][col+1].getGreen();
		int deltaBlue = matrixOfPixels[row][col-1].getBlue() - matrixOfPixels[row][col+1].getBlue();
		int deltaRedSquared = (int) Math.pow(deltaRed,2);
		int deltaGreenSquared = (int) Math.pow(deltaGreen,2);
		int deltaBlueSquared = (int) Math.pow(deltaBlue,2);
		return (deltaRedSquared  + deltaGreenSquared + deltaBlueSquared);
	}
	
	private int squareOfTheYGradient(int row, int col) {
		int deltaRed = matrixOfPixels[row-1][col].getRed() - matrixOfPixels[row+1][col].getRed();
		int deltaGreen = matrixOfPixels[row-1][col].getGreen() - matrixOfPixels[row+1][col].getGreen();
		int deltaBlue = matrixOfPixels[row-1][col].getBlue() - matrixOfPixels[row+1][col].getBlue();
		int deltaRedSquared = (int) Math.pow(deltaRed,2);
		int deltaGreenSquared = (int) Math.pow(deltaGreen,2);
		int deltaBlueSquared = (int) Math.pow(deltaBlue,2);
		return (deltaRedSquared  + deltaGreenSquared + deltaBlueSquared);
	}
	
	private int energyOfPixel(int row, int col) {		
		return squareOfTheXGradient(row, col) + squareOfTheYGradient(row, col);
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