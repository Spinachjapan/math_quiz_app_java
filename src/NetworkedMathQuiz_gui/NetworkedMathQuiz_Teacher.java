/*
 * File name: NetworkedMathQuiz_Teacher
 * Purpose: For UI of teacher application
 * Author: Kohei Kaburaki
 * Date: 2nd June, 2021
 * Version: 1.0.2
 * Note: This file will continue to be fixed and updated by Kohei or authoratized developers.
 */
package NetworkedMathQuiz_gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.LinkedList;
import java.util.List;

public class NetworkedMathQuiz_Teacher extends javax.swing.JFrame {

    private DefaultTableModel questionsTableModel;
    private int eachAnswer;
    private String stringEachAnswer;
    private String[][] sortedList;
    private JFrame frame = new JFrame();
    private String BTStore = "";
    static private ServerSocket serverSocket;
    static private Socket socket;
    static private DataInputStream dataInputStream;
    static private DataOutputStream dataOutputStream;
    static private List<String> incorrectList = new LinkedList<>();
    private String orderedFlag = "empty";

    // Modeling MathQuizTable  
    public NetworkedMathQuiz_Teacher() {

        String[] columnNames_questions = new String[]{"LOp", "op", "ROp", "=", "Ans"};
        questionsTableModel = new DefaultTableModel();
        questionsTableModel.setColumnIdentifiers(columnNames_questions);
        //Object[] defaultData_questions = {"-", "-", "-", "-", "-"};   
        //questionsTableModel.addRow(defaultData_questions); 

        initComponents();
        realizeQuestionsColumns();

    }

    //Setting MathQuizTable
    public void realizeQuestionsColumns() {

        double[] columnWidthPercentage = {0.2f, 0.2f, 0.2f, 0.2f, 0.2f};
        int tableWidth = questionsTable.getWidth();
        TableColumn column;
        TableColumnModel tableColumnModel = questionsTable.getColumnModel();
        int cantCols = tableColumnModel.getColumnCount();

        for (int i = 0; i < cantCols; i++) {
            column = tableColumnModel.getColumn(i);
            float pWidth = Math.round(columnWidthPercentage[i] * tableWidth);
            column.setPreferredWidth((int) pWidth);
        }
    }

    //Adding rows 
    public void addRows(String OPTR, String FN, String SN) {
        answerTextfield.setText(stringEachAnswer);
        String[] questionsArray = new String[5];
        questionsArray[0] = FN;
        questionsArray[1] = OPTR;
        questionsArray[2] = SN;
        questionsArray[3] = "=";
        questionsArray[4] = stringEachAnswer;
        questionsTableModel.addRow(questionsArray);
        orderedFlag = "wait";

        String qForStudent = FN + " " + OPTR + " " + SN + " " + "=";

        BTStore += qForStudent + " " + stringEachAnswer + "\n";
        BinaryTreeTextArea.setText(BTStore);
    }

    // Sorting methods
    public void callSortMethod(String setSortType) {
        ArrayList<Question> QuestionsArrayList = new ArrayList<Question>();
        for (int row = 0; row < questionsTableModel.getRowCount(); row++) {
            Question single_question = new Question(String.valueOf(questionsTableModel.getValueAt(row, 0)),
                    String.valueOf(questionsTableModel.getValueAt(row, 1)),
                    String.valueOf(questionsTableModel.getValueAt(row, 2)),
                    String.valueOf(questionsTableModel.getValueAt(row, 4)));
            QuestionsArrayList.add(single_question);
        }

        methodsForSorting QBT = new methodsForSorting();
        ArrayList<Question> getResult = QBT.sortForArrayList(QuestionsArrayList, setSortType);
        sortedList = new String[getResult.size()][];
        int counter = 0;

        for (int i = 0; i < getResult.size(); i++) {
            String[] eachRow = new String[5];

            eachRow[0] = getResult.get(i).first;
            eachRow[1] = getResult.get(i).operator;
            eachRow[2] = getResult.get(i).second;
            eachRow[3] = "=";
            eachRow[4] = getResult.get(i).answer;

            sortedList[counter] = eachRow;

            counter++;
        }

        for (int i = questionsTableModel.getRowCount() - 1; i > -1; i--) {
            questionsTableModel.removeRow(i);
        }

        for (int row = 0; row < sortedList.length; row++) {
            questionsTableModel.addRow(sortedList[row]);
        }

    }

