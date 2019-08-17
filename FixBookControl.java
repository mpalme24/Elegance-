public class FixBookControl {
	private FixBookUI fixBookUi;

	private enum FixBookControlState {
		INITIALISED, READY, FIXING
	};

	private FixBookControlState fixBookState;
	private Library library;
	private Book currentBook;

	public FixBookControl() {
		this.library = library.instanceLibrary();
		fixBookState = FixBookControlState.INITIALISED;
	}

	public void setFixBookUi(FixBookUI inputFixBookUi) {
		if (!fixBookState.equals(FixBookControlState.INITIALISED)) {
			throw new RuntimeException("FixBookControl: cannot call setFixBookUi except in INITIALISED state");
		}
		this.fixBookUi = inputFixBookUi;
		inputFixBookUi.setFixBookUiState(FixBookUI.FixBookUiState.READY);
		fixBookState = FixBookControlState.READY;
	}

	public void setFixBookScanned(int bookId) {
		if (!fixBookState.equals(FixBookControlState.READY)) {
			throw new RuntimeException("FixBookControl: cannot call fixBookScanned except in READY state");
		}
		currentBook = library.getBook(bookId);
		if (currentBook == null) {
			fixBookUi.display("Invalid bookId");
			return;
		}
		if (!currentBook.getBookStateIsDamaged()) {
			fixBookUi.display("Book has not been damaged");
			return;
		}
		fixBookUi.display(currentBook.toString());
		fixBookUi.setFixBookUiState(FixBookUI.FixBookUiState.FIXING);
		fixBookState = FixBookControlState.FIXING;
	}

	public void setFixBook(boolean bookFixState) {
		if (!fixBookState.equals(FixBookControlState.FIXING)) {
			throw new RuntimeException("FixBookControl: cannot call setFixBook except in FIXING state");
		}
		if (bookFixState) {
			library.repairBook(currentBook);
		}
		currentBook = null;
		fixBookUi.setFixBookUiState(FixBookUI.FixBookUiState.READY);
		fixBookState = FixBookControlState.READY;
	}

	public void scanComplete() {
		if (!fixBookState.equals(FixBookControlState.READY)) {
			throw new RuntimeException("FixBookControl: cannot call scanningComplete except in READY state");
		}
		fixBookUi.setFixBookUiState(FixBookUI.FixBookUiState.COMPLETED);
	}
}
