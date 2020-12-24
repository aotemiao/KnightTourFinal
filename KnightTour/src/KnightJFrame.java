import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class KnightJFrame extends JFrame implements ActionListener, Runnable {
    private final JTextField[] texts;                  //文本行数组
    private final JButton[] buttons;                   //按钮数组
    private final DefaultTableModel tablemodel;        //表格模型
    private Thread thread;                       //线程
    public int sleeptime;                       //线程睡眠时间
    public ArrayList<Coordinate> boardList;
    private int width;
    private int initialX;
    private int initialY;
    private int step;

    private final int[] moveX = {-2, -1, 1, 2, 2, 1, -1, -2};
    private final int[] moveY = {1, 2, 2, 1, -1, -2, -2, -1};
    private final int[] possiblePaths = new int[8];

    public KnightJFrame() {
        super("骑士游历");
        this.setBounds(300, 240, 600, 300);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        JToolBar toolbar = new JToolBar();
        this.getContentPane().add(toolbar, "North");
        String[][] str = {{"X坐标", "Y坐标", "棋盘宽度", "sleeptime"}, {"0", "0", "8", "30"}
                , {"开始", "Stop", "保存", "打开"}};
        this.texts = new JTextField[str[0].length];
        for (int i = 0; i < this.texts.length; i++)  //添加标签和文本行
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
        this.buttons[1].setEnabled(false);//stop按钮

        this.tablemodel = new DefaultTableModel(8, 8);
        JTable jtable = new JTable(this.tablemodel);       //创建表格，指定表格模型
        this.getContentPane().add(new JScrollPane(jtable));//框架内容窗格添加滚动窗格（包含表格）
        this.setVisible(true);
    }

    public void tableInitialized(int width)
    {
        this.tablemodel.setRowCount(0);      //初始化表格
        this.tablemodel.setColumnCount(width);       //设置表格模型行数
        this.tablemodel.setRowCount(width);          //设置表格模型行数
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        switch (event.getActionCommand()) {
            case "开始":
                try {
                    this.width = Integer.parseInt(this.texts[2].getText());//棋盘宽度
                } catch (NumberFormatException ex)         //捕获并处理数值格式异常
                {
                    JOptionPane.showMessageDialog(this, "棋盘宽度数据有误");
                    return;
                }
                try {
                    this.initialX = Integer.parseInt(this.texts[0].getText());
                    if (initialX < 0 || initialX > width - 1) {
                        JOptionPane.showMessageDialog(this, "X坐标数据有误");
                        return;
                    }
                } catch (NumberFormatException ex)         //捕获并处理数值格式异常
                {
                    JOptionPane.showMessageDialog(this, "X坐标输入数据有误");
                    return;
                }
                try {
                    this.initialY = Integer.parseInt(this.texts[1].getText());
                    if (initialY < 0 || initialY > width - 1) {
                        JOptionPane.showMessageDialog(this, "Y坐标数据有误");
                        return;
                    }
                } catch (NumberFormatException ex)         //捕获并处理数值格式异常
                {
                    JOptionPane.showMessageDialog(this, "Y坐标输入数据有误");
                    return;
                }
                try {
                    this.sleeptime = Integer.parseInt(this.texts[3].getText());//线程睡眠时间
                } catch (NumberFormatException ex)         //捕获并处理数值格式异常
                {
                    JOptionPane.showMessageDialog(this, "sleeptime数据有误");
                    return;
                }

                this.tableInitialized(width);       //表格初始化
                boardList = new ArrayList<Coordinate>();
                knightTour();                   //输出数组
                this.tableInitialized(width);       //表格初始化

                this.thread = new Thread(this);
                this.thread.start();                      //启动线程

                this.buttons[0].setEnabled(false);
                this.buttons[1].setEnabled(true);
                this.buttons[2].setEnabled(false);
                this.buttons[3].setEnabled(false);
                break;
            case "Stop":
                this.thread.interrupt();                  //中断线程

                this.buttons[0].setEnabled(true);
                this.buttons[1].setEnabled(false);
                this.buttons[2].setEnabled(false);
                this.buttons[3].setEnabled(true);
                break;
            case "保存":
                FileCords.writeTo("knights.txt", tablemodel, boardList);
                break;
            case "打开":
                boardList = FileCords.readFrom("knights.txt", tablemodel);
                if (boardList.size() == 0) break;
                try {
                    this.sleeptime = Integer.parseInt(this.texts[3].getText());//线程睡眠时间
                } catch (NumberFormatException ex)         //捕获并处理数值格式异常
                {
                    JOptionPane.showMessageDialog(this, "sleeptime数据有误");
                    return;
                }
                this.thread = new Thread(this);
                this.thread.start();                      //启动线程
                this.buttons[0].setEnabled(false);
                this.buttons[1].setEnabled(true);
                this.buttons[2].setEnabled(false);
                break;
        }
    }

    public void knightTour() {



        // 用于记录下一步可以移动的各个位置
        int[] possibleX = new int[width];
        int[] possibleY = new int[width];

        // possiblePaths记录出路的个数

        //goToX,goToY用于存放实际移动方向
        int goToX = this.initialX;
        int goToY = this.initialY;
        //第一个位置由用户设定，并且在数组中赋值为1
        this.boardList.add(new Coordinate(goToX, goToY));
        this.tablemodel.setValueAt(1, goToX, goToY);


        for (step = 2; step <= Math.pow(width, 2); step++) {
            //math.pow(a,b)用于计算a的b次方
            //初始化下一个位置可走的位置的数目
            for (int i = 0; i < 8; i++) {

                possiblePaths[i] = 0;
            }

            int possibleCount = 0;//possibleCount用来记录下一步移动可以有多少个方向的选择

            for (int i = 0; i < 8; i++) {// 试探8个方向
                int tryX = goToX + moveX[i];
                int tryY = goToY + moveY[i];
                // 走到边界，路断
                if (tryX < 0 || tryX > width - 1 || tryY < 0 || tryY > width - 1) {
                    continue;
                }

                if (this.tablemodel.getValueAt( tryX,tryY) == null) {// 记录下可走的方向
                    possibleX[possibleCount] = tryX;
                    possibleY[possibleCount] = tryY;//这里的possibleX和possibleY已经存储了可行方向的坐标了
                    possibleCount++;
                }
            }

            // 到这里，possibleCount表示当前点有几种走法。possibleStep中存储各种走法的坐标。

            if (possibleCount == 0) {
//                JOptionPane.showMessageDialog(this, "走不通");
                this.buttons[0].setEnabled(true);
                this.buttons[1].setEnabled(false);
                this.buttons[2].setEnabled(true);
                this.buttons[3].setEnabled(true);
                return;
            }

            //minPathsIndex用来存放选择哪个方向的数字
            int minPathsIndex;
            if (1 == possibleCount) {
                minPathsIndex = 0;
            } else {// 这一步是为了找到下一次走法中最少种走法的那一步
                for (int i = 0; i < possibleCount; i++) {
                    for (int j = 0; j < 8; j++) {//这里的nextStepX和nextStepY已经存放了下一步的移动方向
                        int tryX = possibleX[i] + moveX[j];//再次加上movex和movey来测定下一步走法中最少方向选择项
                        int tryY = possibleY[i] + moveY[j];
                        if (tryX < 0 || tryX > width - 1 || tryY < 0 || tryY > width - 1) {
                            continue;
                        }
                        if (this.tablemodel.getValueAt( tryX,tryY) == null) {// 记录下这个位置可走的方向数
                            possiblePaths[i]++;
                        }
                    }
                }

                // 从可走的方向中，寻找最难走的出路
                int t = possiblePaths[0];
                minPathsIndex = 0;
                for (int i = 1; i < possibleCount; i++) {
                    if (t > possiblePaths[i]) {
                        t = possiblePaths[i];
                        minPathsIndex = i;
                    }
                }
            }

            // 得到最少的出路
            goToX = possibleX[minPathsIndex];
            goToY = possibleY[minPathsIndex];
            this.boardList.add(new Coordinate(goToX, goToY));

            this.tablemodel.setValueAt(step, goToX, goToY);
        }
    }

    @Override
    public void run() {
        //Todo:显示表格
        for (int i = 0; i < boardList.size(); i++) {
            this.tablemodel.setValueAt(i + 1, boardList.get(i).x, boardList.get(i).y);
            try {
                Thread.sleep(sleeptime);
            } catch (InterruptedException ex) {
                return;
            }
        }
        if (Math.pow(this.tablemodel.getColumnCount(), 2) == boardList.size()) {
            JOptionPane.showMessageDialog(this, "游历完成");
        } else {
            JOptionPane.showMessageDialog(this, "走不通");
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
//2020.12.21 二十二岁生日