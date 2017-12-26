import java.awt.Color;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
	
	private Picture picture;
	private int width, height;
	private Color[][] matrixOfPixels; //Color has 3 coordinates: red,, blue and green  (RBG)
	private double[][] energy;  //the energy of each pixel
	private double[][] distanceTo; //distance to vertex i, j
	private int[][] colTo; //column of the vertex before vertex i,j in the matrix. The row can be inferred (i01)
	
	public SeamCarver(Picture picture) {
		if (picture == null) throw new java.lang.IllegalArgumentException("null argument");
		this.picture = new Picture(picture); //makes a Deep Copy of the original Picture passed in the constructor
		width = picture.width();
		height =  picture.height();
		matrixOfPixels = new Color[width][height];
		energy = new double[width][height];
		distanceTo = new double[width][height];
		colTo = new int[width][height]; 
		// fills out Pixel Matrix with each Pixel being a Color object
		for (int i = 0; i < width ; i++) {
			for (int j = 0; j < height; j++) {
				matrixOfPixels[i][j] = picture.get(j, i);	
			}			
		}
		// fills out energy and distance matrix
		for (int i = 0; i < width ; i++) {
			for (int j = 0; j < height; j++) {
				if (i == 0 || j == 0 ) {
					energy[i][j] = 1000;
					distanceTo[i][j] = Double.MAX_VALUE;
				}
				else {
					energy[i][j] = energyOfPixel(i, j);	
					distanceTo[i][j] = Double.MAX_VALUE;
				}
			}			
		}
	}
	
	public Picture picture() {                 // current picture
		picture = new Picture(width(), height());
		for (int i = 0; i < width ; i++) {
			for (int j = 0; j < height; j++) {
				picture.set(j, i, matrixOfPixels[i][j]);
			}
		}
		return picture;
	}
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	public  double energy(int x, int y)   {            // energy of pixel at column x and row y
		return energy[y][x];
	}
	
	public   int[] findVerticalSeam()  {               // sequence of indices for vertical seam
		double lowestDistance = Double.MAX_VALUE;
		setCoordinatesToInfinity(distanceTo); //needs to set distances in infinity before starting anything 
		int[] verticalSeam = new int[height()];
		//first relaxes all possible vertices to get the distance and colTo matrices
		for (int i = 0; i < (height()-1); i++) {       // last row cannot be relaxed
			for (int j = 0; j < width(); j++) {
				relaxVertex(i,j);
			}
		}
		//sweeps the last row to find the lowest distance and get the col of the lowest distance
		for (int j = 0; j < width(); j++) {
			if (lowestDistance > distanceTo[height()-1][j]) {
				lowestDistance = distanceTo[height()-1][j];
				verticalSeam[height()-1] = j;
			}			
		}
		//iterates from the bottom coordinate up to find the verticalSeam
		int currentIndex = verticalSeam[height()-1]; 
		for (int i = (height()-1); i > 0; i--) {
			verticalSeam[i-1] = colTo[i][currentIndex];
			currentIndex = verticalSeam[i-1];
		}	
		return verticalSeam;
	}
	
	//horizontalSeam transposes the relevant matrices, call findVerticalSeam, and transposes them back
	public int[] findHorizontalSeam() {
		//transposes the matrices
		int [] horizontalSeam = new int[width()];
		energy = transposeMatrix(energy);
		distanceTo = transposeMatrix(distanceTo);
	    int widthTemp = width;
		width = height;
		height = widthTemp;
		//call verticalSeam
		horizontalSeam = findVerticalSeam();
		//transposes the matrices back back
		energy = transposeMatrix(energy);
		distanceTo = transposeMatrix(distanceTo);
	    widthTemp = width;
		width = height;
		height = widthTemp;
		
		return horizontalSeam; 	
	}
	
	public void removeVerticalSeam(int[] seam) {
		if (seam == null) throw new java.lang.IllegalArgumentException("null argument");
		if (seam.length != height()) throw new java.lang.IllegalArgumentException("seam does not have the same height");
		for (int i = 0; i < height(); i++) {
			for (int j = seam[i]; j < (width()-1); j++) {  //fills in the hole when removing a pixel
				matrixOfPixels[i][j] = matrixOfPixels[i][j+1];
			}
			matrixOfPixels[i][(width()-1)] = null; //no loitering
		}		
		width = width - 1;
		caculateEnergyMatrix();
	}
	
	public void removeHorizontalSeam(int[] seam) {
		if (seam == null) throw new java.lang.IllegalArgumentException("null argument");
		if (seam.length != width()) throw new java.lang.IllegalArgumentException("seam does not have the same width");
		for (int j = 0; j < width(); j++) {
			for (int i = seam[j]; i < (height()-1); i++) {  //fills in the hole when removing a pixel
				matrixOfPixels[i][j] = matrixOfPixels[i][j+1];
			}
			matrixOfPixels[j][(height()-1)] = null; //no loitering
		}		
		height = height - 1;
		caculateEnergyMatrix();
	}
	
	private double squareOfTheXGradient(int row, int col) {
		double deltaRed = matrixOfPixels[row][col-1].getRed() - matrixOfPixels[row][col+1].getRed();
		double deltaGreen = matrixOfPixels[row][col-1].getGreen() - matrixOfPixels[row][col+1].getGreen();
		double deltaBlue = matrixOfPixels[row][col-1].getBlue() - matrixOfPixels[row][col+1].getBlue();
		double deltaRedSquared = Math.pow(deltaRed,2);
		double deltaGreenSquared = Math.pow(deltaGreen,2);
		double deltaBlueSquared = Math.pow(deltaBlue,2);
		return (deltaRedSquared  + deltaGreenSquared + deltaBlueSquared);
	}
	
	private double squareOfTheYGradient(int row, int col) {
		double deltaRed = matrixOfPixels[row-1][col].getRed() - matrixOfPixels[row+1][col].getRed();
		double deltaGreen = matrixOfPixels[row-1][col].getGreen() - matrixOfPixels[row+1][col].getGreen();
		double deltaBlue = matrixOfPixels[row-1][col].getBlue() - matrixOfPixels[row+1][col].getBlue();
		double deltaRedSquared = Math.pow(deltaRed,2);
		double deltaGreenSquared = Math.pow(deltaGreen,2);
		double deltaBlueSquared = Math.pow(deltaBlue,2);
		return (deltaRedSquared  + deltaGreenSquared + deltaBlueSquared);
	}
	
	private double energyOfPixel(int row, int col) {		
		return Math.sqrt(squareOfTheXGradient(row, col) + squareOfTheYGradient(row, col));
	}
	
	private void relaxVertex(int i, int j) {
		if (j > 0 && distanceTo[i+1][j-1] > distanceTo[i][j] + energy[i+1][j-1])   {
			distanceTo[i+1][j-1] = distanceTo[i][j] + energy[i+1][j-1];
			colTo[i+1][j-1] = j;
		}
		if (distanceTo[i+1][j] > distanceTo[i][j] + energy[i+1][j])   {
			distanceTo[i+1][j] = distanceTo[i][j] + energy[i+1][j];
			colTo[i+1][j] = j;
		}
		if (j < (width()-1) && distanceTo[i+1][j+1] > distanceTo[i][j] + energy[i+1][j+1])   {
			distanceTo[i+1][j+1] = distanceTo[i][j] + energy[i+1][j+1];
			colTo[i+1][j+1] = j;
		}
	}
	
	//transpose a matrix
	private double[][] transposeMatrix(double[][] matrix) {
		double[][] matrixTransposed;
		int numberOfRows = matrix.length;
		int numberOfColumns = matrix[0].length;
		matrixTransposed = new double[numberOfColumns][numberOfRows];
		for (int i = 0;i < numberOfRows; i++) {
			for (int j = 0; j < numberOfColumns;j++) {
				matrixTransposed[j][i] = matrix[i][j];
			}
		}
		return matrixTransposed;
	}
	
	private double[][] setCoordinatesToInfinity(double[][] matrix) {
		for (int i = 0; i < matrix.length ; i++) {
			for (int j = 0; j < matrix[0].length ; j++) {
				distanceTo[i][j] = Double.MAX_VALUE;
			}			
		}
		return matrix;
	}
	
	// calculate energy	
	private void caculateEnergyMatrix() {
		for (int i = 0; i < width ; i++) {			
			for (int j = 0; j < height; j++) {
				if (i == 0 || j == 0 ) {
					energy[i][j] = 1000;
				}
				else {
					energy[i][j] = energyOfPixel(i, j);	
				}
			}
		}
	}
	
	private boolean verifyIfSeamIsValid(int[] seam, int upperBoundIndex) {
		boolean isValid = true;
		int priorIndex = seam[0];
		for(int i=0; i < seam.length; i++) {
			if (seam[i] < 0 || seam[i] > upperBoundIndex) {
				isValid = false;
				break;
			} else if (seam[i] > (priorIndex+1) || seam[i] < (priorIndex-1)) {
				isValid = false;
				break;
			}
			priorIndex = seam[i];
		}
		return isValid;
	}

	public static void main(String[] args) {	
		
		//put file in root directory of the project root directory here
//		Picture picture = new Picture("HJoceanSmall.png"); 
//		SeamCarver seamCarver = new SeamCarver(picture);
//		
//		picture.show();
//		System.out.println(picture.width());
//		System.out.println(picture.height());
//		
		int[][] arr = new int[10][15];
		System.out.println(arr.length);
		System.out.println(arr[0].length);
		
		

	}

}