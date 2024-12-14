package org.example;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        // Create an instance of DocumentManager
        DocumentManager documentManager = new DocumentManager();

        // Create some authors
        DocumentManager.Author author1 = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();
        DocumentManager.Author author2 = DocumentManager.Author.builder()
                .id("author2")
                .name("Jane Smith")
                .build();

        // Create some documents
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

        // Save documents
        documentManager.save(document1);
        documentManager.save(document2);

        // Search for documents by author
        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .authorIds(java.util.Collections.singletonList("author1"))
                .build();

        java.util.List<DocumentManager.Document> searchResults = documentManager.search(searchRequest);

        // Print out the search results
        System.out.println("Documents by Author 1:");
        for (DocumentManager.Document doc : searchResults) {
            System.out.println("Title: " + doc.getTitle());
            System.out.println("Content: " + doc.getContent());
            System.out.println("Author: " + doc.getAuthor().getName());
            System.out.println("Created: " + doc.getCreated());
            System.out.println("-----------------------------------");
        }

        // Find a document by ID
        String documentId = document1.getId();
        documentManager.findById(documentId)
                .ifPresent(doc -> {
                    System.out.println("Found document by ID (" + documentId + "):");
                    System.out.println("Title: " + doc.getTitle());
                    System.out.println("Content: " + doc.getContent());
                    System.out.println("Author: " + doc.getAuthor().getName());
                    System.out.println("Created: " + doc.getCreated());
                });

        // Demonstrate a search by title prefix
        DocumentManager.SearchRequest titleSearchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(java.util.Collections.singletonList("Java"))
                .build();

        java.util.List<DocumentManager.Document> titleSearchResults = documentManager.search(titleSearchRequest);

        System.out.println("Documents with titles starting with 'Java':");
        for (DocumentManager.Document doc : titleSearchResults) {
            System.out.println("Title: " + doc.getTitle());
        }
    }
}
