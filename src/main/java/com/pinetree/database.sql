CREATE TABLE books (
    isbn TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    author TEXT NOT NULL
);

CREATE TABLE copies (
    copy_id INTEGER PRIMARY KEY AUTOINCREMENT,
    isbn TEXT NOT NULL,
    available INTEGER DEFAULT 1,
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

CREATE TABLE users (
    user_id TEXT PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE borrowings (
    borrowing_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id TEXT NOT NULL,
    copy_id INTEGER NOT NULL,
    borrow_date TEXT DEFAULT CURRENT_TIMESTAMP,
    return_date TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (copy_id) REFERENCES copies(copy_id)
);
