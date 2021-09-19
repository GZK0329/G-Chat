package connect.ds;

/**
 * @Description: 带优先级的节点构建
 * @Author: GZK0329
 * @Date: 2021/1/26
 **/

public class BytePriorityNode<Item> {
    public byte priority;
    public Item item;
    public BytePriorityNode<Item> next;//指向下一个节点

    public BytePriorityNode(Item item) {
        this.item = item;
    }

    /*
    * 按优先级追加到现在的链表中
    * */
    public void appendWithPriority(BytePriorityNode<Item> node){
        if (next == null) {
            next = node;
        }else{
            BytePriorityNode<Item> after = this.next;
            if(after.priority < node.priority){
                /*
                * this after
                * node
                * ==> this node after
                * */
                this.next = node;
                node.next = after;
            }else{
                /*
                * after节点优先级更高 node再伺机插入
                * */
                after.appendWithPriority(node);
            }
        }
    }
}
