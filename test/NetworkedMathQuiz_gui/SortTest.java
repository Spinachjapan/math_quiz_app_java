/*
 * File name: SortTest.java
 * Purpose: Testing sort methods and order methods
 * Author: Kohei Kaburaki
 * Date: 2nd June, 2021
 * Version: 1.0.2
 */
package NetworkedMathQuiz_gui;

import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class SortTest {

    // These variables are for testing
    Question question1 = new Question("3", "+", "2", "5");
    Question question2 = new Question("6", "-", "2", "4");
    Question question3 = new Question("3", "*", "1", "3");
    Question question4 = new Question("4", "/", "2", "2");

    ArrayList<Question> arrayList = new ArrayList<Question>();

    methodsForSorting sortingTest = new methodsForSorting();

    public SortTest() {

        arrayList.add(question1);
        arrayList.add(question2);
        arrayList.add(question3);
        arrayList.add(question4);

        question2.leftChild = question3;
        question2.rightChild = question1;
        question3.leftChild = question4;

    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    /**
     * Test of sortForArrayList method, of class Sort.
     */
    @Test
    public void bubbleTestSortForArrayList() {
        System.out.println("sortForArrayList(bubble)");

        //Setting the expected result
        ArrayList<Question> bubbleResultArrayList = new ArrayList<Question>();
        bubbleResultArrayList.add(question4);
        bubbleResultArrayList.add(question3);
        bubbleResultArrayList.add(question2);
        bubbleResultArrayList.add(question1);
        assertEquals(bubbleResultArrayList, sortingTest.sortForArrayList(arrayList, "bubble"));

    }

    @Test
    public void selectionTestSortForArrayList() {
        System.out.println("sortForArrayList(selection)");
        //Setting the expected result
        ArrayList<Question> selectionResultArrayList = new ArrayList<Question>();
        selectionResultArrayList.add(question1);
        selectionResultArrayList.add(question2);
        selectionResultArrayList.add(question3);
        selectionResultArrayList.add(question4);
        assertEquals(selectionResultArrayList, sortingTest.sortForArrayList(arrayList, "selection"));

    }

    @Test
    public void insertionTestSortForArrayList() {
        System.out.println("sortForArrayList(insertion)");
        //Setting the expected result
        ArrayList<Question> insertionResultArrayList = new ArrayList<Question>();
        insertionResultArrayList.add(question4);
        insertionResultArrayList.add(question3);
        insertionResultArrayList.add(question2);
        insertionResultArrayList.add(question1);
        assertEquals(insertionResultArrayList, sortingTest.sortForArrayList(arrayList, "bubble"));

    }

    @Test
    public void preTestGetTraversalString() {
        System.out.println("getTraversalString(pre-order)");
        sortingTest.preOrder(question2);
        //Setting the expected result
        String resultPreorder = " 4(6-2) 3(3*1) 2(4/2) 5(3+2)";
        assertEquals(resultPreorder, sortingTest.getTraversalString());

    }

    @Test
    public void inTestGetTraversalString() {
        System.out.println("getTraversalString(in-order)");
        sortingTest.inOrder(question2);
        //Setting the expected result
        String resultInorder = " 2(4/2) 3(3*1) 4(6-2) 5(3+2)";
        assertEquals(resultInorder, sortingTest.getTraversalString());

    }

    @Test
    public void postTestGetTraversalString() {
        System.out.println("getTraversalString(post-order)");
        sortingTest.postOrder(question2);
        //Setting the expected result
        String resultPostorder = " 2(4/2) 3(3*1) 5(3+2) 4(6-2)";
        assertEquals(resultPostorder, sortingTest.getTraversalString());

    }

}
