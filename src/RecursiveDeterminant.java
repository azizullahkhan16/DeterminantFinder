import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The RecursiveDeterminant class calculates the determinant of a square matrix using recursive methods.
 * It prompts the user to enter the number of rows and columns of the matrix, fills the matrix with user input,
 * and then calculates and prints the determinant of the matrix.
 */
public class RecursiveDeterminant {

    /**
     * Calculates and prints the determinant of a square matrix.
     * It prompts the user to enter the number of rows and columns of the matrix,
     * fills the matrix with user input, and then calculates and prints the determinant of the matrix.
     */
    public static void GetDeterminant() {
        int rowsColumns = 0; // Variable to store the number of rows and columns
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter the number of rows and columns until a valid input is provided
        while (rowsColumns <= 0) {
            try {
                System.out.print("Enter the number of rows and columns: ");
                rowsColumns = scanner.nextInt();

                if (rowsColumns <= 0) {
                    throw new IllegalArgumentException("Number of rows and columns must be positive.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter an integer value.");
                scanner.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        double[][] matrix = new double[rowsColumns][rowsColumns]; // Create a 2D array for the matrix

        Print2DArray(matrix); // Print the initial matrix

        boolean isFilled;

        // Loop through each element of the matrix and fill it with user input
        for (int i = 0; i < rowsColumns; i++) {
            for (int j = 0; j < rowsColumns; j++) {
                isFilled = false; // Flag to indicate if the element is filled

                // Prompt the user to fill the current position until a valid input is provided
                while (!isFilled) {
                    try {
                        System.out.print("Fill the (R" + (i + 1) + ", C" + (j + 1) + ") position: ");
                        matrix[i][j] = scanner.nextDouble();
                        isFilled = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input! Please enter a numeric value.");
                        scanner.nextLine();
                    }
                }

                Print2DArray(matrix); // Print the updated matrix

                // Optimization: Check if the current row or column contains all zero elements, then det(A) = 0
                if (j == rowsColumns - 1 && RowElementsZero(matrix, i)) {
                    System.out.println("The determinant is zero because all the elements of R" + (i + 1) + " are zeros.");
                    return;
                }

                if (i == rowsColumns - 1 && ColumnElementsZero(matrix, j)) {
                    System.out.println("The determinant is zero because all the elements of C" + (j + 1) + " are zeros.");
                    return;
                }

                // Optimization: If A has two identical rows or two identical columns, then det(A) = 0
                if( i > 0 && j == rowsColumns - 1 && DuplicateRow(matrix, i)){
                    System.out.println("The determinant is zero because R" + (i+1) + " is duplicated.");
                    return;
                }

                if( j > 0 && i == rowsColumns - 1 && DuplicateColumn(matrix, j)){
                    System.out.println("The determinant is zero because C" + (j+1) + " is duplicated.");
                    return;
                }
            }
        }

        // Calculate and print the determinant of the given matrix
        System.out.println("The determinant of the given matrix is: " + FindDeterminant(matrix));

    }


    /**
     * Finds the determinant of a square matrix recursively using recursive cofactor expansion.
     *
     * @param matrix  The 2D array representing the matrix.
     * @return        The determinant of the matrix.
     */
    private static double FindDeterminant(double[][] matrix) {
        if (matrix.length == 1) {
            return matrix[0][0]; // Base case: determinant of a 1x1 matrix is its only element
        }

        // Optimization: We first find the row or column with the most number of zeros, so that we find less cofactors
        // the first element of the optimal is the ith row or jth column with most number of zeros.
        // Second element is the flag which indicates whether it is row or a column
        int[] optimal = RowColumnWithMostZeros(matrix);


        double determinant = 0;

        // Iterate over the elements of the first row and calculate the determinant recursively
        if(optimal[1] == 0) {
            for (int i = 0; i < matrix[optimal[0]].length; i++) {
                if (matrix[optimal[0]][i] == 0)
                    continue; // Optimization: If the element of the first row is zero, so no need to find its cofactor
                determinant += (matrix[optimal[0]][i] * Math.pow(-1, optimal[0]+i) * FindDeterminant(FindMinorMatrix(matrix, optimal[0], i)));
            }
        }else {
            for (int i = 0; i < matrix.length; i++) {
                if (matrix[i][optimal[0]] == 0)
                    continue; // Optimization: If the element of the first row is zero, so no need to find its cofactor
                determinant += (matrix[i][optimal[0]] * Math.pow(-1, optimal[0]+i) * FindDeterminant(FindMinorMatrix(matrix, i, optimal[0])));
            }
        }

        return determinant;
    }

    /**
     * Checks if all elements of a given row in a matrix are zero.
     *
     * @param matrix The matrix to check.
     * @param row    The row to check.
     * @return True if all elements of the row are zero, false otherwise.
     */
    private static boolean RowElementsZero(double[][] matrix, int row) {
        for (int i = 0; i < matrix[row].length; i++) {
            if (matrix[row][i] != 0) {
                return false; // If any non-zero element is found, return false
            }
        }

        return true; // All elements are zero
    }

    /**
     * Checks if all elements of a given column in a matrix are zero.
     *
     * @param matrix The matrix to check.
     * @param column The column to check.
     * @return True if all elements of the column are zero, false otherwise.
     */
    private static boolean ColumnElementsZero(double[][] matrix, int column) {
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][column] != 0) {
                return false; // If any non-zero element is found, return false
            }
        }

        return true; // All elements are zero
    }

