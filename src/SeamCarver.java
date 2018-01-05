import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
	
	private Picture picture;
	private int width, height;
	private int[][] matrixOfPixels; //Color has 3 coordinates: red,, blue and green  (RBG)
	private int[][] colTo; //column of the vertex before vertex i,j in the matrix in the minimum path. The row can be inferred (i-1)
	
	public SeamCarver(Picture picture) {
		if (picture == null) throw new java.lang.IllegalArgumentException("null argument");
		this.picture = new Picture(picture); //makes a Deep Copy of the original Picture passed in the constructor
		width = picture.width();
		height =  picture.height();
		matrixOfPixels = new int[height][width];
		colTo = new int[height][width]; 
		// fills out Pixel Matrix with each Pixel being a Color object
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width ; j++) {
				matrixOfPixels[i][j] = picture.getRGB(j, i);	
			}			
		}
	}
	
	public Picture picture() {                 // current picture
		picture = new Picture(width, height);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width ; j++) {
				picture.setRGB(j, i, matrixOfPixels[i][j]);
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
		if (x < 0 || x > (width-1) || y < 0 || y > (height-1)) throw new java.lang.IllegalArgumentException("index out of bound");
		if (y == 0 || y == (height-1) || x == 0 || x == (width-1)) {
			return 1000;
		} else {
			return Math.sqrt(squareOfTheXGradient(y, x) + squareOfTheYGradient(y, x));
		}
		
	}
	
	public   int[] findVerticalSeam()  {               // sequence of indices for vertical seam
		double lowestDistance = Double.POSITIVE_INFINITY;
		double[][] energy = caculateEnergyMatrix();
		double[][] distanceTo = new double[height][width];
		distanceTo = setCoordinatesToInfinity(distanceTo); //needs to set distances in infinity before starting anything 
		for (int j = 0; j < width; j++) { 
			distanceTo[0][j] = energy(j, 0);
		}
		int[] verticalSeam = new int[height];
		//first relaxes all possible vertices to get the distance and colTo matrices
		for (int i = 0; i < (height-1); i++) {       // last row cannot be relaxed
			for (int j = 0; j < width; j++) {
				relaxVertex(i,j, energy, distanceTo);				
			}
		}
		//sweeps the last row to find the lowest distance and get the col of the lowest distance
		for (int j = 0; j < width; j++) {
			if (lowestDistance > distanceTo[height-1][j]) {
				lowestDistance = distanceTo[height-1][j];
				verticalSeam[height-1] = j;
			}			
		}
		//iterates from the up coordinate down to find the verticalSeam
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
		colTo = transposeMatrix(colTo);
		matrixOfPixels = transposeMatrix(matrixOfPixels);
		
	    int widthTemp = width;
		width = height;
		height = widthTemp;
		//call verticalSeam
		horizontalSeam = findVerticalSeam();
		//transposes the matrix back
		matrixOfPixels = transposeMatrix(matrixOfPixels);
		colTo = transposeMatrix(colTo);
	    widthTemp = width;
		width = height;
		height = widthTemp;
		
		return horizontalSeam; 	
	}
	
	public void removeVerticalSeam(int[] seam) {
		if (seam == null) throw new java.lang.IllegalArgumentException("null argument");
		if (seam.length != height()) throw new java.lang.IllegalArgumentException("seam does not have the same height");
		if (!verifyIfSeamIsValid(seam, width()-1)) throw new java.lang.IllegalArgumentException("seam is not valid");
		if (width() <= 1) throw new java.lang.IllegalArgumentException("picture is of minimal width");
		int[][] matrixOfPixelsNew = new int[height][width-1];
		for (int i = 0; i < height(); i++) {
			for (int j = 0; j < (width()-1); j++) {  //fills in the hole when removing a pixel
				if (j < seam[i]) {
					matrixOfPixelsNew[i][j] = matrixOfPixels[i][j];
				} else {   // j >= seam[i]
					matrixOfPixelsNew[i][j] = matrixOfPixels[i][j+1]; //skips  the hole left by coordinate[i][j]
				}				
			}
//			matrixOfPixels[i][(width-1)] = null; //no loitering
		}		
		width = width - 1;
		matrixOfPixels = matrixOfPixelsNew;
		caculateEnergyMatrix();
	}
	
	public void removeHorizontalSeam(int[] seam) {
		if (seam == null) throw new java.lang.IllegalArgumentException("null argument");
		if (seam.length != width()) throw new java.lang.IllegalArgumentException("seam does not have the same width");
		if (!verifyIfSeamIsValid(seam, height()-1)) throw new java.lang.IllegalArgumentException("seam is not valid");
		if (height() <= 1) throw new java.lang.IllegalArgumentException("picture is of minimal height");
		int[][] matrixOfPixelsNew = new int[height-1][width];
		for (int j = 0; j < width(); j++) {
			for (int i = 0; i < (height()-1); i++) {  //fills in the hole when removing a pixel
				if (i < seam[j]) {
					matrixOfPixelsNew[i][j] = matrixOfPixels[i][j];
				} else {   // i >= seam[j]
					matrixOfPixelsNew[i][j] = matrixOfPixels[i+1][j]; //skips  the hole left by coordinate[i][j]
				}			
				
			}
		}		
		height = height - 1;
		matrixOfPixels = matrixOfPixelsNew;
		caculateEnergyMatrix();
	}
	
	private double squareOfTheXGradient(int row, int col) {
		double deltaRed = new Color(matrixOfPixels[row][col-1]).getRed() - new Color(matrixOfPixels[row][col+1]).getRed();
		double deltaGreen = new Color(matrixOfPixels[row][col-1]).getGreen() - new Color(matrixOfPixels[row][col+1]).getGreen();
		double deltaBlue = new Color(matrixOfPixels[row][col-1]).getBlue() - new Color(matrixOfPixels[row][col+1]).getBlue();
		double deltaRedSquared = Math.pow(deltaRed,2);
		double deltaGreenSquared = Math.pow(deltaGreen,2);
		double deltaBlueSquared = Math.pow(deltaBlue,2);
		return (deltaRedSquared  + deltaGreenSquared + deltaBlueSquared);
	}
	
	private double squareOfTheYGradient(int row, int col) {
		double deltaRed = new Color(matrixOfPixels[row-1][col]).getRed() - new Color(matrixOfPixels[row+1][col]).getRed();
		double deltaGreen = new Color(matrixOfPixels[row-1][col]).getGreen() - new Color(matrixOfPixels[row+1][col]).getGreen();
		double deltaBlue = new Color(matrixOfPixels[row-1][col]).getBlue() - new Color(matrixOfPixels[row+1][col]).getBlue();
		double deltaRedSquared = Math.pow(deltaRed,2);
		double deltaGreenSquared = Math.pow(deltaGreen,2);
		double deltaBlueSquared = Math.pow(deltaBlue,2);
		return (deltaRedSquared  + deltaGreenSquared + deltaBlueSquared);
	}	
	
	private void relaxVertex(int i, int j, double[][] energy, double[][] distanceTo) {
		if (j > 0 && distanceTo[i+1][j-1] > (distanceTo[i][j] + energy[i+1][j-1]))   {
			distanceTo[i+1][j-1] = (distanceTo[i][j] + energy[i+1][j-1]);
			colTo[i+1][j-1] = j;
		}
		if (distanceTo[i+1][j] > (distanceTo[i][j] + energy[i+1][j]))  {
			distanceTo[i+1][j] = (distanceTo[i][j] + energy[i+1][j]);
			colTo[i+1][j] = j;
		}
		if (j < (width()-1) && distanceTo[i+1][j+1] > distanceTo[i][j] + energy[i+1][j+1])   {
			distanceTo[i+1][j+1] = (distanceTo[i][j] + energy[i+1][j+1]);
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
	
	//transpose a matrix
	private int[][] transposeMatrix(int[][] matrix) {
		int[][] matrixTransposed;
		int numberOfRows = matrix.length;
		int numberOfColumns = matrix[0].length;
		matrixTransposed = new int[numberOfColumns][numberOfRows];
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
				matrix[i][j] = Double.POSITIVE_INFINITY;
			}			
		}
		return matrix;
	}
	
	// calculate energy	
	private double[][] caculateEnergyMatrix() {
		double[][] energy = new double[height][width];
		for (int i = 0; i < height ; i++) {			
			for (int j = 0; j < width; j++) {
				if (i == 0 || i == (height-1) || j == 0 || j == (width-1)) {
					energy[i][j] = 1000;
				}
				else {
					energy[i][j] = energy(j, i);
				}
			}
		}
		return energy;
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
//		int[][] arr = new int[10][15];
//		System.out.println(arr.length);
//		System.out.println(arr[0].length);
		
		Picture picture = new Picture("6x5.png"); 
	
        SeamCarver sc = new SeamCarver(picture);
        
        StdOut.printf("Printing distance calculated for each pixel.\n");
        
        double[][] energy = sc.caculateEnergyMatrix();
        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                StdOut.printf("%9.0f ", energy[row][col]);
            StdOut.println();
        }      
		

	}

}