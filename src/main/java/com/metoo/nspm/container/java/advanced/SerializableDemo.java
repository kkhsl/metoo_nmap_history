package com.metoo.nspm.container.java.advanced;

import com.metoo.nspm.entity.nspm.User;
import org.junit.Test;

import java.io.*;

/**
 * @description 序列化
 *
 */
public class SerializableDemo {

    // 序列化
    public static void main(String [] args)
    {
        User e = new User();
        e.setUsername("Reyan Ali");
        try
        {
            FileOutputStream fileOut =
                    new FileOutputStream("C:\\Users\\Administrator\\Desktop\\214\\user.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(e);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in /tmp/employee.ser");
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }

    // 反序列化
    @Test
    public void fileRead(){
        try {
            FileInputStream fis = new FileInputStream("C:\\Users\\Administrator\\Desktop\\214\\user.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            User user = (User)ois.readObject();
            ois.close();
            fis.close();
            System.out.println("Name: " + user.getUsername());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