    /**
     * Checks if a row is a duplicate of any previous row in a matrix.
     *
     * @param matrix  The 2D array representing the matrix.
     * @param row     The row index to check.
     * @return        Returns true if the row is a duplicate of any previous row, false otherwise.
     */
    private static boolean DuplicateRow(double[][] matrix, int row) {
        for (int i = row - 1; i >= 0; i--) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[row][j] != matrix[i][j]) break;
                if (j == matrix[i].length - 1) return true;
            }
        }

        return false;
    }

    /**
     * Checks if a column is a duplicate of any previous column in a matrix.
     *
     * @param matrix  The 2D array representing the matrix.
     * @param column  The column index to check.
     * @return        Returns true if the column is a duplicate of any previous column, false otherwise.
     */
    private static boolean DuplicateColumn(double[][] matrix, int column) {
        for (int i = column - 1; i >= 0; i--) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j][column] != matrix[j][i]) break;
                if (j == matrix.length - 1) return true;

            }
        }

        return false;
    }

    /**
     * Finds the row in a matrix with the most zeros.
     *
     * @param matrix  The 2D array representing the matrix.
     * @return        An array containing the index of the row with the most zeros and the count of zeros.
     */
    private static int[] RowWithMostZeros(double[][] matrix) {
        int countZero = 0;
        int max = Integer.MIN_VALUE;
        int row = 0;

        for (int i = 0; i < matrix.length; i++) {
            countZero = 0;
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0) {
                    countZero++;
                }
            }

            if (countZero > max) {
                max = countZero;
                row = i;
            }
        }

        return new int[]{row, max};
    }

    /**
     * Finds the column in a matrix with the most zeros.
     *
     * @param matrix  The 2D array representing the matrix.
     * @return        An array containing the index of the column with the most zeros and the count of zeros.
     */
    private static int[] ColumnWithMostZeros(double[][] matrix) {
        int countZero = 0;
        int max = Integer.MIN_VALUE;
        int column = 0;

        for (int i = 0; i < matrix[0].length; i++) {
            countZero = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j][i] == 0) {
                    countZero++;
                }
            }

            if (countZero > max) {
                max = countZero;
                column = i;
            }
        }

        return new int[]{column, max};
    }

    /**
     * Finds whether the row or the column in a matrix has the most zeros.
     *
     * @param matrix  The 2D array representing the matrix.
     * @return        An array containing the index of the row or column with the most zeros (based on the maximum count of zeros) and a flag indicating whether it's a row or column (0 for row, 1 for column).
     */
    private static int[] RowColumnWithMostZeros(double[][] matrix) {
        int[] row = RowWithMostZeros(matrix);
        int[] column = ColumnWithMostZeros(matrix);

        if (row[1] > column[1]) {
            return new int[]{row[0], 0};
        } else {
            return new int[]{column[0], 1};
        }
    }


    /**
     * Creates a minor matrix by removing a specified row and column from a given matrix.
     *
     * @param matrix The matrix from which the minor matrix is created.
     * @param row    The row to be removed from the matrix.
     * @param column The column to be removed from the matrix.
     * @return The minor matrix.
     */
    private static double[][] FindMinorMatrix(double[][] matrix, int row, int column) {
        double[][] minor = new double[matrix.length - 1][matrix[0].length - 1];

        int k = 0;

        for (int i = 0; i < matrix.length; i++) {
            if (i == row) {
                continue; // Skip the specified row
            }
            int l = 0;
            for (int j = 0; j < matrix[i].length; j++) {
                if (j == column) {
                    continue; // Skip the specified column
                }
                minor[k][l] = matrix[i][j];
                l++;
            }
            k++;
        }

        return minor;
    }

    /**
     * Prints a 2D array (matrix) in a formatted manner.
     *
     * @param matrix The matrix to be printed.
     */
    private static void Print2DArray(double[][] matrix) {
        // Print the column headers
        for (int i = 0; i < matrix[0].length; i++) {
            if (i == 0) {
                System.out.print("       C" + (i + 1));
            } else {
                if (i < 9) {
                    System.out.print("       C" + (i + 1));
                } else {
                    System.out.print("      C" + (i + 1));
                }
            }
        }

        System.out.println();

        // Print the matrix elements
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (j == 0) {
                    if (i < 9) {
                        System.out.print("R" + (i + 1) + "    " + matrix[i][j] + "      ");
                    } else {
                        System.out.print("R" + (i + 1) + "   " + matrix[i][j] + "      ");
                    }
                } else {
                    System.out.print(matrix[i][j] + "      ");
                }
            }
            System.out.println();
        }

        System.out.println();
    }
}
