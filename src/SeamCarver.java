import java.awt.Color;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
	
	private Picture picture;
	private int widht, height;
	private Color[][] matrixOfPixels; //Color has 3 coordinates: red,, blue and green  (RBG)
	private int[][] energy;  //the energy of each pixel
	private int[][] distanceTo; //distance to vertex i, j
	private int[][] colTo; //column of the vertex before vertex i,j in the matrix. The row can be inferred (i01)
	
	public SeamCarver(Picture picture) {
		this.picture = new Picture(picture); //makes a Deep Copy of the original Picture passed in the constructor
		widht = picture.width();
		height =  picture.height();
		matrixOfPixels = new Color[widht][height];
		energy = new int[widht][height];
		distanceTo = new int[widht][height];
		colTo = new int[widht][height]; 
		// fills out Pixel Matrix with each Pixel being a Color object
		for (int i = 0; i < widht ; i++) {
			for (int j = 0; j < height; j++) {
				matrixOfPixels[i][j] = picture.get(j, i);	
			}			
		}
		// fills out energy and distance matrix
		for (int i = 0; i < widht ; i++) {
			for (int j = 0; j < height; j++) {
				if (i == 0 || j == 0 ) {
					energy[i][j] = 1000;
					distanceTo[i][j] = Integer.MAX_VALUE;
				}
				else {
					energy[i][j] = energyOfPixel(i, j);	
					distanceTo[i][j] = Integer.MAX_VALUE;
				}
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
	
	private void relaxVertex(int i, int j) {
		if (distanceTo[i+1][j-1] > distanceTo[i][j] + energy[i+1][j-1])   {
			distanceTo[i+1][j-1] = distanceTo[i][j] + energy[i+1][j-1];
		}
		if (distanceTo[i+1][j] > distanceTo[i][j] + energy[i+1][j])   {
			distanceTo[i+1][j] = distanceTo[i][j] + energy[i+1][j];
		}
		if (distanceTo[i+1][j+1] > distanceTo[i][j] + energy[i+1][j+1])   {
			distanceTo[i+1][j+1] = distanceTo[i][j] + energy[i+1][j+1];
		}
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