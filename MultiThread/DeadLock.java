import java.io.*;
import java.util.*;
import java.math.*;
import java.text.*;

public class DeadLock {
	private static Object r_0 = "0";
	private static Object r_1 = "1";
	
	public static void main(String args[]) {
		new A("A").start();
		new B("B").start();
		System.out.println("done") ;
    }
	private static class A extends Thread {
		private A( String name) {
			super( name );
		}
		public void run() {
			synchronized( r_0) {
				System.out.println("r_0 is locked!" );
				try {
					sleep( 50 );
				}catch(Exception e) {
					e.printStackTrace();
				}
				System.out.println("Wait for r_1...") ;
				synchronized( r_1) {
					try {
						sleep(1000);
					}catch( Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("r_0 is unlocked!" );
			}
		}
	}
	private static class B extends Thread {
		private B( String name) {
			super( name );
		}
		public void run() {
			synchronized( r_1) {
				System.out.println("r_1 is locked!" );
				try {
					sleep( 50 );
				}catch(Exception e) {
					e.printStackTrace();
				}
				System.out.println("Wait for r_0...") ;
				synchronized( r_0) {
					try {
						sleep(1000);
					}catch( Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("r_1 is unlocked!" );
			}
		}
	}
}