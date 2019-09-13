public class FixBookControl {
	private FixBookUI fixBookUI;

	private enum FixBookControlState {
		INITIALISED, READY, FIXING
	};

	private FixBookControlState fixBookState;
	private library library;
	private book currentBook;

	public FixBookControl() {
		this.library = library.instanceLibrary();
		fixBookState = FixBookControlState.INITIALISED;
	}

	public void setFixBookUI(FixBookUI inputFixBookUI) {
		if (!fixBookState.equals(FixBookControlState.INITIALISED)) {
			throw new RuntimeException("FixBookControl: cannot call setFixBookUi except in INITIALISED state");
		}
		this.fixBookUI = inputFixBookUI;
		inputFixBookUI.setFixBookUIState(FixBookUI.FixBookUIState.READY);
		fixBookState = FixBookControlState.READY;
	}

	public void setFixBookScanned(int bookId) {
		if (!fixBookState.equals(FixBookControlState.READY)) {
			throw new RuntimeException("FixBookControl: cannot call fixBookScanned except in READY state");
		}
		currentBook = library.getBook(bookId);
		if (currentBook == null) {
			fixBookUI.display("Invalid bookId");
			return;
		}
		if (!currentBook.getBookStateIsDamaged()) {
			fixBookUI.display("Book has not been damaged");
			return;
		}
		fixBookUI.display(currentBook.toString());
		fixBookUI.setFixBookUIState(FixBookUI.FixBookUIState.FIXING);
		fixBookState = FixBookControlState.FIXING;
	}

	public void setFixBook(boolean isBookInFixState) {
		if (!fixBookState.equals(FixBookControlState.FIXING)) {
			throw new RuntimeException("FixBookControl: cannot call setFixBook except in FIXING state");
		}
		if (isBookInFixState) {
			library.repairBook(currentBook);
		}
		currentBook = null;
		fixBookUI.setFixBookUIState(FixBookUI.FixBookUIState.READY);
		fixBookState = FixBookControlState.READY;
	}

	public void scannComplete() {
		if (!fixBookState.equals(FixBookControlState.READY)) {
			throw new RuntimeException("FixBookControl: cannot call scanningComplete except in READY state");
		}
		fixBookUI.setFixBookUIState(FixBookUI.FixBookUIState.COMPLETED);
	}
}