    //method for binary tree order
    public void binaryTreeOrder(String type) {
        if (questionsTableModel.getRowCount() == 0) {
            BinaryTreeTextArea.setText("There are no math questions to display.");

        } else {
            methodsForSorting QBT = new methodsForSorting();
            for (int row = 0; row < questionsTableModel.getRowCount(); row++) {
                QBT.addToBinaryTree(new Question(String.valueOf(questionsTableModel.getValueAt(row, 0)),
                        String.valueOf(questionsTableModel.getValueAt(row, 1)),
                        String.valueOf(questionsTableModel.getValueAt(row, 2)),
                        String.valueOf(questionsTableModel.getValueAt(row, 4))));
            }
            if (type.equals("pre")) {
                QBT.preOrder(QBT.getRoot());
                BinaryTreeTextArea.setText("PRE-ORDER:" + QBT.getTraversalString());
            }

            if (type.equals("in")) {
                QBT.inOrder(QBT.getRoot());

                BinaryTreeTextArea.setText("IN-ORDER:" + QBT.getTraversalString());
            }

            if (type.equals("post")) {
                QBT.postOrder(QBT.getRoot());
                BinaryTreeTextArea.setText("POST-ORDER:" + QBT.getTraversalString());
            }

            //prepared for saving
            orderedFlag = type;
        }
    }

