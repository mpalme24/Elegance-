import java.util.Scanner;


public class ReturnBookUI {

	public static enum ReturnBookUiState { INITIALISED, READY, INSPECTING, COMPLETED };

	private ReturnBookControl returnBookUiControl;
	private Scanner keyboardInput;
	private ReturnBookUiState state;

	
	public ReturnBookUI(ReturnBookControl control) {
		this.returnBookUiControl = control;
		keyboardInput = new Scanner(System.in);
		state = ReturnBookUiState.INITIALISED;
		control.setReturnBookUi(this);
	}


	public void runReturnBookUi() {		
		output("Return Book Use Case UI\n");
		
		while (true) {
			
			switch (state) {
			
			case INITIALISED:
				break;
				
			case READY:
				String bookIdString = input("Scan Book (<enter> completes): ");
				if (bookIdString.length() == 0) {
					returnBookUiControl.bookScanningCompleted();
				}
				else {
					try {
						Integer bookIdInteger = Integer.valueOf(bookIdString);
						int bookIdInt=bookIdInteger.intValue();
						returnBookUiControl.bookScanned(bookIdInt);
					}
					catch (NumberFormatException e) {
						output("Invalid bookId");
					}					
				}
				break;				
				
			case INSPECTING:
				String answer = input("Is book damaged? (Y/N): ");
				boolean bookIsDamaged = false;
				if (answer.toUpperCase().equals("Y")) {					
					bookIsDamaged = true;
				}
				returnBookUiControl.dischargeLoan(bookIsDamaged);
			
			case COMPLETED:
				output("Return processing complete");
				return;
			
			default:
				output("Unhandled state");
				throw new RuntimeException("ReturnBookUI : unhandled state :" + state);			
			}
		}
	}

	
	private String input(String prompt) {
		System.out.print(prompt);
		return keyboardInput.nextLine();
	}	
		
		
	private void output(Object object) {
		System.out.println(object);
	}
	
			
	public void display(Object object) {
		output(object);
	}
	
	public void setReturnState(ReturnBookUiState state) {
		this.state = state;
	}

	
}
