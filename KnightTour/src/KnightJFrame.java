import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class KnightJFrame extends JFrame implements ActionListener, Runnable {
    private final JTextField[] texts;                  //�ı�������
    private final JButton[] buttons;                   //��ť����
    private final DefaultTableModel tablemodel;        //���ģ��
    private Thread thread;                       //�߳�
    public int sleeptime;                       //�߳�˯��ʱ��
    public ArrayList<Coordinate> boardList;
    private int width;
    private int initialX;
    private int initialY;
    private int step;

    private final int[] moveX = {-2, -1, 1, 2, 2, 1, -1, -2};
    private final int[] moveY = {1, 2, 2, 1, -1, -2, -2, -1};
    private final int[] possiblePaths = new int[8];

    public KnightJFrame() {
        super("��ʿ����");
        this.setBounds(300, 240, 600, 300);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        JToolBar toolbar = new JToolBar();
        this.getContentPane().add(toolbar, "North");
        String[][] str = {{"X����", "Y����", "���̿��", "sleeptime"}, {"0", "0", "8", "30"}
                , {"��ʼ", "Stop", "����", "��"}};
        this.texts = new JTextField[str[0].length];
        for (int i = 0; i < this.texts.length; i++)  //��ӱ�ǩ���ı���
        {
            toolbar.add(new JLabel(str[0][i]));
            toolbar.add(this.texts[i] = new JTextField(str[1][i], 3));
        }

        this.buttons = new JButton[str[2].length];
        for (int i = 0; i < this.buttons.length; i++) {
            this.buttons[i] = new JButton(str[2][i]);
            toolbar.add(this.buttons[i]);
            this.buttons[i].addActionListener(this);
        }
        this.buttons[1].setEnabled(false);//stop��ť

        this.tablemodel = new DefaultTableModel(8, 8);
        JTable jtable = new JTable(this.tablemodel);       //�������ָ�����ģ��
        this.getContentPane().add(new JScrollPane(jtable));//������ݴ�����ӹ������񣨰������
        this.setVisible(true);
    }

    public void tableInitialized(int width)
    {
        this.tablemodel.setRowCount(0);      //��ʼ�����
        this.tablemodel.setColumnCount(width);       //���ñ��ģ������
        this.tablemodel.setRowCount(width);          //���ñ��ģ������
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        switch (event.getActionCommand()) {
            case "��ʼ":
                try {
                    this.width = Integer.parseInt(this.texts[2].getText());//���̿��
                } catch (NumberFormatException ex)         //���񲢴�����ֵ��ʽ�쳣
                {
                    JOptionPane.showMessageDialog(this, "���̿����������");
                    return;
                }
                try {
                    this.initialX = Integer.parseInt(this.texts[0].getText());
                    if (initialX < 0 || initialX > width - 1) {
                        JOptionPane.showMessageDialog(this, "X������������");
                        return;
                    }
                } catch (NumberFormatException ex)         //���񲢴�����ֵ��ʽ�쳣
                {
                    JOptionPane.showMessageDialog(this, "X����������������");
                    return;
                }
                try {
                    this.initialY = Integer.parseInt(this.texts[1].getText());
                    if (initialY < 0 || initialY > width - 1) {
                        JOptionPane.showMessageDialog(this, "Y������������");
                        return;
                    }
                } catch (NumberFormatException ex)         //���񲢴�����ֵ��ʽ�쳣
                {
                    JOptionPane.showMessageDialog(this, "Y����������������");
                    return;
                }
                try {
                    this.sleeptime = Integer.parseInt(this.texts[3].getText());//�߳�˯��ʱ��
                } catch (NumberFormatException ex)         //���񲢴�����ֵ��ʽ�쳣
                {
                    JOptionPane.showMessageDialog(this, "sleeptime��������");
                    return;
                }

                this.tableInitialized(width);       //����ʼ��
                boardList = new ArrayList<Coordinate>();
                knightTour();                   //�������
                this.tableInitialized(width);       //����ʼ��

                this.thread = new Thread(this);
                this.thread.start();                      //�����߳�

                this.buttons[0].setEnabled(false);
                this.buttons[1].setEnabled(true);
                this.buttons[2].setEnabled(false);
                this.buttons[3].setEnabled(false);
                break;
            case "Stop":
                this.thread.interrupt();                  //�ж��߳�

                this.buttons[0].setEnabled(true);
                this.buttons[1].setEnabled(false);
                this.buttons[2].setEnabled(false);
                this.buttons[3].setEnabled(true);
                break;
            case "����":
                FileCords.writeTo("knights.txt", tablemodel, boardList);
                break;
            case "��":
                boardList = FileCords.readFrom("knights.txt", tablemodel);
                if (boardList.size() == 0) break;
                try {
                    this.sleeptime = Integer.parseInt(this.texts[3].getText());//�߳�˯��ʱ��
                } catch (NumberFormatException ex)         //���񲢴�����ֵ��ʽ�쳣
                {
                    JOptionPane.showMessageDialog(this, "sleeptime��������");
                    return;
                }
                this.thread = new Thread(this);
                this.thread.start();                      //�����߳�
                this.buttons[0].setEnabled(false);
                this.buttons[1].setEnabled(true);
                this.buttons[2].setEnabled(false);
                break;
        }
    }

    public void knightTour() {



        // ���ڼ�¼��һ�������ƶ��ĸ���λ��
        int[] possibleX = new int[width];
        int[] possibleY = new int[width];

        // possiblePaths��¼��·�ĸ���

        //goToX,goToY���ڴ��ʵ���ƶ�����
        int goToX = this.initialX;
        int goToY = this.initialY;
        //��һ��λ�����û��趨�������������и�ֵΪ1
        this.boardList.add(new Coordinate(goToX, goToY));
        this.tablemodel.setValueAt(1, goToX, goToY);


        for (step = 2; step <= Math.pow(width, 2); step++) {
            //math.pow(a,b)���ڼ���a��b�η�
            //��ʼ����һ��λ�ÿ��ߵ�λ�õ���Ŀ
            for (int i = 0; i < 8; i++) {

                possiblePaths[i] = 0;
            }

            int possibleCount = 0;//possibleCount������¼��һ���ƶ������ж��ٸ������ѡ��

            for (int i = 0; i < 8; i++) {// ��̽8������
                int tryX = goToX + moveX[i];
                int tryY = goToY + moveY[i];
                // �ߵ��߽磬·��
                if (tryX < 0 || tryX > width - 1 || tryY < 0 || tryY > width - 1) {
                    continue;
                }

                if (this.tablemodel.getValueAt( tryX,tryY) == null) {// ��¼�¿��ߵķ���
                    possibleX[possibleCount] = tryX;
                    possibleY[possibleCount] = tryY;//�����possibleX��possibleY�Ѿ��洢�˿��з����������
                    possibleCount++;
                }
            }

            // �����possibleCount��ʾ��ǰ���м����߷���possibleStep�д洢�����߷������ꡣ

            if (possibleCount == 0) {
//                JOptionPane.showMessageDialog(this, "�߲�ͨ");
                this.buttons[0].setEnabled(true);
                this.buttons[1].setEnabled(false);
                this.buttons[2].setEnabled(true);
                this.buttons[3].setEnabled(true);
                return;
            }

            //minPathsIndex�������ѡ���ĸ����������
            int minPathsIndex;
            if (1 == possibleCount) {
                minPathsIndex = 0;
            } else {// ��һ����Ϊ���ҵ���һ���߷����������߷�����һ��
                for (int i = 0; i < possibleCount; i++) {
                    for (int j = 0; j < 8; j++) {//�����nextStepX��nextStepY�Ѿ��������һ�����ƶ�����
                        int tryX = possibleX[i] + moveX[j];//�ٴμ���movex��movey���ⶨ��һ���߷������ٷ���ѡ����
                        int tryY = possibleY[i] + moveY[j];
                        if (tryX < 0 || tryX > width - 1 || tryY < 0 || tryY > width - 1) {
                            continue;
                        }
                        if (this.tablemodel.getValueAt( tryX,tryY) == null) {// ��¼�����λ�ÿ��ߵķ�����
                            possiblePaths[i]++;
                        }
                    }
                }

                // �ӿ��ߵķ����У�Ѱ�������ߵĳ�·
                int t = possiblePaths[0];
                minPathsIndex = 0;
                for (int i = 1; i < possibleCount; i++) {
                    if (t > possiblePaths[i]) {
                        t = possiblePaths[i];
                        minPathsIndex = i;
                    }
                }
            }

            // �õ����ٵĳ�·
            goToX = possibleX[minPathsIndex];
            goToY = possibleY[minPathsIndex];
            this.boardList.add(new Coordinate(goToX, goToY));

            this.tablemodel.setValueAt(step, goToX, goToY);
        }
    }

    @Override
    public void run() {
        //Todo:��ʾ���
        for (int i = 0; i < boardList.size(); i++) {
            this.tablemodel.setValueAt(i + 1, boardList.get(i).x, boardList.get(i).y);
            try {
                Thread.sleep(sleeptime);
            } catch (InterruptedException ex) {
                return;
            }
        }
        if (Math.pow(this.tablemodel.getColumnCount(), 2) == boardList.size()) {
            JOptionPane.showMessageDialog(this, "�������");
        } else {
            JOptionPane.showMessageDialog(this, "�߲�ͨ");
        }
        this.buttons[0].setEnabled(true);
        this.buttons[1].setEnabled(false);
        this.buttons[2].setEnabled(true);
        this.buttons[3].setEnabled(true);
    }

    public static void main(String[] args) {
        new KnightJFrame();
    }
}
//2020.12.21 ��ʮ��������