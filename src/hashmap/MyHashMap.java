package hashmap;

import javax.swing.tree.TreeNode;

/**
 * Created by GaryLee on 2019-01-21 10:47.
 * 自己写一个HashMap方便理解
 */
public class MyHashMap<K,V> implements MyMap<K,V>{
    //数组默认大小
    static final int DEFAULT_INITIAL_CAPATITY = 1<<4;
    //默认装载因子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    //数组最大长度
    static final int MAXIMUM_CAPATITY = 1<<30;
    //超过该值转换为红黑树
    static final int TREEIFY_THRESHOLD = 8;
    //低于该值转换为链表
    static final int UNTREEIFY_THRESHOLD = 6;
    //数组
    Node<K,V>[] table;
    //阀值(超过了就要扩容)
    int threshold;
    //装载因子
    float loadFactor;
    //数组容量
    int capatity;
    //元素个数(Entry/Node)
    int size;
    //HashMap结构变化次数(如put)
    int modCount;
    public MyHashMap(){
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }
    //计算key的哈希值，在1.8使用了高位运算，将hashcode与hashcode无符号右移16位进行异或运算得出哈希值
    //key为null的hash为0，统一放在table[0]
    static final int hash(Object key){
        int h;
        return (key==null)?0:(h=key.hashCode())^(h>>>16);
    }
    //返回大于且最接近cap的2的整数幂的值
    static final int tableSizeFor(int cap){
        int n = cap - 1;
        n|=n>>>1;
        n|=n>>>2;
        n|=n>>>4;
        n|=n>>>8;
        n|=n>>>16;
        //边界处理
        return (n<0)?1:(n>MAXIMUM_CAPATITY)?MAXIMUM_CAPATITY:n+1;
    }
    class Node<K,V> implements MyMap.Entry<K,V>{
        int hash;
        K key;
        V value;
        Node<K,V> next;

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
        //赋值，返回旧值
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }
    }
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public V get(Object key) {
        Node<K,V> e;
        return (e=getNode(hash(key),key))==null?null:e.value;
    }
    //查找相同key和hash的Node并返回
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab;Node<K,V> first,e;int n;K k;
        //首先判断数组不为空，并且hash对应的index处不为空
        if((tab=table)!=null&&(n=tab.length)!=0&&(first=tab[hash&(n-1)])!=null){
            //如果数组index位置恰好对应，则直接返回
            if(first.hash==hash&&((k=first.key)==key||(key!=null&&key.equals(k))))
                return first;
            if((e=first.next)!=null){
                if(first instanceof TreeNode){
                    //如果是红黑树结点，交给对应方法操作
                }
                //当next不为空时循环链表
                do{
                    if(e.hash==hash&&((k=e.key)==key||(key!=null&&key.equals(k))))
                        return e;
                }while ((e=e.next)!=null);
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        return putVal(hash(key),key,value,false,true);
    }

    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
        //数组，结点，数组长度，下标
        Node<K,V>[] tab;Node<K,V> p;int n,i;
        //判断数组是否为null，如果是则没有初始化，先初始化
        if((tab=table)==null||(n=tab.length)==0)
            n = (tab = resize()).length;
        if((p=tab[i= hash&(n-1)])==null)
            tab[i] = newNode(hash,key,value,null);
        else {
            //e传相同k的结点，来做值修改操作
            Node<K,V> e;K k;
            //如果数组上的第一个相同
            if(p.hash==hash&&((k=p.key)==key||(key!=null&key.equals(k))))
                e = p;
            else if(p instanceof TreeNode){
                //红黑树put
                e= null;//随便设置，否则编译不同过
            }
            else{
                for(int binCount = 0;;++binCount){
                    //链表遍历到最后仍不相同，则在最后next加上
                    if((e=p.next)==null){
                        p.next = newNode(hash,key,value,null);
                        //判断添加后是否需要转换为红黑树
                        if(binCount>=TREEIFY_THRESHOLD-1){
                            //treeifyBin(tab, hash);
                            break;
                        }
                    }
                    if(e.hash==hash&&((k=e.key)==key||(key!=null&&key.equals(k))))
                        break;
                    p = e;//p = p.next;
                }
            }
            if(e!=null){
                V oldValue = e.value;
                if(!onlyIfAbsent||oldValue==null)
                    e.value = value;
                //afterNodeAccess(e);
                return oldValue;
            }
        }
        //如果不是值修改操作，而是新增结点，则会执行后续操作
        //应该modCount只会在HashMap结构发生变化的时候++，值修改不变化
        ++modCount;
        //在这里统一判定如果新增结点后超过阀值，则扩容
        if(++size>threshold)
            resize();
        //afterNodeInsertion(evict);
        //如果是insert操作则返回null，否则返回旧值
        return null;
    }

    private Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        return new Node<K,V>(hash,key,value,null);
    }

    //扩容，返回扩容后的数组
    final Node<K,V>[] resize(){
        Node<K,V>[] oldTab = table;
        //因为构造函数没直接赋值cap，在这里可能cap并没有赋值
        int oldCap = (oldTab==null)?0:oldTab.length;
        //构造方法没有初始化，在第一次扩容时初始化
        int oldThr = threshold;
        int newCap,newThr = 0;
        //如果是扩容操作,即原数组有值
        if(oldCap>0){
            //如果原数组容量已达上限，则调整阀值并直接返回原数组即可
            if(oldCap>=MAXIMUM_CAPATITY){
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }else if((newCap=oldCap<<1)<MAXIMUM_CAPATITY&&oldCap>=DEFAULT_INITIAL_CAPATITY){
                //扩容后的要小于数组容量上限，并且旧数组容量要大于默认值(因为一开始初始化最小为default)
                newThr=oldThr<<1;
            }
        }
        else if(oldThr>0){
            //如果只有Thr大于0,在一个构造方法中初始化2的幂的数传给了thr，到这里才传给cap
            newCap = oldThr;
        }else {
            //是初始化数组操作
            newCap = DEFAULT_INITIAL_CAPATITY;
            newThr = (int) (DEFAULT_INITIAL_CAPATITY * DEFAULT_LOAD_FACTOR);
        }
        //计算新数组的threshold
        if(newThr==0){
            float ft = newCap * loadFactor;
            newThr = (newCap<MAXIMUM_CAPATITY&&ft<(float)MAXIMUM_CAPATITY?(int)ft:Integer.MAX_VALUE);
        }
        //参数处理完毕，赋值到成员变量
        threshold = newThr;
        @SuppressWarnings("unchecked")
        Node<K,V>[] newTab = new Node[newCap];
        //新数组只有指定长度，还没赋值
        table = newTab;
        //如果是扩容操作，则扩容，否则直接返回新数组(也就是初始化的数组)
        if(oldTab!=null){
            for(int j=0;j<newCap;j++){
                Node<K,V> e;
                if((e = oldTab[j])!=null){
                    //把原数组置空
                    oldTab[j] = null;
                    //如果下标处只有一个结点，直接计算后传过去
                    if(e.next==null)
                        newTab[e.hash&(newCap-1)]=e;
                    else if(e instanceof TreeNode){
                        //交给红黑树处理
                    }else {
                        Node<K,V> loHead = null,loTail = null;
                        Node<K,V> hiHead = null,hiTail = null;
                        Node<K,V> next;
                        do{
                            //先存储个next结点，并对原结点进行操作
                            next = e.next;
                            if((e.hash&oldCap)==0){
                                //第一次设置头结点
                                if(loTail==null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                //tail指向最后一个结点，方便后面继续指向
                                loTail = e;
                            }else {
                                if(hiTail==null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                //tail指向最后一个结点，方便后面继续指向
                                hiTail = e;
                            }
                        }while ((e=next)!=null);
                        //插入到index
                        if(loTail!=null){
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        //插入到index+oldCap
                        if(hiTail!=null){
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    @Override
    public V remove(Object key) {
        Node<K,V> e;
        return (e=removeNode(hash(key),key,null,false,true))==null?null:e.value;
    }

    final Node<K,V> removeNode(int hash, Object key, Object value, boolean matchValue, boolean movable) {
        Node<K,V>[] tab;Node<K,V> p;int n,index;
        if((tab=table)!=null&&(n=tab.length)>0&&(p=tab[index = (hash&(n-1))])!=null){
            //node存放要remove的那个node，e用来遍历
            Node<K,V> node = null,e;K k;V v;
            if(p.hash==hash&&((k=p.key)==key||(key!=null&&key.equals(k))))
                node = p;
            else if((e=p.next)!=null){
                if(p instanceof TreeNode)
                    node = null;//交给红黑树获取要remove的node
                else{
                    do{
                        if(e.hash==hash&&((k=e.key)==key||(key!=null&&key.equals(k)))) {
                            node = e;
                            break;//直接跳出循环，防止后续没必要的操作
                        }
                        //存放node前面的点，方便待会p=e.next，实现删除e结点
                        p = e;
                    }while ((e=e.next)!=null);
                }
            }
            //如果查找到指定的node
            //再判断值是否相等
            if(node!=null&&(!matchValue||(v = node.value)==value||((value!=null)&&value.equals(v)))){
                if(node instanceof TreeNode){
                    //((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
                }else if(node == p){
                    //如果是头结点的话，直接next置空
                    //node.next = null;--->这句话行不通
                    tab[index] = node.next;//数组上结点=next结点??????
                }else{
                    //否则是链表中间的话，就前面一个的指向后面那个
                    p.next = node.next;
                }
                //完成操作，更新成员变量
                ++modCount;
                --size;
                //afterNodeRemoval(node);
                return node;
            }

        }
        //如果数组为空，或者找不到对应的key的node，则返回null
        return null;
    }

    @Override
    public void clear() {
        Node<K,V>[] tab;
        ++modCount;//HashMap结构变化！
        //如果数组不为0，而且有node存在则开始清空
        if((tab=table)!=null&&size>0){
            //将node的个数size置为0
            size = 0;
            for(int i=0;i<tab.length;i++)
                tab[i] = null;//只需要将数组每一个下标处的置空，而不需要考虑next后面的元素
        }
    }

}
