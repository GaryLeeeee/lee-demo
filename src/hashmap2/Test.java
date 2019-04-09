package hashmap2;

/**
 * Created by GaryLee on 2019-04-09 19:14.
 * Hashmap2.0版本
 * 测试计划增多(resize,put,get,size等)
 * 更加独立完成代码
 * 注释完善
 */
public class Test {
    public static void main(String[] args) {
        MyMap myMap = new MyHashMap();
        myMap.put("name","teemo");
        myMap.put("a1","tggg");
        myMap.put("a2","tggg");
        myMap.put("a3","tggg");
        myMap.put("a4","tggg");
        myMap.put("a5","tggg");
        myMap.put("a6","tggg");
        myMap.put("a7","tggg");
        myMap.put("a8","tggg");
        myMap.put("a9","tggg");
        myMap.put("a10","tggg");
        myMap.put("a11","tggg");
        myMap.put("a12","tggg");
        myMap.put("a13","tggg");
        myMap.put("a14","tggg");
        myMap.put("a15","tggg");
        myMap.put("a16","tggg");
        myMap.put("a17","tggg");
        System.out.println(myMap.get("name"));
        System.out.println("插入完成!目前size为"+myMap.size());
        myMap.clear();
        System.out.println("清完了,现在size为"+myMap.size());
    }
}
