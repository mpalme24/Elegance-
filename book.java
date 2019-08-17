import java.io.Serializable;

@SuppressWarnings("serial")
public class book implements Serializable {

	private String bookTitle;
	private String bookAuthor;
	private String bookCallNumber;
	private int bookId;

	private enum BookState {
		AVAILABLE, ONLOAN, DAMAGED, RESERVED
	};

	private BookState State;

	public book(String author, String title, String callNumber, int id) {
		this.bookAuthor = author;
		this.bookTitle = title;
		this.bookCallNumber = callNumber;
		this.bookId = id;
		this.State = BookState.AVAILABLE;
	}

	public String toString() {
		StringBuilder bookStringBuilder = new StringBuilder();
		bookStringBuilder.append("Book: ");
		bookStringBuilder.append(bookId);
		bookStringBuilder.append("\n");
		bookStringBuilder.append("  Title:  ");
		bookStringBuilder.append(bookTitle);
		bookStringBuilder.append("\n");
		bookStringBuilder.append("  Author: ");
		bookStringBuilder.append(bookAuthor);
		bookStringBuilder.append("\n");
		bookStringBuilder.append("  CallNo: ");
		bookStringBuilder.append(bookCallNumber);
		bookStringBuilder.append("\n");
		bookStringBuilder.append("  State:  ");
		bookStringBuilder.append(State);
		return bookStringBuilder.toString();
	}

	public Integer getBookId() {
		return bookId;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public boolean getBookStateAvailable() {
		return State == BookState.AVAILABLE;
	}

	public boolean getBookStateOnloan() {
		return State == BookState.ONLOAN;
	}

	public boolean getBookStateIsDamaged() {
		return State == BookState.DAMAGED;
	}

	public void setBookBorrowed() {
		if (State.equals(BookState.AVAILABLE)) {
			State = BookState.ONLOAN;
		} else {
			String format = String.format("Book: cannot borrow while book is in state: %s", State);
			throw new RuntimeException(format);
		}
	}

	public void setBookReturnedState(boolean DAMAGED) {
		if (State.equals(BookState.ONLOAN)) {
			if (DAMAGED) {
				State = BookState.DAMAGED;
			} else {
				State = BookState.AVAILABLE;
			}
		} else {
			String format = String.format("Book: cannot Return while book is in state: %s", State);
			throw new RuntimeException(format);
		}
	}

	public void setBookStateRepair() {
		if (State.equals(BookState.DAMAGED)) {
			State = BookState.AVAILABLE;
		} else {
			String format = String.format("Book: cannot repair while book is in state: %s", State);
			throw new RuntimeException(format);
		}
	}
}
