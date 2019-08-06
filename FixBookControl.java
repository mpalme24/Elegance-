public class FixBookControl {

	private FixBookUI UI;

	private enum CONTROL_STATE {
		INITIALISED, READY, FIXING
	};

	private CONTROL_STATE state;

	private library library;
	private book curBook;

	public FixBookControl() {
		this.library = library.INSTANCE();
		state = CONTROL_STATE.INITIALISED;
	}

	public void Set_Ui(FixBookUI ui) {
		if (!state.equals(CONTROL_STATE.INITIALISED)) {
			throw new RuntimeException("FixBookControl: cannot call setUI except in INITIALISED state");
		}
		this.UI = ui;
		ui.Set_State(FixBookUI.UI_STATE.READY);
		state = CONTROL_STATE.READY;
	}

	public void scanBook(int bookId) {
		if (!state.equals(CONTROL_STATE.READY)) {
			throw new RuntimeException("FixBookControl: cannot call bookScanned except in READY state");
		}
		curBook = library.Book(bookId);

		if (curBook == null) {
			UI.display("Invalid bookId");
			return;
		}
		if (!curBook.isDamaged()) {
			UI.display("Book has not been damaged");
			return;
		}
		UI.display(curBook.toString());
		UI.Set_State(FixBookUI.UI_STATE.FIXING);
		state = CONTROL_STATE.FIXING;
	}

	public void fixBook(boolean fixBook) {
		if (!state.equals(CONTROL_STATE.FIXING)) {
			throw new RuntimeException("FixBookControl: cannot call fixBook except in FIXING state");
		}
		if (fixBook) {
			library.Repair_BOOK(curBook);
		}
		curBook = null;
		UI.Set_State(FixBookUI.UI_STATE.READY);
		state = CONTROL_STATE.READY;
	}

	public void isBookScaned() {
		if (!state.equals(CONTROL_STATE.READY)) {
			throw new RuntimeException("FixBookControl: cannot call scanningComplete except in READY state");
		}
		UI.Set_State(FixBookUI.UI_STATE.COMPLETED);
	}

}
