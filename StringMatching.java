import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * String Matching Algorithm Comparison Experiment
 * Algorithms: Brute-force, Horspool, Boyer-Moore (Full version)
 */
public class StringMatching {

    public static void main(String[] args) {

        generateRandomBitFile(1, 1);
        generateRandomBitFile(2, 1.5);
        generateRandomBitFile(3, 2);
        
        List<TestCase> testCases = Arrays.asList(
            new TestCase("TestCase1.html","AT_THAT" , "Single match"),
            new TestCase("TestCase2.html","AT_THAT" , "Multiple Matches"),
            new TestCase("TestCase3.html","AAA" , "Overlapping Matches"),
            new TestCase("TestCase4.html","XYZ" , "No Match"),
            new TestCase("BookMoby.html", "whale", "Short, very frequent"),
            new TestCase("BookMoby.html", "white whale", "Medium, rare pattern"),
            new TestCase("BookWar.html", "Prince Andrew", "Long, very frequent"),
            new TestCase("BookWar.html", "nevertheless", "Repeated chars, rare"),
            new TestCase("WorldWar.html", "the Soviet Union", "Long, HTML-heavy text"),
            new TestCase("WorldWar.html", "Nazi Germany", "Medium, forced partial matches"),
            new TestCase("Bits_1.html", "10110", "Short, very frequent"),
            new TestCase("Bits_2.html", "10110", "Same pattern, bigger file"),
            new TestCase("Bits_3.html", "10110", "Same pattern, biggest file"),
            new TestCase("Bits_3.html", "101111100111101000001", "Long, near zero matches"),
            new TestCase("Bits_2.html", "1111100000", "Repeated bits pattern")

        );


        for (int i = 0 ; i < testCases.size() ; i++){
            TestCase testCase = testCases.get(i);
            String inputFilePath = testCase.filename;
            String pattern = testCase.pattern;
            String outputFileName = inputFilePath + "_" + pattern.replace(" ", "_") + "_highlighted.html";

            try {
            // 1. Pre-load the file into memory (Timing starts after this)
            String text = Files.readString(Paths.get(inputFilePath));
            double sizeMB = text.length() / (1024.0 * 1024.0);
            if (sizeMB > 0.1)
            System.out.printf("File Loaded: %.2f MB\n", sizeMB);
        else
            System.out.println("TestCase File Loaded" );
            System.out.println( testCase.description);
            System.out.println("Pattern : " +pattern );
            System.out.println("Length : " + pattern.length() + "\n");

            // 2. Run Brute-force
            SearchResult bfResult = bruteForce(text, pattern);
            bfResult.report();

            // 3. Run Horspool
            SearchResult hResult = horspool(text, pattern);
            hResult.report();

            // 4. Run Boyer-Moore
            BoyerMoore bm = new BoyerMoore(pattern);
            SearchResult bmResult = bm.search(text);
            bmResult.report();
            bm.printTables();

            // 5. Generate Highlighted Output (Using Boyer-Moore results)
            generateHighlightedHTML(text, pattern, bmResult.positions, outputFileName);
            System.out.println("SUCCESS: Output generated in " + outputFileName);

        } catch (IOException e) {
            System.err.println("Error: Could not find " + inputFilePath + ". Ensure the file is in the project directory.");
        }

        }
        


    }

