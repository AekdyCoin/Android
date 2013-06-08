import java.io.*;
import java.util.*;
import java.math.*;
import java.text.*;

public class Main {
	private static BigInteger x, y ;
	
	public static void main(String args[]) {
		Scanner cin = new Scanner( System.in ) ;
		while( cin.hasNextBigInteger()) {
			x = cin.nextBigInteger();
			y = cin.nextBigInteger();
			new ADD("add").start();
			new MUL("mul").start();
		}
    }
	private static class ADD extends Thread {
		private ADD( String name) {
			super( name );
		}
		public void run() {
			System.out.println( x .add( y ) );
		}
	}
	private static class MUL extends Thread {
		private MUL( String name) {
			super( name );
		}
		public void run() {
			System.out.println( x .multiply( y ));
		}
	}
}