# String Matching Algorithm Comparison

This project is a Java-based performance comparison of three classical string matching algorithms:

- Brute Force
- Horspool
- Boyer-Moore

The goal of the project is to analyze how different string searching techniques perform on small test cases, large HTML documents, and randomly generated binary files.

---

## Project Overview

String matching is a fundamental problem in computer science. It is used whenever a program needs to find a pattern inside a larger text.

This project compares three different approaches by measuring:

- Number of occurrences found
- Number of character comparisons
- Execution time in milliseconds
- Correctness of detected matches
- Behavior on overlapping patterns
- Performance on large input files

The program also generates highlighted HTML output files to visually show where the searched pattern appears in the original text.

---

## Why This Project Matters

String matching is widely used in real-world systems such as:

- Search engines
- Text editors
- Web browsers
- DNA sequence analysis
- Log file analysis
- Plagiarism detection systems
- Cybersecurity pattern scanning
- Document processing tools

Efficient string matching becomes especially important when working with large files or repeated searches.

---

## Algorithms Implemented

### Brute Force

The Brute Force algorithm checks every possible position in the text and compares the pattern character by character.

It is simple and reliable, but it can become inefficient for large inputs because it may perform many unnecessary comparisons.

---

### Horspool Algorithm

Horspool's algorithm improves performance by comparing characters from right to left and using a bad-symbol shift table.

Instead of moving the pattern by only one position after a mismatch, it can skip multiple characters depending on the mismatch.

---

### Boyer-Moore Algorithm

Boyer-Moore is one of the most efficient classical string matching algorithms.

This implementation uses:

- Bad Symbol Table
- Good Suffix Table

By combining these two shifting rules, Boyer-Moore can skip large parts of the text and reduce the number of character comparisons.

---

## Features

- Implements three string matching algorithms
- Supports large HTML input files
- Supports randomly generated binary files
- Detects overlapping matches
- Counts character comparisons
- Measures execution time using `System.nanoTime()`
- Generates highlighted HTML output files
- Tests short, medium, long, frequent, rare, and repeated patterns
- Prints Boyer-Moore preprocessing tables

---

## Test Cases

The project includes multiple types of test cases:

### Small Test Cases

Used to verify correctness:

- Single match
- Multiple matches
- Overlapping matches
- No match

### Large HTML Files

Used to test performance on real text data:

- Book text files
- Historical article files
- HTML-heavy text

### Random Binary Files

The program generates binary files with random `0` and `1` characters to test algorithm behavior on a small alphabet.

---

## How It Works

The program follows this flow:

```text
Load input file
      |
      v
Select search pattern
      |
      v
Run Brute Force
      |
      v
Run Horspool
      |
      v
Run Boyer-Moore
      |
      v
Compare occurrences, comparisons, and time
      |
      v
Generate highlighted HTML output
```

Each algorithm returns:

- Number of matches
- Match positions
- Number of character comparisons
- Execution time

---

## Project Structure

```text
String-Matching-Algorithm-Comparison/
│
├── StringMatching.java
├── inputs/
│   ├── TestCase1.html
│   ├── TestCase2.html
│   ├── TestCase3.html
│   ├── TestCase4.html
│   ├── BookMoby.html
│   ├── BookWar.html
│   └── WorldWar.html
│
├── README.md
└── .gitignore
```

---

## Technologies Used

- Java
- File Handling
- Data Structures
- Algorithm Analysis
- HTML Output Generation

---

## How to Run

Compile the Java file:

```bash
javac StringMatching.java
```

Run the program:

```bash
java StringMatching
```

The program will execute the predefined test cases and print the performance results to the console.

It will also generate highlighted HTML files showing the matched patterns.

---

## Example Output

```text
Pattern : whale
Length : 5

[Brute-force]
Occurrences: ...
Comparisons: ...
Time: ... ms

[Horspool]
Occurrences: ...
Comparisons: ...
Time: ... ms

[Boyer-Moore]
Occurrences: ...
Comparisons: ...
Time: ... ms
```

---

## Future Improvements

- Add a graphical interface for selecting files and patterns
- Export comparison results as CSV
- Add charts for execution time and comparison counts
- Support user-defined input files from the command line
- Add more string matching algorithms such as KMP and Rabin-Karp

---

## Academic Project

This project was developed as a university project to study and compare string matching algorithms in terms of correctness and performance.
