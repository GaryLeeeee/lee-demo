package hashmap;

import java.util.HashMap;

/**
 * Created by GaryLee on 2019-01-21 20:35.
 */
public class MyMapTest {
    public static void main(String[] args) {
        MyHashMap<String,String> map = new MyHashMap<>();
        //put
        System.out.println("插入数据前:"+map.size);
        map.put("name","teemo");
        map.put("age","12");
        System.out.println("插入数据后:"+map.size);
        System.out.println("name:"+map.get("name"));
        System.out.println("age:"+map.get("age"));
        System.out.println();
        //remove
        map.remove("name");
        System.out.println("删除数据后:"+map.size);
        System.out.println("name:"+map.get("name"));
        System.out.println();
        //clear
        map.clear();
        System.out.println("清空数据后:"+map.size);
    }
}