    // ==========================================
    // 1. BRUTE-FORCE ALGORITHM (Find All/Overlaps)
    // ==========================================
    public static SearchResult bruteForce(String text, String pattern) {
        SearchResult res = new SearchResult("Brute-force");
        int n = text.length();
        int m = pattern.length();

        long startTime = System.nanoTime();
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m) {
                res.comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }
            if (j == m) {
                res.occurrences++;
                res.positions.add(i);
            }
        }
        res.timeMs = (System.nanoTime() - startTime) / 1_000_000.0;
        return res;
    }

    // ==========================================
    // 2. HORSPOOL'S ALGORITHM (Find All/Overlaps)
    // ==========================================
    public static SearchResult horspool(String text, String pattern) {
        SearchResult res = new SearchResult("Horspool");
        int n = text.length();
        int m = pattern.length();
        int[] shiftTable = new int[65536]; // Support Unicode

        // Pre-processing
        Arrays.fill(shiftTable, m);
        for (int j = 0; j < m - 1; j++) shiftTable[pattern.charAt(j)] = m - 1 - j;

        long startTime = System.nanoTime();
        int i = m - 1;
        while (i < n) {
            int k = 0;
            while (k < m) {
                res.comparisons++;
                if (text.charAt(i - k) != pattern.charAt(m - 1 - k)) {
                    break;
                }
                k++;
            }
            if (k == m) {
                res.occurrences++;
                res.positions.add(i - m + 1);
            }
            // Move pointer based on last character in window
            i += shiftTable[text.charAt(i)];
        }
        res.timeMs = (System.nanoTime() - startTime) / 1_000_000.0;
        return res;
    }

    // ==========================================
    // 3. BOYER-MOORE ALGORITHM (Lecture Version)
    // ==========================================
    static class BoyerMoore {
        private String p;
        private int m;
        private int[] d1; // Bad Symbol
        private int[] d2; // Good Suffix

        public BoyerMoore(String pattern) {
            this.p = pattern;
            this.m = pattern.length();
            this.d1 = new int[65536];
            this.d2 = new int[m];
            preProcess();
        }

        private void preProcess() {
            // Bad Symbol Table (d1)
            Arrays.fill(d1, m);
            for (int i = 0; i < m - 1; i++) {
                d1[p.charAt(i)] = m - 1 - i;
            }

            // Good Suffix Table (d2)
            int[] suffix = new int[m];
            computeSuffixes(suffix);
            
            Arrays.fill(d2, m);
            int j = 0;
            for (int i = m - 1; i >= 0; i--) {
                if (suffix[i] == i + 1) {
                    for (; j < m - 1 - i; j++) {
                        if (d2[j] == m) d2[j] = m - 1 - i;
                    }
                }
            }
            for (int i = 0; i <= m - 2; i++) {
                d2[m - 1 - suffix[i]] = m - 1 - i;
            }
        }

        private void computeSuffixes(int[] suf) {
    // The last position always matches the whole pattern length
    suf[m - 1] = m; 
    
    // Check every index from the second-to-last down to 0
    for (int i = m - 2; i >= 0; i--) {
        int matchCount = 0;
        
        // Count how many characters match, moving backwards from i
        // and comparing to the end of the pattern (m - 1)
        while (matchCount <= i && 
               p.charAt(i - matchCount) == p.charAt(m - 1 - matchCount)) {
            matchCount++;
        }
        
        // Save the total count of matches for this suffix
        suf[i] = matchCount;
    }
}

        public SearchResult search(String text) {
            SearchResult res = new SearchResult("Boyer-Moore");
            int n = text.length();
            long startTime = System.nanoTime();

            int i = 0;
            while (i <= n - m) {
                int j = m - 1;
                while (j >= 0) {
                    res.comparisons++;
                    if (p.charAt(j) != text.charAt(i + j)) break;
                    j--;
                }
                if (j < 0) {
                    res.occurrences++;
                    res.positions.add(i);
                    i += d2[0]; // Shift for overlap
                } else {
                    i += Math.max(d2[j], d1[text.charAt(i + j)] - (m - 1 - j));
                }
            }
            res.timeMs = (System.nanoTime() - startTime) / 1_000_000.0;
            return res;
        }

        public void printTables() {
    System.out.println("\n============================");
    System.out.println("     BOYER-MOORE TABLES");
    System.out.println("============================");
    System.out.println("---------------------------");

    // 1. Format the Bad Symbol Table (d1)
    System.out.println("[ Bad Symbol Table (d1) ]");
    System.out.println("  Char  | Shift ");
    System.out.println("--------+-------");
    
    
    boolean[] printed = new boolean[65536];
    for (int i = 0; i <= m - 1; i++) { 
        char c = p.charAt(i);
        if (!printed[c]) {
            // %5s and %5d are used to pad with spaces so the columns line up perfectly
            System.out.printf("  %4c  | %4d \n", c, d1[c]);
            printed[c] = true;
        }
    }
    // Print the fallback rule for the rest of the alphabet
    System.out.printf(" Others | %4d \n", m); 
    System.out.println("---------------------------");

    // 2. Format the Good Suffix Table (d2)
    System.out.println("[ Good Suffix Table (d2) ]");
    System.out.println(" Index  | Shift ");
    System.out.println("--------+-------");
    for (int i = 0; i < m; i++) {
        System.out.printf("  %4d  | %4d \n", i, d2[i]);
    }
    System.out.println("============================\n");
}
    }

    // ==========================================
    // 4. UTILITY: RESULTS & HIGHLIGHTING
    // ==========================================
    static class SearchResult {
        String name;
        int occurrences = 0;
        long comparisons = 0;
        double timeMs = 0;
        List<Integer> positions = new ArrayList<>();

        SearchResult(String name) { this.name = name; }

        void report() {
            System.out.printf("[%s]\nOccurrences: %d\nComparisons: %d\nTime: %.4f ms\n\n", 
                               name, occurrences, comparisons, timeMs);
        }
    }
    
    public static void generateRandomBitFile(int fileOrder, double mbSize) {
        String fileName = "Bits_" + fileOrder + ".html";
        long targetBytes = (long) (mbSize * 1024L * 1024L); // Convert MB to actual Bytes

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("<HTML><BODY>\n");
            long bytesWritten = 13; // Counting the characters we just wrote

            Random random = new Random(42);
            int chunkSize = 10000;
            char[] chunk = new char[chunkSize];

            

            while (bytesWritten < targetBytes) {
                // If we are close to the end, only write the exact amount needed
                if (targetBytes - bytesWritten < chunkSize) {
                    chunkSize = (int) (targetBytes - bytesWritten);
                    chunk = new char[chunkSize];
                }
                
                for (int i = 0; i < chunkSize; i++) {
                    chunk[i] = random.nextBoolean() ? '1' : '0';
                }
                writer.write(chunk);
                bytesWritten += chunkSize;
            }

            writer.write("\n</BODY></HTML>");
            System.out.println("Done creating " + fileName + "!");

        } catch (IOException e) {
            System.err.println("Error creating " + fileName + ": " + e.getMessage());
        }
    }

    public static void generateHighlightedHTML(String text, String pattern, List<Integer> positions, String filename) throws IOException {
    int n = text.length();
    int m = pattern.length();
    
    // 1. Create an array to track which characters should be highlighted
    boolean[] highlight = new boolean[n];

    // 2. "Paint" all characters that belong to ANY match
    for (int pos : positions) {
        for (int i = 0; i < m; i++) {
            if (pos + i < n) {
                highlight[pos + i] = true;
            }
        }
    }

    // 3. Build the final HTML string
    StringBuilder sb = new StringBuilder();
    boolean currentlyHighlighting = false;

    for (int i = 0; i < n; i++) {
        // If this character should be highlighted, and we aren't already highlighting, open the tag
        if (highlight[i] && !currentlyHighlighting) {
            sb.append("<mark style='background-color: yellow;'>");
            currentlyHighlighting = true;
        } 
        // If this character should NOT be highlighted, but we ARE highlighting, close the tag
        else if (!highlight[i] && currentlyHighlighting) {
            sb.append("</mark>");
            currentlyHighlighting = false;
        }
        
        // Append the actual character
        sb.append(text.charAt(i));
    }

    // 4. Safely close the tag if the string ends while we are still highlighting
    if (currentlyHighlighting) {
        sb.append("</mark>");
    }

    Files.writeString(Path.of(filename), sb.toString());
}
static class TestCase {
        String filename;
        String pattern;
        String description;

        TestCase(String filename, String pattern, String description) {
            this.filename = filename;
            this.pattern = pattern;
            this.description = description;
        }
    }
}
