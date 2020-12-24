import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;

//���ö����ļ�������ģ���е����е�Ԫ�����ͨ�÷���
public class FileCords
{
    //��tablemodel���ģ�������ж���д����filenameָ���ļ����Ķ����ļ���
    //����д��������������д��ն��󣻷���д��Ķ���������������������������
    //�����쳣���򵯳��Ի����֪
    public static int writeTo(String filename, DefaultTableModel tablemodel, ArrayList<Coordinate> inputList)
    {
        int rows=tablemodel.getRowCount(), columns=tablemodel.getColumnCount(),n=0; //��ñ��ģ��������
        try
        {   //�¾䴴���ļ��ֽ�����������ļ����ڣ�����д�����ļ�·����ȷ���ļ������ڣ� 
            //�򴴽��ļ��������׳��ļ��������쳣
            OutputStream out = new FileOutputStream(filename);
            ObjectOutputStream objOut = new ObjectOutputStream(out); //�����ֽ�����������ļ��ֽ�����Ϊ����Դ
            objOut.writeInt(rows);                         //д����ģ��������������Ҳ��д��int����
            objOut.writeInt(columns);                      //д����ģ������


            for (int i = 0; i < inputList.size(); i++) {
                objOut.writeObject(inputList.get(i));
                n++;
            }
            objOut.close();                                 //�رն�����
            out.close();                                    //�ر��ļ���
            JOptionPane.showMessageDialog(null, "д��\""+filename+"\"�ļ���"+rows+"�У�"+columns+"�У�"+n+"������");
        }
        catch(FileNotFoundException ex)          //�ļ��������쳣�����ļ�·�������ļ�����null��""
        {
            JOptionPane.showMessageDialog(null, "\""+filename+"\"�ļ������ڡ�");
        }
        catch(IOException ex)
        {
            JOptionPane.showMessageDialog(null, "д���ļ�ʱ���ݴ���");
        }
        return n;
    }

    //��filenameָ���ļ����Ķ����ļ����ڣ����ȶ�ȡ2��������Ϊtablemodel���ģ����������
    //�ٽ���ȡ�����ж��󣨰���null����ӵ����ģ�ͣ�
    //���ض�ȡ�Ķ������������������������������ļ������ڣ��򵯳��Ի����֪
    public static ArrayList<Coordinate> readFrom(String filename, DefaultTableModel tablemodel)
    {
        ArrayList<Coordinate> outputList = new ArrayList<>();
        try
        {
            InputStream in = new FileInputStream(filename);     //�ļ��ֽ�������
            ObjectInputStream objIn = new ObjectInputStream(in);//�����ֽ�������
            int rows=objIn.readInt(), columns=objIn.readInt();  //��ȡ2��������Ϊ���������������
//            int rows=(Integer)objIn.readObject(), columns=(Integer)objIn.readObject();//��ȡ2������������Ϊ���������
            tablemodel.setRowCount(0);
            tablemodel.setRowCount(rows);        //���ñ��������û����ձ��
            tablemodel.setColumnCount(columns);  //���ñ������
            while(true)                          //��֪�ļ�����
            {
                try
                {
                    Coordinate cordsIn = (Coordinate) objIn.readObject();
                        outputList.add(cordsIn);
                }
                catch(EOFException eof)          //�������ֽ�����������ʱ�׳��ļ�β�쳣
                {
                    break;                       //�˳�while(true)ѭ��
                }
            }
            objIn.close();                       //�ȹرն�����
            in.close();                          //�ٹر��ļ���
        }
        catch(FileNotFoundException ex)          //�ļ��������쳣�����ļ�·�������ļ�����null��""
        {
//            if (!filename.equals(""))
            JOptionPane.showMessageDialog(null, "\""+filename+"\"�ļ������ڡ�");
        }
        catch(ClassNotFoundException ex)
        {
            JOptionPane.showMessageDialog(null, "ָ����δ�ҵ�����");
        }
        catch(IOException ex)
        {
            JOptionPane.showMessageDialog(null, "��ȡ�ļ�ʱ���ݴ���");
        }
        return outputList;
    }
}    