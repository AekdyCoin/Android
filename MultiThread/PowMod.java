import java.io.*;
import java.util.*;
import java.math.*;
import java.text.*;

public class PowMod {
	private static int a, b ,c;
	
	public static void main(String args[]) {
		Scanner cin = new Scanner(System.in);
		while( cin .hasNextInt() ) {
			a = cin.nextInt(); b = cin.nextInt();
			if(a ==0 && b == 0) break ;
			c = 1;
			A a0 = new A("A", b / 10);
			A a1 = new A("B", b - b / 10);
			a0.start();a1.start();
			
			try {
				a0.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				a1.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(a == 0) c = 0;
			else if(c == 0) c = 9;
			
			System.out.println( c );
		}
		
    }
	private static class A extends Thread {
		int cnt ;
		String name ;
		private static Object obj = new Object() ;
		//obj is a reference in the HEAP SPACE
		private A(String name,  int _cnt) {
			super( name) ;
			this.name = name ;
			cnt = _cnt;
		}
		public void run() {
			synchronized( obj) {
				for(int i=0;i<cnt; ++ i) {
					c = (c * a) % 9 ;
				}
			}
		}
	}
}