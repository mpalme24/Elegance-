import java.util.Scanner;

public class FixBookUI {
	public static enum FixBookUiState {
		INITIALISED, READY, FIXING, COMPLETED
	};

	private FixBookControl fixBookControl;
	private Scanner keyboardInput;
	private FixBookUiState fixBookUiState;

	public FixBookUI(FixBookControl inputControl) {
		this.fixBookControl = inputControl;
		keyboardInput = new Scanner(System.in);
		fixBookUiState = FixBookUiState.INITIALISED;
		inputControl.setFixBookUi(this);
	}

	public void setFixBookUiState(FixBookUiState state) {
		this.fixBookUiState = state;
	}

	public void runFixBookUi() {
		output("Fix Book Use Case UI\n");
		while (true) {
			switch (fixBookUiState) {
			case READY:
				String bookIdString = input("Scan Book (<enter> completes): ");
				if (bookIdString.length() == 0) {
					fixBookControl.scannComplete();
				} else {
					try {
						Integer bookIdInteger = Integer.valueOf(bookIdString);
						fixBookControl.setFixBookScanned(bookIdInteger.intValue());
					} catch (NumberFormatException e) {
						output("Invalid bookId");
					}
				}
				break;
			case FIXING:
				String answer = input("Fix Book? (Y/N) : ");
				boolean bookFixed = false;
				if (answer.toUpperCase().equals("Y")) {
					bookFixed = true;
				}
				fixBookControl.setFixBook(bookFixed);
				break;
			case COMPLETED:
				output("Fixing process complete");
				return;
			default:
				output("Unhandled state");
				throw new RuntimeException("FixBookUI : unhandled state :" + fixBookUiState);
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
}
