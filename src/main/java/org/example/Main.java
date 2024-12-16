package org.example;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        // Step 1: Initialize DocumentManager
        DocumentManager documentManager = new DocumentManager();

        // Step 2: Create authors
        DocumentManager.Author author1 = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Author author2 = DocumentManager.Author.builder()
                .id("author2")
                .name("Jane Smith")
                .build();

        // Step 3: Create and save documents
        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("Java Basics")
                .content("Learn Java basics and object-oriented programming.")
                .author(author1)
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("Advanced Java")
                .content("Deep dive into advanced Java concepts like streams, lambdas, and concurrency.")
                .author(author2)
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument1 = documentManager.save(document1);
        DocumentManager.Document savedDocument2 = documentManager.save(document2);

        // Confirm saved documents
        System.out.println("Saved Documents:");
        System.out.println("Document 1 ID: " + savedDocument1.getId());
        System.out.println("Document 2 ID: " + savedDocument2.getId());
        System.out.println("-----------------------------------");

        // Step 4: Search for documents by author
        DocumentManager.SearchRequest authorSearchRequest = DocumentManager.SearchRequest.builder()
                .authorIds(Collections.singletonList("author1"))
                .build();

        List<DocumentManager.Document> authorSearchResults = documentManager.search(authorSearchRequest);

        // Print search results for Author 1
        System.out.println("Documents by Author 1:");
        for (DocumentManager.Document doc : authorSearchResults) {
            printDocumentDetails(doc);
        }

        // Step 5: Find a document by ID
        String documentId = savedDocument1.getId();
        Optional<DocumentManager.Document> foundDocument = documentManager.findById(documentId);

        foundDocument.ifPresent(doc -> {
            System.out.println("Found Document by ID (" + documentId + "):");
            printDocumentDetails(doc);
        });

        // Step 6: Search for documents with titles starting with "Java"
        DocumentManager.SearchRequest titleSearchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(Collections.singletonList("Java"))
                .build();

        List<DocumentManager.Document> titleSearchResults = documentManager.search(titleSearchRequest);

        System.out.println("Documents with titles starting with 'Java':");
        for (DocumentManager.Document doc : titleSearchResults) {
            System.out.println("Title: " + doc.getTitle());
        }

        // Step 7: Delete a document
        boolean isDeleted = documentManager.delete(savedDocument1.getId());
        System.out.println("Deleted Document 1: " + isDeleted);

        // Verify deletion
        Optional<DocumentManager.Document> deletedDocument = documentManager.findById(savedDocument1.getId());
        System.out.println("Trying to find deleted document: " + (deletedDocument.isPresent() ? "Found" : "Not Found"));
    }

    /**
     * Utility method to print document details.
     *
     * @param document the document to print
     */
    private static void printDocumentDetails(DocumentManager.Document document) {
        System.out.println("Title: " + document.getTitle());
        System.out.println("Content: " + document.getContent());
        System.out.println("Author: " + document.getAuthor().name());
        System.out.println("Created: " + document.getCreated());
        System.out.println("-----------------------------------");
    }
}
