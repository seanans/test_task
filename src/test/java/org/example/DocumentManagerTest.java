package org.example;

import org.example.DocumentManager.Author;
import org.example.DocumentManager.Document;
import org.example.DocumentManager.SearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        // Initialize DocumentManager before each test
        documentManager = new DocumentManager();
    }

    @Test
    void testSaveNewDocument() {
        Author author = Author.builder().id("author1").name("John Doe").build();
        Document document = Document.builder()
                .title("Java Basics")
                .content("This is a basic Java document.")
                .author(author)
                .created(Instant.now())
                .build();

        Document savedDocument = documentManager.save(document);
        assertNotNull(savedDocument.getId(), "Document ID should not be null");
        assertEquals("Java Basics", savedDocument.getTitle(), "Title should match");
        assertEquals("This is a basic Java document.", savedDocument.getContent(), "Content should match");
        assertEquals("author1", savedDocument.getAuthor().id(), "Author ID should match");
    }

    @Test
    void testFindById_NotFound() {
        Optional<Document> foundDocument = documentManager.findById("nonexistent-id");
        assertFalse(foundDocument.isPresent(), "Document should not be found for nonexistent ID");
    }

    @Test
    void testSearchWithNullCriteria() {
        // Setup
        Author author = Author.builder().id("author1").name("John Doe").build();
        Document document1 = Document.builder()
                .title("Java Basics")
                .content("Learn Java basics.")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);

        // Search with all null criteria
        SearchRequest request = SearchRequest.builder().build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size(), "All documents should be returned when no criteria are specified");
        assertEquals("Java Basics", results.get(0).getTitle(), "Document title should match");
    }

    @Test
    void testSearchWithSomeNullCriteria() {
        // Setup
        Author author1 = Author.builder().id("author1").name("John Doe").build();
        Author author2 = Author.builder().id("author2").name("Jane Doe").build();

        Document document1 = Document.builder()
                .title("Java Basics")
                .content("Learn Java basics.")
                .author(author1)
                .created(Instant.parse("2024-01-01T00:00:00Z"))
                .build();
        Document document2 = Document.builder()
                .title("Advanced Java")
                .content("Learn advanced Java topics.")
                .author(author2)
                .created(Instant.parse("2024-06-01T00:00:00Z"))
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        // Search with title prefix only, others null
        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(Collections.singletonList("Java"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(2, results.size(), "Two documents should match the title prefix");
    }

    @Test
    void testSearchByTitlePrefixAndNullContent() {
        // Setup
        Author author = Author.builder().id("author1").name("John Doe").build();
        Document document1 = Document.builder()
                .title("Java Basics")
                .content("Learn Java basics.")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);

        // Search with title prefix and null content
        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(Collections.singletonList("Java"))
                .containsContents(null)
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size(), "Document should match the title prefix");
        assertEquals("Java Basics", results.get(0).getTitle(), "Document title should match");
    }

    @Test
    void testSearchWithMultipleCriteriaIncludingNull() {
        // Setup
        Author author1 = Author.builder().id("author1").name("John Doe").build();
        Author author2 = Author.builder().id("author2").name("Jane Doe").build();

        Document document1 = Document.builder()
                .title("Java Basics")
                .content("Learn Java basics.")
                .author(author1)
                .created(Instant.parse("2024-01-01T00:00:00Z"))
                .build();

        Document document2 = Document.builder()
                .title("Advanced Java")
                .content("Learn advanced Java topics.")
                .author(author2)
                .created(Instant.parse("2024-06-01T00:00:00Z"))
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        // Search with specific title prefix and date range but null content and author
        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(Collections.singletonList("Advanced"))
                .createdFrom(Instant.parse("2024-01-01T00:00:00Z"))
                .createdTo(Instant.parse("2024-12-31T00:00:00Z"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size(), "Only one document should match the combined criteria");
        assertEquals("Advanced Java", results.get(0).getTitle(), "Document title should match 'Advanced Java'");
    }
}
