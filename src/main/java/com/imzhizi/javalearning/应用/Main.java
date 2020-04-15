package com.imzhizi.javalearning.应用;

/**
 * created by zhizi
 * on 4/9/20 14:56
 */
import java.util.Scanner;
public class Main {
    static class Node{
        int val;
        Node next;
        Node(int v){
            this.val=v;
        }
    }
    public static void main(String[] args) {
        //Scanner in = new Scanner(System.in);
        //int a = in.nextInt();
        //System.out.println(a);
        Node n1=new Node(1);
        Node n11=new Node(2);
        Node n12=new Node(4);

        Node n2=new Node(2);
        Node n21=new Node(4);
        Node n22=new Node(7);

        Node node=merge(n1,n2);

        while(node!=null){
            System.out.println(node.val);
            node=node.next;
        }
    }

    public static Node merge(Node n1, Node n2){
        Node result=new Node(0);
        Node node=result;
        while(n1!=null&&n2!=null){
            if(n1.val<n2.val){
                node.next=n1;
                n1=n1.next;
            }else{
                node.next=n2;
                n2=n2.next;
            }
            node=node.next;
        }

        if(n1!=null){
            node.next=n1;
        }else{
            node.next=n2;
        }

        return result.next;
    }
}