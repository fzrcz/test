public class Rcz{
    public static void main(String[] args){
       System.out.println("hello git");
    }
    
    public int add(int a,int b){
        return a+b;
    }
    
    public int sub(int a,int b){
        return a-b;
    }
    
    public int multi(int a,int b){
        return a*b;
    }
    
    public int div(int a,int b){
        if(b==0){
            throw new Exception("除数不能为0"):
        }
        return a/b;
    }
}
