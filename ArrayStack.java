/**
 * Dynamic array stack for undo entries.
 * Complexity: push/pop amortized O(1)
 */
public class ArrayStack {
    private Object[] arr; private int top;
    public ArrayStack() { arr=new Object[8]; top=0; }
    public void push(Object o) { if (top==arr.length){ Object[] n=new Object[arr.length*2]; System.arraycopy(arr,0,n,0,arr.length); arr=n; } arr[top++]=o; }
    public Object pop() { if (top==0) return null; Object v=arr[--top]; arr[top]=null; return v; }
    public boolean isEmpty() { return top==0; }
}
