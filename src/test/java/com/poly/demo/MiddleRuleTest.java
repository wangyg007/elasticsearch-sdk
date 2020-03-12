package com.poly.demo;

/**
 * @author wangyg
 * @time 17:49
 * @note
 **/
public class MiddleRuleTest {


    public static int getIndex(int[] array,int des){

        int low=0;
        int high=array.length-1;
        while (low<=high){

            int middle = (low + high) / 2;
            if (des==array[middle]){
                return middle;
            }else if (des<array[middle]){
                high=middle-1;
            }else{
                low=middle+1;
            }

        }

        return -1;

    }

    static class node{

        public node() {

        }

        public node(String id, String name) {
            this.id = id;
            this.name = name;
        }

        String id;
        String name;

        @Override
        public String toString() {
            return "node{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public int hashCode() {
            return (id+name).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return hashCode()==obj.hashCode();
        }
    }

    public static void main(String[] args) {

        int[] array=new int[]{1,2,5,6,7,8,47,52,89,444,44444,444444,823525};
        System.out.println(getIndex(array,823525));
        node node = new MiddleRuleTest.node();
        node.id="1414";
        node.name="1414";

        node node1 = new MiddleRuleTest.node();
        node1.id="1414";
        node1.name="1414";
        System.out.println(node.hashCode());
        System.out.println(node1.hashCode());
        System.out.println(node.equals(node1));

        new MiddleObj();


    }

}


class MiddleObj{




}

