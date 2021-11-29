/*
 * File name: methodsForSorting.java
 * Purpose: For methods or three sorts and Three orders
 * Author: Kohei Kaburaki
 * Date: 2nd June, 2021
 * Version: 1.0.2
 * Note: This file will continue to be fixed and updated by Kohei or authoratized developers.
         This file refers the third party library "BinaryTree.java", which is licensed from Hans Telford under GNU
 */
package NetworkedMathQuiz_gui;

import java.util.ArrayList;

public class methodsForSorting {

    private Question root;
    private int count;
    private String traversalString;

    public methodsForSorting() {
        root = null;
        count = 0;
        traversalString = "";

    }

    // Bubble, selection and in sertion sort
    public ArrayList<Question> sortForArrayList(ArrayList<Question> arrayList, String sortType) {
        Question memo;

        if (sortType.equals("bubble")) {

            boolean bubble_flag = false;

            while (!bubble_flag) {
                bubble_flag = true;
                for (int i = 0; i < arrayList.size() - 1; i++) {

                    if (arrayList.get(i).compareTo(arrayList.get(i + 1)) == 1) {

                        memo = arrayList.get(i);
                        arrayList.set(i, arrayList.get(i + 1));

                        arrayList.set(i + 1, memo);
                        bubble_flag = false;
                    }

                }

            }

        }

        if (sortType.equals("selection")) {

            for (int i = 0; i < arrayList.size(); i++) {

                int maximum = i;

                for (int j = i + 1; j < arrayList.size(); j++) {
                    if (arrayList.get(j).compareTo(arrayList.get(i)) == 1) {
                        maximum = j;
                    }
                }

                memo = arrayList.get(i);
                arrayList.set(i, arrayList.get(maximum));
                arrayList.set(maximum, memo);

            }

        }

        if (sortType.equals("insertion")) {

            for (int i = 1; i < arrayList.size(); i++) {
                //System.out.println("i: " + Questions.get(i));
                for (int j = 0; j < i; j++) {

                    if (arrayList.get(j).compareTo(arrayList.get(i)) == 1) {

                        memo = arrayList.get(j);
                        arrayList.set(j, arrayList.get(i));
                        arrayList.set(i, memo);

                    }

                }

            }

        }

        return arrayList;

    }

    public Question getRoot() {
        return root;
    }

    // For structuring binary tree
    public void addToBinaryTree(Question data) {

        if (root == null) {
            root = data;
            count++;
        } else {
            Question current = root;
            Question parent;

            while (true) {
                parent = current;

                if (data.compareTo(current) == -1) {
                    current = current.leftChild;
                    if (current == null) {
                        parent.leftChild = data;
                        count++;
                        return;
                    }
                } else {
                    current = current.rightChild;
                    if (current == null) {
                        parent.rightChild = data;
                        count++;
                        return;
                    }
                }

            }

        }

    }

    public void preOrder(Question root) {

        if (root != null) {
            traversalString += " " + root.toString();
            preOrder(root.leftChild);
            preOrder(root.rightChild);
        }
    }

    public void postOrder(Question root) {

        if (root != null) {
            postOrder(root.leftChild);
            postOrder(root.rightChild);
            traversalString += " " + root.toString();

        }
    }

    public void inOrder(Question root) {

        if (root != null) {
            inOrder(root.leftChild);
            traversalString += " " + root.toString();
            inOrder(root.rightChild);
        }
    }

    //displayed in BinaryTreeTextarea
    public String getTraversalString() {
        return traversalString;
    }

}
