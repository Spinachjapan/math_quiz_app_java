/*
 * File name: Question.java
 * Purpose: For each math question, and it can be each node
 * Author: Kohei Kaburaki
 * Date: 2nd June, 2021
 * Version: 1.0.2
 * Note: This file will continue to be fixed and updated by Kohei or authoratized developers.
 */
package NetworkedMathQuiz_gui;

public class Question implements Comparable<Question> {

    public String first;
    public String operator;
    public String second;
    public String answer;
    public Question leftChild;
    public Question rightChild;

    public Question(String first, String operator, String second, String answer) {
        this.first = first;
        this.operator = operator;
        this.second = second;
        this.answer = answer;

    }

    @Override
    public String toString() {
        return answer + "(" + first + operator + second + ")";
    }

    public int compareTo(Question another) {
        if (Integer.valueOf((String) this.answer) < Integer.valueOf((String) another.answer)) {
            return -1;
        } else {
            return 1;
        }
    }

}
