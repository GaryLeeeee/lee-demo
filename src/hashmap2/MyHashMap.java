package hashmap2;

/**
 * Created by GaryLee on 2019-04-08 15:24.
 */
public class MyHashMap<K,V> implements MyMap<K,V>{
    int DEFAULT_INITIAL_CAPATITY = 1<<4;//默认容量为16
    float DEFAULT_LOAD_FACTOR = 0.75f;
    float loadFactor;//装载因子
    int capatity;//容量(数组大小)
    int threshold;//阀值
    int size;//Node个数
    int modCount;//map结构变化次数
    final static int TREEIFY_THRESHOLD = 8;//
    Node<K,V>[] table;
    int MAX_CAPATITY = 1<<30;

    class Node<K,V> implements MyMap.Entry<K,V>{
        int hash;//结点的哈希值
        K key;//键
        V value;//值
        Node<K,V> next;//next

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        //赋新值，返回旧值
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }
    }

    public MyHashMap(){
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    //这里用Object不用K?
    //高位运算:key的hashcode跟(hashcode右移16位)做异或操作
    int hash(Object key){
        int h;
        return (key==null)?0:((h=key.hashCode()))^(h>>>16);
    }

    //可读性贼差
    public V get(Object key) {
        Node<K,V> e;
        return (e=getNode(hash(key),key))==null?null:e.value;
    }

    public Node<K,V> getNode(int hash,Object key){
        Node<K,V>[] tab;
        Node<K,V> first,e;
        int n,i;
        K k;
        if((tab=table)==null||(n=tab.length)==0)
            return null;
        if((first=tab[i=hash&(n-1)])!=null){
            //跟put判断一样，都是先判断头结点，再遍历
            if((first.hash==hash)&&((k = first.key)==key||(key!=null&&(key.equals(k))))){
                System.err.format("在数组[%d]下标为[%d]找到key[%s],value[%s]\n",
                        n,i,first.key,first.value);
                return first;
            }
            if((e = first.next)!=null){
                //红黑树的自动过滤
                //遍历链表直到表尾，其中如果找到指定结点则直接返回
                do {
                    if(e.hash==hash&&((k = e.key)==key||(key!=null&&key.equals(k)))){
                        return e;
                    }
                }while ((e=e.next)!=null);
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        return putValue(hash(key),key,value,false,true);
    }

    public V putValue(int hash,K key,V value,boolean ifAbsent,boolean evict){
        Node<K,V>[] tab;
        Node<K,V> first;
        int n,i;//n存放长度，i存放下标
        //如果当前table为空，或者table长度为0
        if((tab = table)==null||(n = tab.length) == 0){
            //初始化数组
            n = (tab = resize()).length;
        }
        //判断当前下标是否有node
        if((first = tab[i = (hash&(n-1))])==null) {
            //直接插入
//            first = newNode(hash, key, value, null);
            //赋给数组，而不是first(因为first就是个null)!!!
            tab[i] = newNode(hash,key,value,null);
            System.err.format("数组[%d]下标为[%d]没有元素，直接插入['%s':'%s']\n",
                                n,i,key,value);
        }else {
            //链表一个个找
            Node<K,V> e;//当前结点，用于遍历用，也用来值修改
            K k;//当前结点的k，用于比较判断
            //到这里的话，证明first上有值(通过if判断)
            //先判断头结点
            //要接上key==null，否则容易爆错误
//            if(first.hash==hash&&(((k = first.key)==key)||(key.equals(k)))){
            if(first.hash==hash&&(((k = first.key)==key)||(key!=null&&key.equals(k)))){
                e = first;
            }
            //红黑树结点判断忽略掉...
            else {
                //遍历链表
                for(int binCount = 0;;binCount++){
                    //如果已经到表尾了
                    if((e = first.next)==null){
                        first.next = newNode(hash,key,value,null);
                        System.err.format("数组[%d]下标为[%d]有元素,插入元素['%s':'%s']到表尾\n",
                                n,i,key,value);
                        if(binCount>=TREEIFY_THRESHOLD-1){
                            //转为红黑树...
                        }
                        break;
                    }
                    if(e.hash==hash&&((k = e.key)==key||(key!=null&&key.equals(k)))){
                        break;
                    }
                    first = e;//等价于first = first.next
                }
            }
            if(e!=null){
                //如果e有值的话，就是值修改
                V oldValue = e.value;
                e.value = value;
                System.err.format("值修改[%s]:'%s'->'%s'\n",e.key,oldValue,value);
                return oldValue;
            }

        }
        //如果是插完值后
        ++modCount;
        //如果put后大于扩容的条件
        if(++size>threshold) {
            System.err.format("当前size为[%d],需要扩容!\n",size);
            resize();
        }

        //
        return null;
    }

    public Node<K,V> newNode(int hash,K key,V value,Node<K,V> next){
        return new Node<>(hash,key,value,next);
    }

    public Node<K, V>[] resize(){
        Node<K,V>[] oldTable = table;
        int oldCap = (oldTable==null)?0:oldTable.length;
        int oldThr = threshold;
        int newCap,newThr = 0;
        if(oldCap>0){
            //初始化数组后
            if(oldCap>=MAX_CAPATITY){
                threshold = Integer.MAX_VALUE;
                return oldTable;
            }else if ((newCap = oldCap<<1)<MAX_CAPATITY&&oldCap>=DEFAULT_INITIAL_CAPATITY){
                newThr = oldThr << 1;
                System.err.format("超过阀值[%d]!开始扩容![%d]->[%d]\n",oldThr,oldCap,newCap);
            }
        }else if(oldThr>0){
            //另外一个构造方法，忽略
            newCap = oldThr;//
        }else {
            //初始化数组
            newCap = DEFAULT_INITIAL_CAPATITY;
            newThr = (int) (DEFAULT_INITIAL_CAPATITY * loadFactor);
        }


        //

        //运行到这里表示已经扩容或出书画了，得到新长度，准备放数据
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTable = new Node[newCap];
        table = newTable;
        threshold = newThr;//这步记得写!!!
        //如果原数组为空，即扩容
        if(oldTable!=null){
            //遍历数组每一个链表
//            for(int i=0;i<newCap;i++){
            //是oldCap,别写错了！！！
            for(int i=0;i<oldCap;i++){
                Node<K,V> first;//存放头结点
                if((first=oldTable[i])!=null){
                    //原数组要置空
                    oldTable[i] = null;
                    //如果只有头结点，直接放入
                    if(first.next==null)
                        newTable[first.hash&(newCap-1)] = first;
                    //红黑树代码省略
                    else{
                        //如果有后续结点，就用高一位的与cap做与操作判断为1还是0
                        Node<K,V> loHead = null,loTail = null;//放入新数组的低半位
                        Node<K,V> hiHead = null,hiTail = null;//放入新数组的高半位
                        Node<K,V> e = first;//遍历当前列表用
                        //do...while先执行再判断
                        do{
                            //这里注意是跟旧长度比较
                            if((e.hash&oldCap)==0){
                                if(loTail==null)//这里用loHead也行，反正一开始都为null
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }else {
                                if(hiTail==null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        }while ((e = e.next)==null);
                        //如果low和high处有值则接过去
                        if(loTail!=null){
                            newTable[i] = loHead;
                            loTail.next = null;
                        }
                        if(hiTail!=null){
                            newTable[i+oldCap] = hiHead;
                            hiTail.next = null;
                        }
                    }

                }
            }
        }


        return newTable;
    }

    //删除失败啥都没返回，成功返回value
    public V remove(Object key) {
        Node<K,V> e;
        return (e = removeNode(hash(key),key,null))==null?null:e.value;
    }

    public Node<K,V> removeNode(int hash,Object key,Object value){
        Node<K,V>[] tab;
        Node<K,V> first ;
        int n,i;
        //如果数组为空，直接返回null
        if((tab=table)==null||(n=tab.length)==0)
            return null;
        //接下来就判断下标处有没东西，再判断链表
        if((first = tab[i = hash&(n-1)])!=null){
            K k;
            V v;
            Node<K,V> e,tmp = null;
            if(first.hash==hash&&((k = first.key)==key||(key!=null&&key.equals(k)))){
//                return first;
                //不是直接返回，而是先保留该结点，后面要将断开的连起来
                tmp = first;
            }else if((e = first.next)!=null){
                //如果链表后面有值
                do{
                    if(e.hash==hash&&((k = e.key)==key||(key!=null&&key.equals(k)))){
                        tmp = e;
                        //找到了就不用再遍历剩余的
                        break;
                    }
                    first = e;//这个保留删除结点的前一个结点!!!!!!!!!
                }while ((e = e.next)!=null);
            }
            //如果找到要删除的结点
            if(tmp!=null){
                //如果头结点就是，就把数组下标处置空
                if(tmp==first) {
                    tab[i] = null;
                    System.err.format("删除位置是数组[%d]下标[%d]头结点,直接置空!\n",tab.length,i);
                }
                else {
                    first.next = tmp.next;
                }

                //删除完了
                ++modCount;
                --size;
                return tmp;
            }

        }

        return null;
    }

    public void clear() {
        Node<K,V>[] tab;
        if((tab=table)!=null&&size!=0){//原来是>0
            for(int i=0;i<tab.length;i++)
                tab[i] = null;
            size = 0;
        }
        ++modCount;

    }

    @Override
    public int size() {
        return size;
    }


}