    //Method for saving 
    public void saveToFile(String type) {
        //If the BinaryTreeEextArea is not updated, orderFlag if false

        if (orderedFlag.equals("empty")) {
            javax.swing.JOptionPane.showMessageDialog(null, "BinaryTreeTextArea is empty.\n Please add math questions and " + type + "-order them.", "ERROR IN SAVE(" + type + "-order)",
                    javax.swing.JOptionPane.ERROR_MESSAGE);

        } else if (!orderedFlag.equals(type) || orderedFlag.equals("wait")) {
            javax.swing.JOptionPane.showMessageDialog(null, "Please press the Display Button of " + type + "-order.", "ERROR IN SAVE(" + type + "-order)",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {
            int yesOrNo = javax.swing.JOptionPane.showConfirmDialog(null,
                    "You are about to write " + BinaryTreeTextArea.getText() + " to the external file: " + type + "order.txt\nDo you wish to continue?", "External file write", javax.swing.JOptionPane.YES_NO_OPTION);

            if (yesOrNo == javax.swing.JOptionPane.YES_OPTION) {

                try {

                    File file = new File("../NetworkedMathQuiz/External_Files/" + type + "order.txt");
                    FileWriter filewriter = new FileWriter(file, true);
                    filewriter.write(BinaryTreeTextArea.getText() + "\n");
                    filewriter.close();

                } catch (IOException e) {
                    System.out.println(e);
                }
            }

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        firstNumberTextfield = new javax.swing.JTextField();
        operatorComboBox = new javax.swing.JComboBox<>();
        secondNumberTextfield = new javax.swing.JTextField();
        answerTextfield = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        questionsTable = new javax.swing.JTable();
        sendButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        DisplayListButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        LinkedListTextArea = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        BinaryTreeTextArea = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        PreOrderButton = new javax.swing.JButton();
        savePreOrder = new javax.swing.JButton();
        InOrderButton = new javax.swing.JButton();
        saveInOrder = new javax.swing.JButton();
        PostOrderButton = new javax.swing.JButton();
        savePostOrder = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        bubbleButton = new javax.swing.JButton();
        selectionButton = new javax.swing.JButton();
        insertionButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setSize(new java.awt.Dimension(900, 600));

        jLabel1.setBackground(new java.awt.Color(51, 51, 255));
        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Teacher");
        jLabel1.setOpaque(true);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel2.setText("First Number:");

        jLabel3.setText("Operator:");

        jLabel4.setText("Second Number:");

        jLabel5.setText("Answer:");

        jLabel6.setText("Enter question, then Send");

        firstNumberTextfield.setToolTipText("");

        operatorComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "+", "-", "*", "/" }));

        answerTextfield.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(answerTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(operatorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(firstNumberTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(secondNumberTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstNumberTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(operatorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(secondNumberTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(answerTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(24, 24, 24))
        );

        questionsTable.setModel(questionsTableModel);
        jScrollPane1.setViewportView(questionsTable);

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(204, 204, 204));
        jLabel7.setText("Linked List (of all incorrectly answered exercises):");
        jLabel7.setOpaque(true);

        DisplayListButton.setText("Display List");
        DisplayListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayListButtonActionPerformed(evt);
            }
        });

        LinkedListTextArea.setColumns(20);
        LinkedListTextArea.setRows(5);
        jScrollPane2.setViewportView(LinkedListTextArea);

        jLabel8.setBackground(new java.awt.Color(204, 204, 204));
        jLabel8.setText("Binary Tree ( of all questions - added in the order that they were asked):");
        jLabel8.setOpaque(true);

        BinaryTreeTextArea.setColumns(20);
        BinaryTreeTextArea.setRows(5);
        jScrollPane3.setViewportView(BinaryTreeTextArea);

        jLabel9.setBackground(new java.awt.Color(51, 102, 255));
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Pre-Order");
        jLabel9.setOpaque(true);

        jLabel10.setBackground(new java.awt.Color(51, 102, 255));
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("In-Order");
        jLabel10.setOpaque(true);

        jLabel11.setBackground(new java.awt.Color(51, 102, 255));
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Post-Order");
        jLabel11.setOpaque(true);

        PreOrderButton.setText("Display");
        PreOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreOrderButtonActionPerformed(evt);
            }
        });

        savePreOrder.setText("Save");
        savePreOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePreOrderActionPerformed(evt);
            }
        });

        InOrderButton.setText("Display");
        InOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InOrderButtonActionPerformed(evt);
            }
        });

        saveInOrder.setText("Save");
        saveInOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveInOrderActionPerformed(evt);
            }
        });

        PostOrderButton.setText("Display");
        PostOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PostOrderButtonActionPerformed(evt);
            }
        });

        savePostOrder.setText("Save");
        savePostOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePostOrderActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jLabel12.setText("Sort:");

        bubbleButton.setText("1-Bubble (asc)");
        bubbleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bubbleButtonActionPerformed(evt);
            }
        });

        selectionButton.setText("2-Selection (desc)");
        selectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectionButtonActionPerformed(evt);
            }
        });

        insertionButton.setText("3-Insertion (asc)");
        insertionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addGap(18, 43, Short.MAX_VALUE)
                .addComponent(bubbleButton)
                .addGap(41, 41, 41)
                .addComponent(selectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(insertionButton)
                .addGap(17, 17, 17))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(bubbleButton)
                    .addComponent(selectionButton)
                    .addComponent(insertionButton))
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(17, 17, 17)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(sendButton)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(exitButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(DisplayListButton)))
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 865, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(PreOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(savePreOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(92, 92, 92)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(283, 283, 283)
                                .addComponent(InOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveInOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(100, 100, 100)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(PostOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(savePostOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(exitButton)
                            .addComponent(sendButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DisplayListButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(PreOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(savePreOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(InOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(saveInOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(savePostOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PostOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(35, 35, 35))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed

        String first_number = firstNumberTextfield.getText();
        String Operator = (String) operatorComboBox.getSelectedItem();
        String second_number = secondNumberTextfield.getText();

        if (first_number.isEmpty() || second_number.isEmpty()) {

            javax.swing.JOptionPane.showMessageDialog(null, "ERROR: One or both numeric fields are empty or contain non-numeric values!", "SORRY - CHECK YOUR NUMBERS PLEASE!", javax.swing.JOptionPane.ERROR_MESSAGE);

        } else {
            try {
                switch (operatorComboBox.getSelectedIndex()) {
                    case 0:

                        eachAnswer = Integer.parseInt(first_number) + Integer.parseInt(second_number);
                        stringEachAnswer = String.valueOf(eachAnswer);
                        addRows("+", first_number, second_number);
                        break;

                    case 1:

                        eachAnswer = Integer.parseInt(first_number) - Integer.parseInt(second_number);
                        stringEachAnswer = String.valueOf(eachAnswer);
                        addRows("-", first_number, second_number);
                        break;

                    case 2:

                        eachAnswer = Integer.parseInt(first_number) * Integer.parseInt(second_number);
                        stringEachAnswer = String.valueOf(eachAnswer);
                        addRows("*", first_number, second_number);
                        break;

                    case 3:

                        eachAnswer = Integer.parseInt(first_number) / Integer.parseInt(second_number);
                        stringEachAnswer = String.valueOf(eachAnswer);
                        addRows("/", first_number, second_number);
                        break;

                    default:

                        break;
                }

                //Sending question and Answer
                try {
                    String sentQuestion = first_number + " " + Operator + " " + second_number + " " + "=";
                    String sentAnswer = answerTextfield.getText();
                    sendButton.setEnabled(false);
                    dataOutputStream.writeUTF(sentQuestion);
                    dataOutputStream.writeUTF(sentAnswer);
                } catch (Exception e) {
                    String exceptionStr = "Server Send Error: " + e.getMessage();
                    JOptionPane.showMessageDialog(null, exceptionStr, "SERVER SEND ERROR", JOptionPane.ERROR_MESSAGE);
                    System.out.println(exceptionStr);
                    sendButton.setEnabled(true);
                }
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(null, "You must enter a numaric answer to the math question", "ERROR IN ANSWER", javax.swing.JOptionPane.ERROR_MESSAGE);
            } catch (ArithmeticException e) {
                javax.swing.JOptionPane.showMessageDialog(null, "Numbers cannot be divided by zero", "ERROR IN QUESTION", javax.swing.JOptionPane.ERROR_MESSAGE);
                sendButton.setEnabled(true);
            }
        }
    }//GEN-LAST:event_sendButtonActionPerformed


    private void bubbleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bubbleButtonActionPerformed

        callSortMethod("bubble");

    }//GEN-LAST:event_bubbleButtonActionPerformed


    private void selectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectionButtonActionPerformed

        callSortMethod("selection");

    }//GEN-LAST:event_selectionButtonActionPerformed


    private void insertionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertionButtonActionPerformed

        callSortMethod("insertion");

    }//GEN-LAST:event_insertionButtonActionPerformed

    //Displaying wrong-answered quiz
    private void DisplayListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisplayListButtonActionPerformed

        if (incorrectList.size() == 0) {
            LinkedListTextArea.setText("There are no incorrectly answered questions.");
        } else {
            String IncorrectDisplay = "HEAD";
            for (String i : incorrectList) {
                IncorrectDisplay += " <-> " + i;
            }

            IncorrectDisplay += " <-> TAIL";
            LinkedListTextArea.setText(IncorrectDisplay);
        }
    }//GEN-LAST:event_DisplayListButtonActionPerformed


    private void PreOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreOrderButtonActionPerformed

        binaryTreeOrder("pre");

    }//GEN-LAST:event_PreOrderButtonActionPerformed


    private void InOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InOrderButtonActionPerformed

        binaryTreeOrder("in");

    }//GEN-LAST:event_InOrderButtonActionPerformed


    private void PostOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PostOrderButtonActionPerformed

        binaryTreeOrder("post");

    }//GEN-LAST:event_PostOrderButtonActionPerformed


    private void savePreOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePreOrderActionPerformed

        saveToFile("pre");

    }//GEN-LAST:event_savePreOrderActionPerformed


    private void saveInOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveInOrderActionPerformed

        saveToFile("in");

    }//GEN-LAST:event_saveInOrderActionPerformed


    private void savePostOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePostOrderActionPerformed

        saveToFile("post");

    }//GEN-LAST:event_savePostOrderActionPerformed


    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed

        System.exit(0);

    }//GEN-LAST:event_exitButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NetworkedMathQuiz_Teacher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NetworkedMathQuiz_Teacher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NetworkedMathQuiz_Teacher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NetworkedMathQuiz_Teacher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NetworkedMathQuiz_Teacher().setVisible(true);
            }
        });
        try {
            serverSocket = new ServerSocket(1201);
            socket = serverSocket.accept();
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String messageIn = "";

            while (!messageIn.equals("exit")) {
                messageIn = dataInputStream.readUTF();
                if (messageIn.equals("y")) {
                    LinkedListTextArea.setText("Student answered correctly.");
                } else {
                    LinkedListTextArea.setText("Student answered incorrectly.");
                    incorrectList.add(answerTextfield.getText() + "(" + firstNumberTextfield.getText() + (String) operatorComboBox.getSelectedItem() + secondNumberTextfield.getText() + ")");
                }

                firstNumberTextfield.setText("");
                operatorComboBox.setSelectedIndex(0);
                secondNumberTextfield.setText("");
                answerTextfield.setText("");
                sendButton.setEnabled(true);
            }
        } catch (SocketException se) {
            String exceptionStr = "Server Socket Error: " + se.getMessage();
            JOptionPane.showMessageDialog(null, exceptionStr, "SERVER ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            String exceptionStr = "Server Error: " + e.getMessage();
            JOptionPane.showMessageDialog(null, exceptionStr, "SERVER ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea BinaryTreeTextArea;
    private javax.swing.JButton DisplayListButton;
    private javax.swing.JButton InOrderButton;
    private static javax.swing.JTextArea LinkedListTextArea;
    private javax.swing.JButton PostOrderButton;
    private javax.swing.JButton PreOrderButton;
    public static javax.swing.JTextField answerTextfield;
    private javax.swing.JButton bubbleButton;
    private javax.swing.JButton exitButton;
    private static javax.swing.JTextField firstNumberTextfield;
    private javax.swing.JButton insertionButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private static javax.swing.JComboBox<String> operatorComboBox;
    private javax.swing.JTable questionsTable;
    private javax.swing.JButton saveInOrder;
    private javax.swing.JButton savePostOrder;
    private javax.swing.JButton savePreOrder;
    private static javax.swing.JTextField secondNumberTextfield;
    private javax.swing.JButton selectionButton;
    private static javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables
}
