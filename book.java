import java.io.Serializable;

@SuppressWarnings("serial")
public class Book implements Serializable {

	private String bookTitle;
	private String bookAuthor;
	private String bookCallNumber;
	private int bookId;

	private enum BookState {
		AVAILABLE, ONLOAN, DAMAGED, RESERVED
	};

	private BookState bookState;

	public Book(String newBookAuthor, String newBookTitle, String newBookCallNumber, int newBookId) {
		this.bookAuthor = newBookAuthor;
		this.bookTitle = newBookTitle;
		this.bookCallNumber = newBookCallNumber;
		this.bookId = newBookId;
		this.bookState = BookState.AVAILABLE;
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
		bookStringBuilder.append(bookState);
		return bookStringBuilder.toString();
	}

	public Integer getBookId() {
		return bookId;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public boolean getBookStateAvailable() {
		return bookState == BookState.AVAILABLE;
	}

	public boolean getBookStateOnloan() {
		return bookState == BookState.ONLOAN;
	}

	public boolean getBookStateIsDamaged() {
		return bookState == BookState.DAMAGED;
	}

	public void setBookBorrowed() {
		if (bookState.equals(BookState.AVAILABLE)) {
			bookState = BookState.ONLOAN;
		} else {
			String format = String.format("Book: cannot borrow while book is in state: %s", bookState);
			throw new RuntimeException(format);
		}

	}

	public void setBookReturnedState(boolean DAMAGED) {
		if (bookState.equals(BookState.ONLOAN)) {
			if (DAMAGED) {
				bookState = BookState.DAMAGED;
			} else {
				bookState = BookState.AVAILABLE;
			}
		} else {
			String format = String.format("Book: cannot Return while book is in state: %s", bookState);
			throw new RuntimeException(format);
		}
	}

	public void setBookStateRepair() {
		if (bookState.equals(BookState.DAMAGED)) {
			bookState = BookState.AVAILABLE;
		} else {
			String format = String.format("Book: cannot repair while book is in state: %s", bookState);
			throw new RuntimeException(format);
		}
	}
}
