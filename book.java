import java.io.Serializable;

@SuppressWarnings("serial")
public class book implements Serializable {

	private String bookTitle;
	private String bookAuthor;
	private String callno;
	private int bookId;

	private enum STATE {ISAVAILABLE, ONLOAN, ISDAMAGED, ISRESERVED};

	private STATE bookState;

	public book(String bookAuthor, String bookTitle, String callNo, int bookID) {
		this.bookAuthor = bookAuthor;
		this.bookTitle = bookTitle;
		this.callno = callNo;
		this.bookId = bookID;
		this.bookState = STATE.ISAVAILABLE;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Book: ").append(bookId).append("\n").append("  bookTitle:  ").append(bookTitle).append("\n")
				.append("  bookAuthor: ").append(bookAuthor).append("\n").append("  CallNo: ").append(callno)
				.append("\n").append("  State:  ").append(bookState);

		return sb.toString();
	}

	public Integer getbookId() {
		return bookId;
	}

	public String getbookTitle() {
		return bookTitle;
	}

	public boolean isAvailble() {
		return bookState == STATE.ISAVAILABLE;
	}

	public boolean onLoan() {
		return bookState == STATE.ONLOAN;
	}

	public boolean isDamaged() {
		return bookState == STATE.ISDAMAGED;
	}

	public void isBorrowed() {
		if (bookState.equals(STATE.ISAVAILABLE)) {
			bookState = STATE.ONLOAN;
		} else {
			throw new RuntimeException(String.format("Book: cannot borrow while book is in state: %s", bookState));
		}

	}

	public void bookReturned(boolean isDamaged) {
		if (bookState.equals(STATE.ONLOAN)) {
			if (isDamaged) {
				bookState = STATE.ISDAMAGED;
			} else {
				bookState = STATE.ISAVAILABLE;
			}
		} else {
			throw new RuntimeException(String.format("Book: cannot Return while book is in state: %s", bookState));
		}
	}

	public void Repair() {
		if (bookState.equals(STATE.ISDAMAGED)) {
			bookState = STATE.ISAVAILABLE;
		} else {
			throw new RuntimeException(String.format("Book: cannot repair while book is in state: %s", bookState));
		}
	}
}