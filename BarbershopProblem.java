import java.util.concurrent.Semaphore;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
public class BarbershopProblem{
	public static Semaphore [] chairs = new Semaphore[4];
	public static void main(String [] args){
		new CustomerMaker().start();
		new Barber().start();
		for(int i = 0; i < chairs.length; i++){
			chairs[i]=new Semaphore(1);
		}
	}
	
	private static class CustomerMaker extends Thread{
		private Timer t;
		int numCustomers = 0;
		private int delay = (int)(Math.random()*1500);
		ActionListener timerListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new Customer(++numCustomers).start();
				t = new Timer((int)(Math.random()*1500),timerListener);
			}
		};
		public void run(){
			t = new Timer(delay,timerListener);
			t = new Timer(delay,timerListener);
			t.start();
		}
	}
	
	private static class Barber extends Thread{
		public void run(){
			System.out.println("The barber has arrived");
			while(true){
				try{
					sleep((int)(Math.random()*2000));
				}catch(InterruptedException e){}
				if(chairs[0].tryAcquire()){
					System.out.println("The barber is waiting for a customer.");
					chairs[0].release();
				}
				else{
					System.out.println("The barber is cutting hair");
					try{sleep((int)(Math.random()*2000));
					}catch(InterruptedException e){}
					chairs[0].release();
				}
			}
		}
		public Barber(){
			super();
		}
	}
	
	private static class Customer extends Thread{
		public int id;
		public void run(){
			System.out.println("Customer "+ id +" has arrived!");
			int myChair = 4;
			if(chairs[3].tryAcquire()){
				System.out.println("Customer "+id+" found an empty chair in the waiting room.");
				for(int i = 2; i >= 0;i--){
					try{
						chairs[i].acquire();
					}catch(InterruptedException e){}
					chairs[i+1].release();
					System.out.println("Customer "+id+" has moved from chair "+(i+1)+" to chair "+i+".");
				}
				getHairCut();
			}
			else{
				System.out.println("Customer "+id+" didn't find seats.");
				System.out.println("Customer "+id+" has left, frustrated.");
			}
		}
		public Customer(int id){
			super();
			this.id = id;
		}
		public void getHairCut(){
			System.out.println("Customer "+id+" is getting a haircut");
			try{
				chairs[0].acquire();
			}catch(InterruptedException e){}
			chairs[0].release();
			System.out.println("Customer "+id+" has left, satisfied");
		}
	}
}