package org.example;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc.
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> documentStorage = new ConcurrentHashMap<>(); //ConcurrentHashMap will help in future

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        validateDocument(document); // added fields validation

        if (document.getId() == null || document.getId().isEmpty()) {
            String newId = UUID.randomUUID().toString();
            document = Document.builder()
                    .id(newId)
                    .title(document.getTitle())
                    .content(document.getContent())
                    .author(document.getAuthor())
                    .created(document.getCreated())
                    .build();
        } else {
            Document existingDocument = documentStorage.get(document.getId());
            if (existingDocument != null) {
                document = Document.builder()
                        .id(existingDocument.getId())
                        .title(document.getTitle())
                        .content(document.getContent())
                        .author(document.getAuthor())
                        .created(existingDocument.getCreated())
                        .build();
            }
        }

        documentStorage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation of this method should find documents which match with request
     * -----
     * Method checks every element of storage to match criteria
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documentStorage.values().stream()
                .filter(document -> matchesWithCriteria(document, request))
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {

        return Optional.ofNullable(documentStorage.get(id));
    }

    /**
     * Deletes a document by id
     *
     * @param id - document id
     * @return boolean
     */
    public boolean delete(String id) {
        return documentStorage.remove(id) != null;
    }

    /**
     *
     * @param document - document content and author data
     * @param request - search request, each field could be null
     * @return - boolean
     */
    private boolean matchesWithCriteria(Document document, SearchRequest request) {
        if (request.getTitlePrefixes() != null && request.getTitlePrefixes().stream()
                .noneMatch(prefix -> document.getTitle().contains(prefix))) {
            return false;
        }

        if (request.getContainsContents() != null && request.getContainsContents().stream()
                .noneMatch(content -> document.getContent().contains(content))) {
            return false;
        }

        if (request.getAuthorIds() != null && request.getAuthorIds().stream()
                .noneMatch(authorId -> document.getAuthor().id().equals(authorId))) {
            return false;
        }

        if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
            return false;
        }

        if (request.getCreatedTo() != null && document.getCreated().isAfter(request.getCreatedTo())) {
            return false;
        }

        return true;
    }

    /**
     * Validates fields of document not to be null or empty
     *
     * @param document - document content and author data
     */
    private void validateDocument(Document document) {
        if (document.getTitle() == null || document.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (document.getContent() == null || document.getContent().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (document.getAuthor() == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        if (document.getAuthor().id() == null || document.getAuthor().id().isEmpty()) {
            throw new IllegalArgumentException("Author ID cannot be null or empty");
        }
        if (document.getCreated() == null) {
            throw new IllegalArgumentException("Created timestamp cannot be null");
        }
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    // Changed to be immutable
    @Getter
    public static class Document {
        private final String id;
        private final String title;
        private final String content;
        private final Author author;
        private final Instant created;

        @Builder
        private Document(String id, String title, String content, Author author, Instant created) {
            // ID validation happens only if it's not null
            if (id != null && id.isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
            this.id = id;
            this.title = Objects.requireNonNull(title, "Title cannot be null");
            this.content = Objects.requireNonNull(content, "Content cannot be null");
            this.author = Objects.requireNonNull(author, "Author cannot be null");
            this.created = Objects.requireNonNull(created, "Created date cannot be null");
        }
    }

    // Changed to be immutable
    public record Author(String id, String name) {
        @Builder
        public Author {
            if (id == null || id.isEmpty()) {
                throw new IllegalArgumentException("Author ID cannot be null or empty");
            }
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Author name cannot be null or empty");
            }
        }

    }
}