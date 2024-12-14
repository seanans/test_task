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
        assertEquals("author1", savedDocument.getAuthor().getId(), "Author ID should match");
    }

    @Test
    void testSaveDocumentWithExistingId() {
        Author author = Author.builder().id("author1").name("John Doe").build();
        Document document1 = Document.builder()
                .id("doc-1")
                .title("Java Basics")
                .content("This is a basic Java document.")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);

        Document document2 = Document.builder()
                .id("doc-1")
                .title("Updated Title")
                .content("This is an updated Java document.")
                .author(author)
                .created(Instant.now())
                .build();

        Document updatedDocument = documentManager.save(document2);

        assertEquals("doc-1", updatedDocument.getId(), "ID should remain the same");
        assertEquals("Updated Title", updatedDocument.getTitle(), "Title should be updated");
        assertEquals("This is an updated Java document.", updatedDocument.getContent(), "Content should be updated");
    }

    @Test
    void testFindById() {
        Author author = Author.builder().id("author1").name("John Doe").build();
        Document document = Document.builder()
                .title("Java Basics")
                .content("This is a basic Java document.")
                .author(author)
                .created(Instant.now())
                .build();

        Document savedDocument = documentManager.save(document);
        Optional<Document> foundDocument = documentManager.findById(savedDocument.getId());

        assertTrue(foundDocument.isPresent(), "Document should be found by ID");
        assertEquals(savedDocument.getId(), foundDocument.get().getId(), "Document IDs should match");
    }

    @Test
    void testFindById_NotFound() {
        Optional<Document> foundDocument = documentManager.findById("nonexistent-id");
        assertFalse(foundDocument.isPresent(), "Document should not be found for nonexistent ID");
    }

    @Test
    void testSearchByTitlePrefix() {
        Author author = Author.builder().id("author1").name("John Doe").build();
        Document document1 = Document.builder()
                .title("Java Basics")
                .content("Learn Java basics.")
                .author(author)
                .created(Instant.now())
                .build();
        Document document2 = Document.builder()
                .title("Advanced Java")
                .content("Learn advanced Java.")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(Collections.singletonList("Java"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(2, results.size(), "Two documents should match the title prefix");
    }

    @Test
    void testSearchByContent() {
        Author author = Author.builder().id("author1").name("John Doe").build();
        Document document1 = Document.builder()
                .title("Java Basics")
                .content("This document covers Java basics.")
                .author(author)
                .created(Instant.now())
                .build();
        Document document2 = Document.builder()
                .title("Advanced Java")
                .content("This document covers advanced Java topics.")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        SearchRequest request = SearchRequest.builder()
                .containsContents(Collections.singletonList("advanced"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size(), "Only one document should match the content search");
        assertEquals("Advanced Java", results.get(0).getTitle(), "Document title should match 'Advanced Java'");
    }

    @Test
    void testSearchByAuthor() {
        Author author1 = Author.builder().id("author1").name("John Doe").build();
        Author author2 = Author.builder().id("author2").name("Jane Smith").build();

        Document document1 = Document.builder()
                .title("Java Basics")
                .content("Learn Java basics.")
                .author(author1)
                .created(Instant.now())
                .build();
        Document document2 = Document.builder()
                .title("Advanced Java")
                .content("Learn advanced Java.")
                .author(author2)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        SearchRequest request = SearchRequest.builder()
                .authorIds(Collections.singletonList("author1"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size(), "Only one document should match the author search");
        assertEquals("Java Basics", results.get(0).getTitle(), "Document title should match 'Java Basics'");
    }

    @Test
    void testSearchByDateRange() {
        Author author = Author.builder().id("author1").name("John Doe").build();

        Document document1 = Document.builder()
                .title("Java Basics")
                .content("Learn Java basics.")
                .author(author)
                .created(Instant.parse("2024-01-01T00:00:00Z"))
                .build();
        Document document2 = Document.builder()
                .title("Advanced Java")
                .content("Learn advanced Java.")
                .author(author)
                .created(Instant.parse("2024-06-01T00:00:00Z"))
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        SearchRequest request = SearchRequest.builder()
                .createdFrom(Instant.parse("2024-01-01T00:00:00Z"))
                .createdTo(Instant.parse("2024-06-01T00:00:00Z"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(2, results.size(), "Both documents should be within the date range");
    }
}
