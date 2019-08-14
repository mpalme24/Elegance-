public class FixBookControl {
	
	private FixBookUI ui;
	private enum Control_State { INITIALISED, READY, FIXING };
	private Control_State state;
	
	private library library;
	private book currentBook;


	public FixBookControl() {
		this.library = library.INSTANCE();
		state = Control_State.INITIALISED;
	}
	
	
	public void setUi(FixBookUI ui) {
		if (!state.equals(Control_State.INITIALISED)) {
			throw new RuntimeException("FixBookControl: cannot call setUI except in INITIALISED state");
		}	
		this.ui = ui;
		ui.setState(FixBookUI.Ui_State.READY);
		state = Control_State.READY;		
	}


	public void bookScanned(int bookId) {
		if (!state.equals(Control_State.READY)) {
			throw new RuntimeException("FixBookControl: cannot call bookScanned except in READY state");
		}	
		currentBook = library.Book(bookId);
		
		if (currentBook == null) {
			ui.display("Invalid bookId");
			return;
		}
		if (!currentBook.isDamaged()) {
			ui.display("Book has not been damaged");
			return;
		}
		ui.display(currentBook.toString());
		ui.setState(FixBookUI.Ui_State.FIXING);
		state = Control_State.FIXING;		
	}


	public void fixBook(boolean isFixed) {
		if (!state.equals(Control_State.FIXING)) {
			throw new RuntimeException("FixBookControl: cannot call fixBook except in FIXING state");
		}	
		if (isFixed) {
			library.Repair_BOOK(currentBook);
		}
		currentBook = null;
		ui.setState(FixBookUI.Ui_State.READY);
		state = Control_State.READY;		
	}

	
	public void scanComplete() {
		if (!state.equals(Control_State.READY)) {
			throw new RuntimeException("FixBookControl: cannot call scanningComplete except in READY state");
		}	
		ui.setState(FixBookUI.Ui_State.COMPLETED);		
	}






}
