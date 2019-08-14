import java.util.Scanner;


public class FixBookUI {

	public static enum Ui_State { INITIALISED, READY, FIXING, COMPLETED };

	private FixBookControl control;
	private Scanner input;
	private Ui_State state;

	
	public FixBookUI(FixBookControl control) {
		this.control = control;
		input = new Scanner(System.in);
		state = Ui_State.INITIALISED;
		control.setUi(this);
	}


	public void setState(Ui_State state) {
		this.state = state;
	}

	
	public void run() {
		output("Fix Book Use Case UI\n");
		
		while (true) {
			
			switch (state) {
			
			case READY:
				String bookString = input("Scan Book (<enter> completes): ");
				if (bookString.length() == 0) {
					control.scanComplete();
				}
				else {
					try {
						int bookId = Integer.valueOf(bookString).intValue();
						control.bookScanned(bookId);
					}
					catch (NumberFormatException e) {
						output("Invalid bookId");
					}
				}
				break;	
				
			case FIXING:
				String answer = input("Fix Book? (Y/N) : ");
				boolean isFixed = false;
				if (answer.toUpperCase().equals("Y")) {
					isFixed = true;
				}
				control.fixBook(isFixed);
				break;
								
			case COMPLETED:
				output("Fixing process complete");
				return;
			
			default:
				output("Unhandled state");
				throw new RuntimeException("FixBookUI : unhandled state :" + state);			
			
			}		
		}
		
	}

	
	private String input(String prompt) {
		System.out.print(prompt);
		return input.nextLine();
	}	
		
		
	private void output(Object object) {
		System.out.println(object);
	}
	

	public void display(Object object) {
		output(object);
	}
	
	
}
