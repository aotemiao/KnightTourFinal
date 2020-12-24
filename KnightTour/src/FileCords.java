import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;

//采用对象文件保存表格模型中的所有单元格对象，通用方法
public class FileCords
{
    //将tablemodel表格模型中所有对象写入由filename指定文件名的对象文件，
    //首先写入表格行列整数，写入空对象；返回写入的对象个数（不包括表格行列数）；
    //若有异常，则弹出对话框告知
    public static int writeTo(String filename, DefaultTableModel tablemodel, ArrayList<Coordinate> inputList)
    {
        int rows=tablemodel.getRowCount(), columns=tablemodel.getColumnCount(),n=0; //获得表格模型行列数
        try
        {   //下句创建文件字节输出流，若文件存在，则重写；若文件路径正确但文件不存在， 
            //则创建文件，否则抛出文件不存在异常
            OutputStream out = new FileOutputStream(filename);
            ObjectOutputStream objOut = new ObjectOutputStream(out); //数据字节输出流，以文件字节流作为数据源
            objOut.writeInt(rows);                         //写入表格模型行数，对象流也可写入int整数
            objOut.writeInt(columns);                      //写入表格模型列数


            for (int i = 0; i < inputList.size(); i++) {
                objOut.writeObject(inputList.get(i));
                n++;
            }
            objOut.close();                                 //关闭对象流
            out.close();                                    //关闭文件流
            JOptionPane.showMessageDialog(null, "写入\""+filename+"\"文件，"+rows+"行，"+columns+"列，"+n+"个对象。");
        }
        catch(FileNotFoundException ex)          //文件不存在异常，如文件路径错误、文件名是null或""
        {
            JOptionPane.showMessageDialog(null, "\""+filename+"\"文件不存在。");
        }
        catch(IOException ex)
        {
            JOptionPane.showMessageDialog(null, "写入文件时数据错误");
        }
        return n;
    }

    //若filename指定文件名的对象文件存在，首先读取2个整数作为tablemodel表格模型行列数；
    //再将读取的所有对象（包括null）添加到表格模型；
    //返回读取的对象个数（不包括表格行列数）；若文件不存在，则弹出对话框告知
    public static ArrayList<Coordinate> readFrom(String filename, DefaultTableModel tablemodel)
    {
        ArrayList<Coordinate> outputList = new ArrayList<>();
        try
        {
            InputStream in = new FileInputStream(filename);     //文件字节输入流
            ObjectInputStream objIn = new ObjectInputStream(in);//数据字节输入流
            int rows=objIn.readInt(), columns=objIn.readInt();  //读取2个整数作为表格行列数，可行
//            int rows=(Integer)objIn.readObject(), columns=(Integer)objIn.readObject();//读取2个整数对象作为表格行列数
            tablemodel.setRowCount(0);
            tablemodel.setRowCount(rows);        //设置表格行数，没有清空表格
            tablemodel.setColumnCount(columns);  //设置表格列数
            while(true)                          //不知文件长度
            {
                try
                {
                    Coordinate cordsIn = (Coordinate) objIn.readObject();
                        outputList.add(cordsIn);
                }
                catch(EOFException eof)          //当数据字节输入流结束时抛出文件尾异常
                {
                    break;                       //退出while(true)循环
                }
            }
            objIn.close();                       //先关闭对象流
            in.close();                          //再关闭文件流
        }
        catch(FileNotFoundException ex)          //文件不存在异常，如文件路径错误、文件名是null或""
        {
//            if (!filename.equals(""))
            JOptionPane.showMessageDialog(null, "\""+filename+"\"文件不存在。");
        }
        catch(ClassNotFoundException ex)
        {
            JOptionPane.showMessageDialog(null, "指定类未找到错误");
        }
        catch(IOException ex)
        {
            JOptionPane.showMessageDialog(null, "读取文件时数据错误");
        }
        return outputList;
    }
}    